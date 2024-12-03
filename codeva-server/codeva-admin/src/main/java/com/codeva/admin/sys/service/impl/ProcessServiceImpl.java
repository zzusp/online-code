package com.codeva.admin.sys.service.impl;

import com.alibaba.compileflow.engine.BpmnProcessEngineFactory;
import com.alibaba.compileflow.engine.process.impl.BpmnStringProcessEngineImpl;
import com.alibaba.compileflow.engine.process.preruntime.compiler.impl.BpmnFlowClassLoader;
import com.alibaba.compileflow.extension.executor.JavaExecutor;
import com.alibaba.compileflow.extension.util.FlowUtils;
import com.alibaba.compileflow.extension.util.VarUtils;
import com.codeva.admin.constant.RedisKey;
import com.codeva.admin.enums.IdKeyEnum;
import com.codeva.admin.exception.BusinessException;
import com.codeva.admin.exception.ForbiddenException;
import com.codeva.admin.sys.dao.ProcessMapper;
import com.codeva.admin.sys.dao.ProcessTaskMapper;
import com.codeva.admin.sys.model.SysProcess;
import com.codeva.admin.sys.model.SysProcessTask;
import com.codeva.admin.sys.service.AuthService;
import com.codeva.admin.sys.service.ProcessService;
import com.codeva.admin.sys.service.RedisCacheService;
import com.codeva.admin.sys.service.RedisLockService;
import com.codeva.admin.web.R;
import com.codeva.admin.web.page.PageParam;
import com.codeva.admin.web.page.PageTable;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageInterceptor;
import com.sankuai.inf.leaf.IDGen;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import javax.tools.JavaFileObject;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author 孙鹏
 * @description 流程服务
 * @date Created in 17:57 2023/2/24
 * @modified By
 */
@Service
public class ProcessServiceImpl implements ProcessService {

    private static final Logger log = LoggerFactory.getLogger(ProcessServiceImpl.class);

    private final SqlSessionFactory sqlSessionFactory;
    private final IDGen idGen;
    private final RedisLockService redisLockService;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final RedisCacheService redisCacheService;
    private final AuthService authService;

    public ProcessServiceImpl(DataSource dataSource, IDGen idGen, PageInterceptor pageInterceptor,
                              RedisLockService redisLockService,
                              RedisTemplate<Object, Object> redisTemplate,
                              RedisCacheService redisCacheService, AuthService authService) {
        this.idGen = idGen;
        this.redisLockService = redisLockService;
        this.redisTemplate = redisTemplate;
        this.redisCacheService = redisCacheService;
        this.authService = authService;
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(ProcessMapper.class);
        configuration.addMapper(ProcessTaskMapper.class);
        configuration.addInterceptor(pageInterceptor);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    @Override
    public R<PageTable> list(PageParam<SysProcess> pageParam) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            if (pageParam.getPageNum() > 0 && pageParam.getPageSize() > 0) {
                PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
            }
            if (pageParam.getParam() == null) {
                pageParam.setParam(new SysProcess());
            }
            String menuCode = pageParam.getParam().getMenuCode();
            String procCode = pageParam.getParam().getProcCode();
            String procName = pageParam.getParam().getProcName();
            List<SysProcess> list = sqlSession.getMapper(ProcessMapper.class).getAllProcess(menuCode, procCode, procName);
            PageInfo<SysProcess> pageInfo = new PageInfo<>(list);
            return R.ok(PageTable.page(pageInfo.getTotal(), pageInfo.getList()));
        }
    }

    @Override
    public List<SysProcess> listAll() {
        return redisCacheService.cacheList(RedisKey.ALL_PROC_CACHE, (data) -> {
            // 缓存未找到，查询数据库
            try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
                return sqlSession.getMapper(ProcessMapper.class).getAllProcess(null, null, null);
            }
        }, SysProcess.class, 10, 24 * 60 * 60, TimeUnit.SECONDS);
    }

    @Override
    public SysProcess getById(long id) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            return sqlSession.getMapper(ProcessMapper.class).getById(id);
        }
    }

    @Override
    public SysProcess getInfoWithTaskById(long id) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            SysProcess process = sqlSession.getMapper(ProcessMapper.class).getById(id);
            if (process != null) {
                process.setTasks(sqlSession.getMapper(ProcessTaskMapper.class).getByProcCode(process.getProcCode()));
            }
            return process;
        }
    }

    @Override
    public void save(SysProcess process) {
        String procCode = process.getProcCode();
        if (StringUtils.isEmpty(procCode)) {
            throw new BusinessException("编码不可为空");
        }
        JavaExecutor executor = new JavaExecutor();
        // 分布式业务锁
        redisLockService.lock(RedisKey.getProcCacheKey(procCode), v -> {
            try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
                // 业务处理
                process.setBpmn(FlowUtils.replaceProcCode(process.getBpmn(), procCode));
                if (process.getId() == null) {
                    if (sqlSession.getMapper(ProcessMapper.class).getByProcCode(procCode) != null) {
                        throw new BusinessException("编码已存在");
                    }
                    process.setId(idGen.get(IdKeyEnum.SYS_PROCESS.getCode()).getId());
                    process.setCreateTime(LocalDateTime.now());
                    sqlSession.getMapper(ProcessMapper.class).insert(process);
                } else {
                    process.setUpdateTime(LocalDateTime.now());
                    sqlSession.getMapper(ProcessMapper.class).update(process);
                }
                if (process.getTasks() != null && !process.getTasks().isEmpty()) {
                    sqlSession.getMapper(ProcessTaskMapper.class).deleteByProcCode(procCode);
                    for (SysProcessTask task : process.getTasks()) {
                        if (task.getId() == null) {
                            task.setId(idGen.get(IdKeyEnum.SYS_PROCESS.getCode()).getId());
                        }
                        // 编译检查
                        if (StringUtils.isNoneBlank(task.getExecuteCmd())) {
                            executor.compiler(task.getExecuteCmd());
                        }
                    }
                    sqlSession.getMapper(ProcessTaskMapper.class).insertBatch(process.getTasks());
                }
                sqlSession.commit();
                // 删除缓存
                redisTemplate.delete(RedisKey.getProcCacheKey(procCode));
                redisTemplate.delete(RedisKey.ALL_PROC_CACHE);
            } catch (Exception e) {
                log.error("保存流程失败，错误信息：{}", e.getMessage(), e);
                throw new BusinessException("保存流程失败，错误信息：" + e.getMessage());
            }
        }, 10, TimeUnit.SECONDS);
    }

    @Override
    public void copy(SysProcess process) {
        if (StringUtils.isEmpty(process.getCopyProcCode())) {
            throw new BusinessException("请选择一个流程用于拷贝");
        }
        String procCode = process.getProcCode();
        if (StringUtils.isEmpty(procCode)) {
            throw new BusinessException("编码不可为空");
        }
        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            String fromCode = process.getCopyProcCode();
            SysProcess from;
            if ((from = sqlSession.getMapper(ProcessMapper.class).getByProcCode(fromCode)) == null) {
                throw new BusinessException("拷贝来源流程不存在");
            }
            if (sqlSession.getMapper(ProcessMapper.class).getByProcCode(procCode) != null) {
                throw new BusinessException("编码已存在");
            }
            process.setId(idGen.get(IdKeyEnum.SYS_PROCESS.getCode()).getId());
            process.setCreateTime(LocalDateTime.now());
            process.setBpmn(FlowUtils.replaceProcCode(from.getBpmn(), procCode));
            // 入库
            sqlSession.getMapper(ProcessMapper.class).insert(process);
            // 拷贝节点
            List<SysProcessTask> tasks = sqlSession.getMapper(ProcessTaskMapper.class).getByProcCode(fromCode);
            if (tasks != null && !tasks.isEmpty()) {
                for (SysProcessTask task : tasks) {
                    task.setId(idGen.get(IdKeyEnum.SYS_PROCESS.getCode()).getId());
                    task.setProcCode(procCode);
                }
                // 节点入库
                sqlSession.getMapper(ProcessTaskMapper.class).insertBatch(tasks);
            }
            sqlSession.commit();
            // 删除缓存
            redisTemplate.delete(RedisKey.ALL_PROC_CACHE);
        }
    }

    @Override
    public void delete(Long id) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            SysProcess process = sqlSession.getMapper(ProcessMapper.class).getById(id);
            if (process == null) {
                return;
            }
            String procCode = process.getProcCode();
            // 分布式业务锁
            redisLockService.lock(RedisKey.getProcCacheKey(procCode), v -> {
                try {
                    // 业务操作
                    sqlSession.getMapper(ProcessMapper.class).delete(id);
                    sqlSession.getMapper(ProcessTaskMapper.class).deleteByProcCode(procCode);
                    sqlSession.commit();
                    // 删除缓存
                    redisTemplate.delete(RedisKey.getProcCacheKey(procCode));
                    redisTemplate.delete(RedisKey.ALL_PROC_CACHE);
                } catch (Exception e) {
                    log.error("删除流程失败，错误信息：{}", e.getMessage(), e);
                }
            }, 10, TimeUnit.SECONDS);
        }
    }

    @Override
    public Object run(String code, Map<String, Object> params) {
        return this.run(code, true, params);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object run(String code, boolean checkPermission, Map<String, Object> params) {
        StopWatch sw = new StopWatch();
        sw.start("接收到请求");
        if (StringUtils.isEmpty(code)) {
            throw new BusinessException("流程编码不可为空");
        }
        sw.stop();
        sw.start("查询权限缓存");
        List<SysProcess> list = this.listAll();
        sw.stop();
        sw.start("校验权限");
        if (checkPermission && !authService.checkProcessPermission(code, list)) {
            throw new ForbiddenException();
        }
        if (params == null) {
            params = new HashMap<>(8);
        }
        sw.stop();
        sw.start("查询process");
        SysProcess process = this.getByProcCode(code);
        if (process == null) {
            throw new BusinessException("未找到流程：" + code);
        }
        Map<String, Object> flowVars = new HashMap<>(32);
        if (!CollectionUtils.isEmpty(process.getTasks())) {
            for (SysProcessTask task : process.getTasks()) {
                flowVars.put(task.getTaskCode(), task.getExecuteCmd());
            }
        }
        sw.stop();
        sw.start("开始执行");
        // code在bpm文件中定义
        BpmnStringProcessEngineImpl processEngine = BpmnProcessEngineFactory.getProcessEngine();
        processEngine.setFlowString(FlowUtils.getFlowStr(process.getBpmn()));
        Map<String, Object> param = new HashMap<>(8);
        param.put("flowVars", flowVars);
        param.put("vars", params);
        param.put("varUtil", new VarUtils(params));
        Map<String, Object> flowResult = (Map<String, Object>) processEngine.execute(code, param).get("result");
        sw.stop();
        sw.start("返回结果");
        if (flowResult == null || !flowResult.containsKey("flowRes")) {
            return null;
        }
        sw.stop();
        log.info(sw.prettyPrint());
        return flowResult.get("flowRes");
    }

    @Override
    public Object runTask(String procCode, String taskCode, Map<String, Object> params) {
        SysProcessTask task;
        // 查询节点
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            task = sqlSession.getMapper(ProcessTaskMapper.class).getByProcCodeAndTaskCode(procCode, taskCode);
            if (task == null) {
                throw new BusinessException("未找到指定的节点");
            }
        }
        String cmd = task.getExecuteCmd();
        if (StringUtils.isBlank(cmd)) {
            throw new BusinessException("节点未配置代码");
        }
        JavaExecutor executor = new JavaExecutor();
        try {
            return executor.execute(cmd, params);
        } catch (Exception e) {
            throw new BusinessException("节点执行失败，错误信息" + e.getMessage(), e.getCause());
        }
    }

    @Override
    public Object runCmd(String cmd, Map<String, Object> params) {
        return new JavaExecutor().execute(cmd, params);
    }

    @Override
    public Object autocomplete() {
        List<String> list = new ArrayList<>();
        for (URL url : BpmnFlowClassLoader.getInstance().getURLs()) {
            String ext_form = url.toExternalForm();
//            String jar = ext_form.substring(0, ext_form.lastIndexOf("!"));
//            String pkg = ext_form.substring(ext_form.lastIndexOf("!") + 1);
            String pkg = ext_form;
            try {
                if (ext_form.startsWith("file:/")) {
//                    FileURLConnection conn = (FileURLConnection) url.openConnection();
//                    conn.connect();
                    if (ext_form.endsWith(".jar") && !ext_form.endsWith("idea_rt.jar")) {
                        Enumeration<JarEntry> jarItems = new JarFile(new File(ext_form.replace("file:/", ""))).entries();
                        while (jarItems.hasMoreElements()) {
                            JarEntry item = jarItems.nextElement();
                            if (item.isDirectory() || (!item.getName().endsWith(".class")) || item.getName().contains("$")) {
                                continue;
                            }
                            String name = item.getName();
                            String binaryName = name.replaceAll("/", ".");
                            binaryName = binaryName.substring(0, binaryName.indexOf(JavaFileObject.Kind.CLASS.extension));
                            list.add(binaryName);
                        }
                    } else if (ext_form.endsWith("/classes/")) {
                        readFile(new File(ext_form.replace("file:/", "")), list);
                    }
                }
            } catch (Exception e) {
                throw new BusinessException("自动补全异常，错误信息" + e.getMessage(), e.getCause());
            }
        }
        return list;
    }

    private void readFile(File file, List<String> classNameList) {
        if (!file.isDirectory() && file.getName().endsWith(".class")) {
            classNameList.add(file.getPath().split("\\\\classes\\\\")[1]
                    .replaceAll("\\\\", ".").replaceAll(".class", ""));
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    readFile(f, classNameList);
                }
            }
        }
    }

    /**
     * 根据流程编码查询流程及节点信息
     *
     * @param code
     * @return
     */
    private SysProcess getByProcCode(String code) {
        return redisCacheService.cache(RedisKey.getProcCacheKey(code), (data) -> {
            // 缓存未找到，查询数据库
            try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
                data = sqlSession.getMapper(ProcessMapper.class).getByProcCode(code);
                if (data != null) {
                    data.setTasks(sqlSession.getMapper(ProcessTaskMapper.class).getByProcCode(code));
                }
            }
            return data;
        }, SysProcess.class, 10, 24 * 60 * 60, TimeUnit.SECONDS);
    }

}

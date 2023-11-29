package com.onlinecode.admin.process.service.impl;

import com.alibaba.compileflow.engine.BpmnProcessEngineFactory;
import com.alibaba.compileflow.engine.process.impl.BpmnStringProcessEngineImpl;
import com.alibaba.compileflow.engine.process.preruntime.compiler.impl.BpmnFlowClassLoader;
import com.alibaba.compileflow.extension.executor.JavaExecutor;
import com.alibaba.compileflow.extension.util.FlowUtils;
import com.alibaba.compileflow.extension.util.VarUtils;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageInterceptor;
import com.onlinecode.admin.constant.FlowConstants;
import com.onlinecode.admin.exception.BusinessException;
import com.onlinecode.admin.process.dao.ProcessMapper;
import com.onlinecode.admin.process.dao.ProcessTaskMapper;
import com.onlinecode.admin.process.model.SysProcess;
import com.onlinecode.admin.process.model.SysProcessTask;
import com.onlinecode.admin.process.service.ProcessService;
import com.onlinecode.admin.web.R;
import com.onlinecode.admin.web.page.PageParam;
import com.onlinecode.admin.web.page.PageTable;
import com.sankuai.inf.leaf.IDGen;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sun.net.www.protocol.file.FileURLConnection;

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

    private static final String LOCK_KEY = FlowConstants.BUSINESS_LOCK + "sys_process:";
    private static final String CACHE_KEY = FlowConstants.BUSINESS_CACHE + "sys_process:";

    private final SqlSessionFactory sqlSessionFactory;
    private final IDGen idGen;
    private final RedissonClient redissonClient;
    private final RedisTemplate<Object, Object> redisTemplate;

    public ProcessServiceImpl(DataSource dataSource, IDGen idGen, PageInterceptor pageInterceptor,
                              RedissonClient redissonClient, RedisTemplate<Object, Object> redisTemplate) {
        this.idGen = idGen;
        this.redissonClient = redissonClient;
        this.redisTemplate = redisTemplate;
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
            PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
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
        // 分布式业务锁
        RLock lock = redissonClient.getLock(LOCK_KEY + procCode);
        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            // 获取锁
            boolean locked = lock.tryLock(10, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("服务器忙，请稍后重试");
            }
            // 业务处理
            process.setBpmn(FlowUtils.replaceProcCode(process.getBpmn(), procCode));
            if (process.getId() == null) {
                if (sqlSession.getMapper(ProcessMapper.class).getByProcCode(procCode) != null) {
                    throw new BusinessException("编码已存在");
                }
                process.setId(idGen.get("sys_process").getId());
                process.setCreateTime(LocalDateTime.now());
                sqlSession.getMapper(ProcessMapper.class).insert(process);
            } else {
                process.setUpdateTime(LocalDateTime.now());
                sqlSession.getMapper(ProcessMapper.class).update(process);
            }
            sqlSession.getMapper(ProcessTaskMapper.class).deleteByProcCode(procCode);
            if (process.getTasks() != null && process.getTasks().size() > 0) {
                for (SysProcessTask task : process.getTasks()) {
                    if (task.getId() == null) {
                        task.setId(idGen.get("sys_process").getId());
                    }
                }
                sqlSession.getMapper(ProcessTaskMapper.class).batchInsert(process.getTasks());
            }
            sqlSession.commit();
            // 删除缓存
            redisTemplate.delete(CACHE_KEY + procCode);
        } catch (Exception e) {
            log.error("保存流程失败，错误信息：{}", e.getMessage(), e);
        } finally {
            lock.unlock();
        }
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
            process.setId(idGen.get("sys_process").getId());
            process.setCreateTime(LocalDateTime.now());
            process.setBpmn(FlowUtils.replaceProcCode(from.getBpmn(), procCode));
            // 入库
            sqlSession.getMapper(ProcessMapper.class).insert(process);
            // 拷贝节点
            List<SysProcessTask> tasks = sqlSession.getMapper(ProcessTaskMapper.class).getByProcCode(fromCode);
            if (tasks != null && tasks.size() > 0) {
                for (SysProcessTask task : tasks) {
                    task.setId(idGen.get("sys_process").getId());
                    task.setProcCode(procCode);
                }
                // 节点入库
                sqlSession.getMapper(ProcessTaskMapper.class).batchInsert(tasks);
            }
            sqlSession.commit();
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
            RLock lock = redissonClient.getLock(LOCK_KEY + procCode);
            try {
                // 获取锁
                boolean locked = lock.tryLock(10, TimeUnit.SECONDS);
                if (!locked) {
                    throw new BusinessException("服务器忙，请稍后重试");
                }
                // 业务操作
                sqlSession.getMapper(ProcessMapper.class).delete(id);
                sqlSession.getMapper(ProcessTaskMapper.class).deleteByProcCode(procCode);
                sqlSession.commit();
                // 删除缓存
                redisTemplate.delete(CACHE_KEY + procCode);
            } catch (Exception e) {
                log.error("删除流程失败，错误信息：{}", e.getMessage(), e);
            } finally {
                lock.unlock();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object run(String code, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>(8);
        }
        // code在bpm文件中定义
        BpmnStringProcessEngineImpl processEngine = BpmnProcessEngineFactory.getProcessEngine();
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
        processEngine.setFlowString(FlowUtils.getFlowStr(process.getBpmn()));
        Map<String, Object> param = new HashMap<>(8);
        param.put("flowVars", flowVars);
        param.put("vars", params);
        param.put("varUtil", new VarUtils(params));
        Map<String, Object> flowResult = (Map<String, Object>) processEngine.execute(code, param).get("result");
        if (flowResult == null || !flowResult.containsKey("flowRes")) {
            return null;
        }
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
        if (StringUtils.isBlank(cmd)) {
            throw new BusinessException("代码不可为空");
        }
        JavaExecutor executor = new JavaExecutor();
        try {
            return executor.execute(cmd, params);
        } catch (Exception e) {
            throw new BusinessException("节点执行失败，错误信息" + e.getMessage(), e.getCause());
        }
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
                    FileURLConnection conn = (FileURLConnection) url.openConnection();
                    conn.connect();
                    if (ext_form.endsWith(".jar") && !ext_form.endsWith("idea_rt.jar")) {
                        Enumeration<JarEntry> jar_items = new JarFile(new File(ext_form.replace("file:/", ""))).entries();
                        while (jar_items.hasMoreElements()) {
                            JarEntry item = jar_items.nextElement();
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
                } else {
//                    JarURLConnection conn = (JarURLConnection) url.openConnection();
//                    conn.connect();
//                    Enumeration<JarEntry> jar_items = conn.getJarFile().entries();
//                    while (jar_items.hasMoreElements()) {
//                        JarEntry item = jar_items.nextElement();
//                        if (item.isDirectory() || (!item.getName().endsWith(".class"))) {
//                            continue;
//                        }
////                    if (item.getName().lastIndexOf("/") != (pkg.length() - 1)) {
////                        continue;
////                    }
//                        String name = item.getName();
////                    URI uri = URI.create(jar + "!/" + name);
//                        String binaryName = name.replaceAll("/", ".");
//                        binaryName = binaryName.substring(0, binaryName.indexOf(JavaFileObject.Kind.CLASS.extension));
////                    result.add(new JavaStringCompiler.LibJavaFileObject(binaryName, uri));
//                        list.add(binaryName);
//                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
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
        // 先查缓存
        String cache = (String) redisTemplate.opsForValue().get(CACHE_KEY + code);
        if (StringUtils.isNoneBlank(cache)) {
            return JSONObject.parseObject(cache, SysProcess.class);
        }
        SysProcess process;
        // 缓存未找到，查询数据库
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            process = sqlSession.getMapper(ProcessMapper.class).getByProcCode(code);
            if (process != null) {
                process.setTasks(sqlSession.getMapper(ProcessTaskMapper.class).getByProcCode(code));
            }
        }
        // 分布式业务锁
        RLock lock = redissonClient.getLock(LOCK_KEY + code);
        try {
            // 获取锁
            boolean locked = lock.tryLock(10, TimeUnit.SECONDS);
            if (!locked) {
                throw new BusinessException("服务器忙，请稍后重试");
            }
            // 写入缓存
            if (process == null) {
                redisTemplate.delete(CACHE_KEY + code);
            } else {
                redisTemplate.opsForValue().setIfAbsent(CACHE_KEY + code, JSONObject.toJSONString(process), 1, TimeUnit.DAYS);
            }
        } catch (Exception e) {
            log.error("缓存流程失败，错误信息：{}", e.getMessage(), e);
        } finally {
            lock.unlock();
        }
        return process;
    }

}

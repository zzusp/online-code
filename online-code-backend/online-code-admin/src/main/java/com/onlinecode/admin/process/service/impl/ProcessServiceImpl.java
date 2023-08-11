package com.onlinecode.admin.process.service.impl;

import com.alibaba.compileflow.engine.process.impl.BpmnStringProcessEngineImpl;
import com.alibaba.compileflow.extension.util.VarUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.onlinecode.admin.process.dao.ProcessMapper;
import com.onlinecode.admin.process.dao.ProcessTaskMapper;
import com.onlinecode.admin.process.model.SysProcess;
import com.onlinecode.admin.process.model.SysProcessTask;
import com.onlinecode.admin.process.service.ProcessService;
import com.onlinecode.admin.web.R;
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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 孙鹏
 * @description 流程服务
 * @date Created in 17:57 2023/2/24
 * @modified By
 */
@Service
public class ProcessServiceImpl implements ProcessService {

    private static final Pattern DATA_ACTION_PATTERN = Pattern.compile("dataAction=\"(.*?)\"");
    private static final Pattern CONDITION_PATTERN = Pattern.compile("<conditionExpression xsi:type=\"tFormalExpression\">(.*?)</conditionExpression>");

    private final SqlSessionFactory sqlSessionFactory;
    private final IDGen idGen;

    public ProcessServiceImpl(DataSource dataSource, IDGen idGen) {
        this.idGen = idGen;
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(ProcessMapper.class);
        configuration.addMapper(ProcessTaskMapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    @Override
    public R<PageTable> list() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            PageHelper.startPage(1, 10);
            List<SysProcess> list = sqlSession.getMapper(ProcessMapper.class).getAllProcess();
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
        try (SqlSession sqlSession = sqlSessionFactory.openSession(false)) {
            if (process.getId() == null) {
                if (sqlSession.getMapper(ProcessMapper.class).getByProcCode(process.getProcCode()) != null) {
                    throw new RuntimeException("编码已存在");
                }
                process.setId(idGen.get("sys_process").getId());
                process.setCreateTime(LocalDateTime.now());
                sqlSession.getMapper(ProcessMapper.class).insert(process);
            } else {
                process.setUpdateTime(LocalDateTime.now());
                sqlSession.getMapper(ProcessMapper.class).update(process);
            }
            sqlSession.getMapper(ProcessTaskMapper.class).deleteByProcCode(process.getProcCode());
            if (process.getTasks() != null && process.getTasks().size() > 0) {
                for (SysProcessTask task : process.getTasks()) {
                    if (task.getId() == null) {
                        task.setId(idGen.get("sys_process").getId());
                    }
                }
                sqlSession.getMapper(ProcessTaskMapper.class).batchInsert(process.getTasks());
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
            sqlSession.getMapper(ProcessMapper.class).delete(id);
            sqlSession.getMapper(ProcessTaskMapper.class).deleteByProcCode(process.getProcCode());
            sqlSession.commit();
        }
    }

    @Override
    public Object run(String code, Map<String, Object> params) {
        if (params == null) {
            params = new HashMap<>(8);
        }
        // code在bpm文件中定义
        BpmnStringProcessEngineImpl processEngine = new BpmnStringProcessEngineImpl();
        SysProcess process;
        Map<String, Object> flowVars = new HashMap<>(32);
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            process = sqlSession.getMapper(ProcessMapper.class).getByProcCode(code);
            List<SysProcessTask> tasks = sqlSession.getMapper(ProcessTaskMapper.class).getByProcCode(code);
            process.setTasks(tasks);
            if (!CollectionUtils.isEmpty(tasks)) {
                for (SysProcessTask task : tasks) {
                    flowVars.put(task.getTaskCode(), task.getExecuteCmd());
                }
            }
        }
        processEngine.setFlowString(getFlowStr(process.getBpmn()));
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

    /**
     * 过滤bpmn字符串中无用内容
     *
     * @param bpmn bpmn字符串
     * @return
     */
    public static String getFlowStr(String bpmn) {
        if (StringUtils.isEmpty(bpmn)) {
            return null;
        }
        // 过滤掉不支持的dataAction属性
        Matcher matcher = DATA_ACTION_PATTERN.matcher(bpmn);
        while (matcher.find()) {
            bpmn = bpmn.replace(matcher.group(), "");
        }
        // 过滤掉不需要存储的conditionExpression标签
        matcher = CONDITION_PATTERN.matcher(bpmn);
        while (matcher.find()) {
            bpmn = bpmn.replace(matcher.group(), "");
        }
        // 过滤掉不需要解析bpmndi
        bpmn = bpmn.substring(0, bpmn.indexOf("<bpmndi:BPMNDiagram")) + "</definitions>";
        return bpmn;
    }

}

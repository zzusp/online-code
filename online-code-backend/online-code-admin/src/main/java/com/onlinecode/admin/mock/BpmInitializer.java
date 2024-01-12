package com.onlinecode.admin.mock;

import com.alibaba.compileflow.engine.BpmnProcessEngineFactory;
import com.alibaba.compileflow.engine.process.impl.BpmnStringProcessEngineImpl;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.provider.support.BpmnElementParserProvider;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.support.bpmn.*;
import com.alibaba.compileflow.engine.process.preruntime.generator.bean.SpringApplicationContextProvider;
import com.alibaba.compileflow.extension.util.FlowUtils;
import com.onlinecode.admin.process.model.SysProcess;
import com.onlinecode.admin.process.service.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 孙鹏
 * @description 注意：这里是想让系统启动时，预先加载bpm流程到内存，防止第一次调用时，初始化流程所带来的耗时
 * @date Created in 17:57 2023/2/24
 * @modified By
 */
@Component
@Configuration
public class BpmInitializer implements InitializingBean, ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(BpmInitializer.class);

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        BpmnElementParserProvider bpmnElementParserProvider = BpmnElementParserProvider.getInstance();
        bpmnElementParserProvider.registerParser(new DecisionParser());
        bpmnElementParserProvider.registerParser(new IncomingParser());
        bpmnElementParserProvider.registerParser(new OutgoingParser());
        bpmnElementParserProvider.registerParser(new SequenceFlowParser());
        bpmnElementParserProvider.registerParser(new TaskParser());
        ProcessService processService = this.applicationContext.getBean(ProcessService.class);
        List<SysProcess> list = processService.listAll();
        if (list.isEmpty()) {
            log.info("no process bpmn need to init...");
        } else {
            BpmnStringProcessEngineImpl processEngine = BpmnProcessEngineFactory.getProcessEngine();
            log.info("start process bpmn init...");
            for (SysProcess p : list) {
                try {
                    processEngine.setFlowString(FlowUtils.getFlowStr(p.getBpmn()));
                    processEngine.preCompile(p.getProcCode());
                    log.info("--> {}({}) bpmn init success", p.getProcCode(), p.getProcName());
                } catch (Exception e) {
                    log.error("--> {}({}) bpmn init failed", p.getProcCode(), p.getProcName(), e);
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationContextProvider.applicationContext = applicationContext;
        this.applicationContext = applicationContext;
    }

}

package com.onlinecode.admin.mock;

import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.provider.support.BpmnElementParserProvider;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.support.bpmn.*;
import com.alibaba.compileflow.engine.process.preruntime.generator.bean.SpringApplicationContextProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 注意：这里是想让系统启动时，预先加载bpm流程到内存，防止第一次调用时，初始化流程所带来的耗时
 *
 * @author xuan
 * @since 2020/8/16
 */
@Component
@Configuration
public class BpmInitializer implements InitializingBean, ApplicationContextAware {

    @Override
    public void afterPropertiesSet() throws Exception {
//        ProcessEngine processEngine = ProcessEngineFactory.getProcessEngine();
//        processEngine.preCompile("bpm.ktvExample");
//        processEngine.preCompile("bpm.sqrt");

        BpmnElementParserProvider bpmnElementParserProvider = BpmnElementParserProvider.getInstance();
        bpmnElementParserProvider.registerParser(new DecisionParser());
        bpmnElementParserProvider.registerParser(new IncomingParser());
        bpmnElementParserProvider.registerParser(new OutgoingParser());
        bpmnElementParserProvider.registerParser(new SequenceFlowParser());
        bpmnElementParserProvider.registerParser(new TaskParser());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationContextProvider.applicationContext = applicationContext;
    }

}

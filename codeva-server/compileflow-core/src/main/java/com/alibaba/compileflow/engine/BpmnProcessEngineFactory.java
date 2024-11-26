package com.alibaba.compileflow.engine;

import com.alibaba.compileflow.engine.process.impl.BpmnStringProcessEngineImpl;

public class BpmnProcessEngineFactory {

    private static final BpmnStringProcessEngineImpl BPMN_PROCESS_ENGINE = new BpmnStringProcessEngineImpl();

    public static BpmnStringProcessEngineImpl getProcessEngine() {
        return BPMN_PROCESS_ENGINE;
    }
}

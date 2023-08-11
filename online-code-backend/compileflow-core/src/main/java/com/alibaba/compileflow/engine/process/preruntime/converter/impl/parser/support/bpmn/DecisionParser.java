package com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.support.bpmn;

import com.alibaba.compileflow.engine.definition.bpmn.BpmnModelConstants;
import com.alibaba.compileflow.engine.definition.bpmn.ServiceTask;
import com.alibaba.compileflow.engine.definition.bpmn.Task;
import com.alibaba.compileflow.engine.definition.common.Element;
import com.alibaba.compileflow.engine.definition.common.action.impl.Action;
import com.alibaba.compileflow.engine.definition.common.action.impl.JavaActionHandle;
import com.alibaba.compileflow.engine.definition.common.var.impl.Var;
import com.alibaba.compileflow.engine.definition.tbbpm.TbbpmModelConstants;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.constants.ActionType;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.model.ParseContext;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.model.XMLSource;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.support.AbstractBpmnElementParser;

public class DecisionParser extends AbstractBpmnElementParser<Task> {

    @Override
    protected Task doParse(XMLSource xmlSource, ParseContext parseContext) throws Exception {
        ServiceTask serviceTask = new ServiceTask();
        serviceTask.setId(xmlSource.getString(BpmnModelConstants.BPMN_ATTRIBUTE_ID));
        serviceTask.setImplementation(xmlSource.getString(BpmnModelConstants.BPMN_ATTRIBUTE_IMPLEMENTATION));
        String type = xmlSource.getCfString(BpmnModelConstants.BPMN_ATTRIBUTE_TYPE);
        serviceTask.setType(type);

        Action action = new Action();

        serviceTask.setType(ActionType.JAVA.getType());
        action.setType(ActionType.JAVA.getType());
        JavaActionHandle actionHandle = new JavaActionHandle();
        actionHandle.setClazz("com.alibaba.compileflow.extension.cmd.JavaCmd");
        actionHandle.setMethod("execute");
        // 传入执行时的参数对象
        Var flowVar = new Var();
        flowVar.setName("input");
        flowVar.setDataType("java.util.Map");
        flowVar.setContextVarName("flowVars");
        flowVar.setInOutType("param");
        actionHandle.addVar(flowVar);
        Var inVar = new Var();
        inVar.setName("input");
        inVar.setDataType("java.util.Map");
        inVar.setContextVarName("vars");
        inVar.setInOutType("param");
        actionHandle.addVar(inVar);
        // 传入当前节点的id
        Var taskId = new Var();
        taskId.setName("this.taskId");
        taskId.setDataType("java.lang.String");
        taskId.setContextVarName(null);
        taskId.setDefaultValue(serviceTask.getId());
        taskId.setInOutType("param");
        actionHandle.addVar(taskId);
        // 传入当前节点的名称
        Var taskName = new Var();
        taskName.setName("this.taskName");
        taskName.setDataType("java.lang.String");
        taskName.setContextVarName(null);
        taskName.setDefaultValue(xmlSource.getString(BpmnModelConstants.BPMN_ATTRIBUTE_NAME));
        taskName.setInOutType("param");
        actionHandle.addVar(taskName);
        // 返回参数
        Var outVar = new Var();
        outVar.setName("output");
        outVar.setDataType("Object");
        outVar.setContextVarName("result");
        outVar.setInOutType("return");
        actionHandle.addVar(outVar);
        action.setActionHandle(actionHandle);

        serviceTask.setAction(action);
        return serviceTask;
    }

    @Override
    protected void attachChildElement(Element childElement, Task element, ParseContext parseContext) {

    }

    @Override
    public String getName() {
        return TbbpmModelConstants.DECISION;
    }

}

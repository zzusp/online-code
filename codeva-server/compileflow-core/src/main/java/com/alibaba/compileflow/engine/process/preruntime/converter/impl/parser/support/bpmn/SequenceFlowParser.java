package com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.support.bpmn;

import com.alibaba.compileflow.engine.definition.bpmn.BpmnModelConstants;
import com.alibaba.compileflow.engine.definition.bpmn.Expression;
import com.alibaba.compileflow.engine.definition.bpmn.SequenceFlow;
import com.alibaba.compileflow.engine.definition.common.Element;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.model.ParseContext;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.model.XMLSource;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.support.AbstractBpmnElementParser;
import org.apache.commons.lang.StringUtils;

public class SequenceFlowParser extends AbstractBpmnElementParser<SequenceFlow> {
    public SequenceFlowParser() {
    }

    private String flowId;
    private String flowName;

    @Override
    protected SequenceFlow doParse(XMLSource xmlSource, ParseContext parseContext) throws Exception {
        SequenceFlow sequenceFlow = new SequenceFlow();
        sequenceFlow.setId(xmlSource.getString("id"));
        sequenceFlow.setName(xmlSource.getString("name"));
        sequenceFlow.setSourceRef(xmlSource.getString("sourceRef"));
        sequenceFlow.setTargetRef(xmlSource.getString("targetRef"));
        sequenceFlow.setImmediate(xmlSource.getBoolean("isImmediate"));
        this.flowId = sequenceFlow.getId();
        this.flowName = sequenceFlow.getName();
        return sequenceFlow;
    }

    @Override
    protected void attachChildElement(Element childElement, SequenceFlow element, ParseContext parseContext) {
        if (childElement instanceof Expression) {
            String conditionValue = ((Expression) childElement).getValue();
            if (StringUtils.isNotEmpty(conditionValue)) {
                element.setConditionExpression("new JavaExecutor().decide((Map)DataType.transfer(vars, Map.class), \""
                        + this.flowId + "\", \"" + this.flowName + "\")");
            } else {
                element.setConditionExpression(conditionValue);
            }
        }
    }

    @Override
    public String getName() {
        return BpmnModelConstants.BPMN_ELEMENT_SEQUENCE_FLOW;
    }
}

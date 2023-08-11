package com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.support.bpmn;

import com.alibaba.compileflow.engine.definition.bpmn.Activity;
import com.alibaba.compileflow.engine.definition.bpmn.BpmnModelConstants;
import com.alibaba.compileflow.engine.definition.common.Element;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.model.ParseContext;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.model.XMLSource;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.support.AbstractBpmnElementParser;

public class IncomingParser extends AbstractBpmnElementParser<Activity> {

    @Override
    protected Activity doParse(XMLSource xmlSource, ParseContext parseContext) throws Exception {
        return null;
    }

    @Override
    protected void attachChildElement(Element childElement, Activity element, ParseContext parseContext) {

    }

    @Override
    public String getName() {
        return BpmnModelConstants.BPMN_ELEMENT_INCOMING;
    }

}

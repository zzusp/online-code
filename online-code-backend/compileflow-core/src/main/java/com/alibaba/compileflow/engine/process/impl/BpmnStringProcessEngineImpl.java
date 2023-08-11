package com.alibaba.compileflow.engine.process.impl;

import com.alibaba.compileflow.engine.ProcessEngine;
import com.alibaba.compileflow.engine.common.CompileFlowException;
import com.alibaba.compileflow.engine.common.DirectedGraph;
import com.alibaba.compileflow.engine.common.constant.FlowModelType;
import com.alibaba.compileflow.engine.common.util.ArrayUtils;
import com.alibaba.compileflow.engine.definition.bpmn.BpmnModel;
import com.alibaba.compileflow.engine.definition.bpmn.FlowNode;
import com.alibaba.compileflow.engine.definition.common.Element;
import com.alibaba.compileflow.engine.definition.common.EndElement;
import com.alibaba.compileflow.engine.definition.common.TransitionNode;
import com.alibaba.compileflow.engine.definition.common.TransitionSupport;
import com.alibaba.compileflow.engine.definition.common.var.impl.Var;
import com.alibaba.compileflow.engine.process.preruntime.compiler.impl.FlowClassLoader;
import com.alibaba.compileflow.engine.process.preruntime.converter.FlowModelConverter;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.BpmnModelConverter;
import com.alibaba.compileflow.engine.process.preruntime.converter.impl.parser.model.StringFlowStreamSource;
import com.alibaba.compileflow.engine.runtime.impl.AbstractProcessRuntime;
import com.alibaba.compileflow.engine.runtime.impl.BpmnProcessRuntime;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BpmnStringProcessEngineImpl implements ProcessEngine<BpmnModel> {

    private final Map<String, AbstractProcessRuntime<BpmnModel>> runtimeCache = new ConcurrentHashMap<>();

    private String flowString;

    public BpmnStringProcessEngineImpl() {
    }

    public void setFlowString(String flowString) {
        this.flowString = flowString;
    }

    @Override
    public Map<String, Object> execute(String code, Map<String, Object> context) {
        BpmnProcessRuntime runtime = (BpmnProcessRuntime) this.getProcessRuntime(code);
        return runtime.start(context);
    }

    @Override
    public Map<String, Object> trigger(String code, String tag, Map<String, Object> context) {
        BpmnProcessRuntime runtime = (BpmnProcessRuntime) this.getProcessRuntime(code);
        return runtime.trigger(tag, context);
    }

    @Override
    public Map<String, Object> trigger(String code, String tag, String event, Map<String, Object> context) {
        BpmnProcessRuntime runtime = (BpmnProcessRuntime) this.getProcessRuntime(code);
        return runtime.trigger(tag, event, context);
    }

    @Override
    public Map<String, Object> start(String code, Map<String, Object> context) {
        BpmnProcessRuntime runtime = (BpmnProcessRuntime) this.getProcessRuntime(code);
        return runtime.start(context);
    }

    @Override
    public void preCompile(String... codes) {
        this.preCompile(null, codes);
    }

    @Override
    public void preCompile(ClassLoader classLoader, String... codes) {
        if (ArrayUtils.isEmpty(codes)) {
            throw new CompileFlowException("No process to compile");
        } else {
            String[] var3 = codes;
            int var4 = codes.length;
            for (int var5 = 0; var5 < var4; ++var5) {
                String code = var3[var5];
                AbstractProcessRuntime<BpmnModel> runtime = this.getProcessRuntime(code);
                runtime.compile(classLoader);
            }
        }
    }

    @Override
    public void reload(String code) {
        FlowClassLoader.getInstance().clearCache();
        AbstractProcessRuntime<BpmnModel> runtime = this.runtimeCache.computeIfPresent(code,
                (k, v) -> this.getRuntimeFromSource(code));
        assert runtime != null;
        runtime.recompile(code);
    }

    protected AbstractProcessRuntime<BpmnModel> getProcessRuntime(String code) {
        String cacheKey = this.getCacheKey(code);
        return this.runtimeCache.computeIfAbsent(cacheKey, (c) -> this.getCompiledRuntime(code));
    }

    private AbstractProcessRuntime<BpmnModel> getCompiledRuntime(String code) {
        AbstractProcessRuntime<BpmnModel> runtime = this.getRuntimeFromSource(code);
        runtime.compile();
        return runtime;
    }

    private AbstractProcessRuntime<BpmnModel> getRuntimeFromSource(String code) {
        BpmnModel flowModel = this.load(code);

        Var flowVar = new Var();
        flowVar.setName("flowVars");
        flowVar.setDataType("java.util.Map");
        flowVar.setInOutType("param");
        flowModel.getParamVars().add(flowVar);
        Var inVar = new Var();
        inVar.setName("vars");
        inVar.setDataType("java.util.Map");
        inVar.setInOutType("param");
        flowModel.getParamVars().add(inVar);
        Var varUtil = new Var();
        varUtil.setName("varUtil");
        varUtil.setDataType("com.alibaba.compileflow.extension.util.VarUtils");
        varUtil.setInOutType("param");
        flowModel.getParamVars().add(varUtil);
        Var outVar = new Var();
        outVar.setName("result");
        outVar.setDataType("java.util.Map");
        outVar.setInOutType("return");
        flowModel.getReturnVars().add(outVar);

        AbstractProcessRuntime<BpmnModel> runtime = this.getRuntimeFromModel(flowModel);
        runtime.init();
        return runtime;
    }

    @Override
    public BpmnModel load(String code) {
        StringFlowStreamSource flowStreamSource = StringFlowStreamSource.of(this.flowString);
        BpmnModel flowModel = this.getFlowModelConverter().convertToModel(flowStreamSource);
        if (flowModel == null) {
            throw new RuntimeException("No valid flow model found, code is " + code);
        } else {
            this.checkCycle(flowModel);
            this.checkContinuous(flowModel);
            this.sortTransition(flowModel);
            return flowModel;
        }
    }

    @Override
    public String getJavaCode(String code) {
        AbstractProcessRuntime<BpmnModel> runtime = this.getRuntimeFromSource(code);
        return runtime.generateJavaCode();
    }

    @Override
    public String getTestCode(String code) {
        AbstractProcessRuntime<BpmnModel> runtime = this.getRuntimeFromSource(code);
        return runtime.generateTestCode();
    }

    private void checkContinuous(BpmnModel flowModel) {
        ArrayList<TransitionNode> visitedNodes = new ArrayList<>();
        this.checkContinuous(flowModel.getStartNode(), visitedNodes, flowModel);
    }

    private void checkContinuous(TransitionNode node, List<TransitionNode> visitedNodes, BpmnModel flowModel) {
        visitedNodes.add(node);
        if (!(node instanceof EndElement)) {
            List<TransitionNode> outgoingNodes = node.getOutgoingNodes();
            if (CollectionUtils.isEmpty(outgoingNodes)) {
                throw new CompileFlowException("Flow should end with an end node " + flowModel);
            } else {
                Iterator<TransitionNode> var5 = outgoingNodes.iterator();
                while (var5.hasNext()) {
                    TransitionNode outgoingNode = var5.next();
                    if (!visitedNodes.contains(outgoingNode)) {
                        this.checkContinuous(outgoingNode, visitedNodes, flowModel);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void checkCycle(BpmnModel flowModel) {
        DirectedGraph<TransitionNode> directedGraph = new DirectedGraph<>();
        Iterator<FlowNode> var3 = flowModel.getAllNodes().iterator();

        while (var3.hasNext()) {
            TransitionNode node = var3.next();
            List<TransitionNode> outgoingNodes = node.getOutgoingNodes();
            if (CollectionUtils.isNotEmpty(outgoingNodes)) {
                outgoingNodes.forEach((outgoingNode) -> directedGraph.add(DirectedGraph.Edge.of(node, outgoingNode)));
            }
        }

        List<TransitionNode> cyclicVertexList = directedGraph.findCyclicVertexList();
        if (CollectionUtils.isNotEmpty(cyclicVertexList)) {
            throw new CompileFlowException("Cyclic nodes found in flow " + flowModel.getCode() + " check node ["
                    + cyclicVertexList.stream().map(Element::getId).collect(Collectors.joining(",")) + "]");
        }
    }

    private void sortTransition(BpmnModel flowModel) {
        flowModel.getAllNodes().forEach((node) -> {
            node.getTransitions().sort(Comparator.comparing(TransitionSupport::getPriority).reversed());
        });
    }

    private String getCacheKey(String code) {
        return code;
    }

    protected FlowModelType getFlowModelType() {
        return FlowModelType.BPMN;
    }

    protected FlowModelConverter<BpmnModel> getFlowModelConverter() {
        return BpmnModelConverter.getInstance();
    }

    protected AbstractProcessRuntime<BpmnModel> getRuntimeFromModel(BpmnModel bpmnModel) {
        return BpmnProcessRuntime.of(bpmnModel);
    }

}

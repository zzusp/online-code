package com.alibaba.compileflow.extension.executor;

import com.alibaba.compileflow.engine.process.preruntime.compiler.impl.BpmnFlowClassLoader;
import com.alibaba.compileflow.extension.cmd.AbstractJavaCmd;
import com.alibaba.compileflow.extension.core.JavaStringCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class JavaExecutor {

    private static final Logger log = LoggerFactory.getLogger(JavaExecutor.class);

    @SuppressWarnings("unchecked")
    public Map<String, Object> execute(Map<String, Object> flowVars, Map<String, Object> vars, String taskId, String taskName) throws UnsupportedEncodingException {
        log.debug("===========================");
        log.debug("执行{}（{}）节点", taskName, taskId);
        if (!flowVars.containsKey(taskId)) {
            log.debug("节点未配置代码，无需执行，跳过");
            return vars;
        }
        String code = flowVars.get(taskId).toString();
        return (Map<String, Object>) execute(code, vars);
    }

    public boolean decide(Map<String, Object> flowVars, Map<String, Object> vars, String flowId, String flowName) {
        log.debug("===========================");
        log.debug("执行{}（{}）流", flowName, flowId);
        if (!flowVars.containsKey(flowId)) {
            log.debug("连线未配置代码，无需执行，跳过");
            return true;
        }
        String code = flowVars.get(flowId).toString();
        Object result = execute(code, vars);
        if (!(result instanceof Boolean)) {
            return true;
        }
        return (boolean) result;
    }

    public Object execute(String code, Map<String, Object> vars) {
        JavaStringCompiler compiler = compiler(code);
        log.debug("开始运行");
        long startTime = System.currentTimeMillis();
        Object result = ((AbstractJavaCmd) compiler.getClassInstance()).execute(vars);
        log.debug("运行成功");
        log.debug("运行耗时：{}ms", System.currentTimeMillis() - startTime);
        return result;
    }

    public JavaStringCompiler compiler(String code) {
        JavaStringCompiler compiler = new JavaStringCompiler(code);
        compiler.setFlowClassLoader(BpmnFlowClassLoader.getInstance());
        log.debug("开始编译");
        if (!compiler.compile()) {
            log.debug("编译异常{}", compiler.getCompilerMessage());
            throw new RuntimeException("编译异常{}" + compiler.getCompilerMessage());
        }
        log.debug("编译成功");
        log.debug("编译耗时：{}ms", compiler.getCompilerTakeTime());
        return compiler;
    }

}

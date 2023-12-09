package compileflow;

import com.alibaba.compileflow.engine.ProcessEngineFactory;
import com.alibaba.compileflow.engine.common.util.ObjectFactory;
import com.alibaba.compileflow.engine.common.util.DataType;
import com.alibaba.compileflow.extension.util.VarUtils;
import com.alibaba.compileflow.extension.executor.JavaExecutor;
import com.alibaba.compileflow.engine.process.preruntime.generator.bean.BeanProvider;
import java.lang.String;
import java.util.Map;
import com.alibaba.compileflow.engine.runtime.instance.ProcessInstance;
import java.util.HashMap;

public class UserListFlow implements ProcessInstance {

    private java.util.Map flowVars = null;
    private java.util.Map vars = null;
    private com.alibaba.compileflow.extension.util.VarUtils varUtil = null;
    private java.util.Map result = null;

    public Map<String, Object> execute(Map<String, Object> _pContext) throws Exception {
        flowVars = (Map)DataType.transfer(_pContext.get("flowVars"), Map.class);
        vars = (Map)DataType.transfer(_pContext.get("vars"), Map.class);
        varUtil = (VarUtils)DataType.transfer(_pContext.get("varUtil"), VarUtils.class);
        
        //ServiceTask
        result = ((JavaExecutor)ObjectFactory.getInstance("com.alibaba.compileflow.extension.executor.JavaExecutor")).execute((Map)DataType.transfer(flowVars, Map.class), (Map)DataType.transfer(vars, Map.class), "Activity_09t0yok", "用户列表查询");
        
        return _wrapResult();
    }

    public Map<String, Object> _wrapResult() throws Exception {
        Map<String, Object> _pResult = new HashMap<>();
        _pResult.put("result", result);
        return _pResult;
    }

}
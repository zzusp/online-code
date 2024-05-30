package com.alibaba.compileflow.extension.cmd;

import java.util.Map;

/**
 * @author 孙鹏
 * @description Java代码执行
 * @date Created in 10:39 2023/6/27
 * @modified By
 */
public abstract class AbstractJavaCmd {

    /**
     * 执行节点代码
     *
     * @param vars 流程参数
     * @return 执行结果
     */
    public abstract Object execute(Map<String, Object> vars);

}

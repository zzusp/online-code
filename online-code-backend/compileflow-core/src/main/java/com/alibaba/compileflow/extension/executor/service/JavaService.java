package com.alibaba.compileflow.extension.executor.service;

import java.util.Map;

/**
 * @author 孙鹏
 * @description Java节点服务
 * @date Created in 17:57 2023/2/24
 * @modified By
 */
public interface JavaService {

    /**
     * 执行节点代码
     *
     * @param vars 流程参数
     * @return 执行结果
     */
    Object execute(Map<String, Object> vars);

}

package com.onlinecode.admin.constant;

import java.io.File;

/**
 * @author 孙鹏
 * @description 静态常量
 * @date Created in 17:57 2023/2/24
 * @modified By
 */
public class FlowConstants {

    public static final String FLOW_PROCESS_DIR = System.getProperty("user.dir") + File.separator
            + ".flowprocesses" + File.separator;
    public static final String FLOW_PROCESS_SUFFIX = ".json";

}

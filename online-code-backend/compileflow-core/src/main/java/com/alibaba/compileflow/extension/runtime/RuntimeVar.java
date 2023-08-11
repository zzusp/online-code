package com.alibaba.compileflow.extension.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeVar {

    public RuntimeVar() {
    }

    public static ThreadLocal<Map<String, Object>> getInstance() {
        return Holder.INSTANCE;
    }

    public static void runTask(String taskId) {
        RuntimeVar.getInstance().get().put("this.task", taskId);
    }

    public static String getRunTask() {
        return RuntimeVar.getInstance().get().get("this.task").toString();
    }

    private static class Holder {
        private static final ThreadLocal<Map<String, Object>> INSTANCE = new ThreadLocal<>();

        static {
            INSTANCE.set(new ConcurrentHashMap<>());
        }

        private Holder() {
        }
    }

}

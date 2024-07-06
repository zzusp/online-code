package com.onlinecode.admin.cron.service.impl;

import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 孙鹏
 * @description 定时任务注册启停
 * @date Created in 10:17 2024/6/5
 * @modified By
 */
@Service
public class CronTaskRegistrar {

    private static final Map<String, ScheduledTask> TASK_MAP = new ConcurrentHashMap<>(32);

    private ScheduledTaskRegistrar taskRegistrar;

    public void setTaskRegistrar(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
    }

    public void startTask(String taskCode, String cron, Runnable task) {
        if (TASK_MAP.containsKey(taskCode)) {
            return;
        }
        CronTask cronTask = new CronTask(task, cron);
        ScheduledTask scheduledTask = taskRegistrar.scheduleCronTask(cronTask);
        TASK_MAP.put(taskCode, scheduledTask);
    }

    public void stopTask(String taskCode) {
        if (TASK_MAP.containsKey(taskCode)) {
            TASK_MAP.get(taskCode).cancel();
            TASK_MAP.remove(taskCode);
        }
    }

    public void stopAllTask() {
        TASK_MAP.forEach((k, v) -> {
            v.cancel();
            TASK_MAP.remove(k);
        });
    }
}

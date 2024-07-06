package com.onlinecode.admin.config;

import com.onlinecode.admin.cron.model.SysCron;
import com.onlinecode.admin.cron.service.CronService;
import com.onlinecode.admin.cron.service.impl.CronTaskRegistrar;
import com.onlinecode.admin.enums.StatusEnum;
import com.onlinecode.admin.process.service.ProcessService;
import com.onlinecode.admin.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author 孙鹏
 * @description 定时任务配置
 * @date Created in 17:51 2024/6/5
 * @modified By
 */
@Configuration
public class CronConfig implements SchedulingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CronConfig.class);

    private final CronService cronService;
    private final CronTaskRegistrar cronTaskRegistrar;
    private final ProcessService processService;

    public CronConfig(CronService cronService, CronTaskRegistrar cronTaskRegistrar, ProcessService processService) {
        this.cronService = cronService;
        this.cronTaskRegistrar = cronTaskRegistrar;
        this.processService = processService;
    }


    public void init(CronService cronService, CronTaskRegistrar cronTaskRegistrar, ProcessService processService) {
        List<SysCron> cronList = cronService.listAll();
        cronList = cronList.stream().filter(cron -> StatusEnum.ENABLED.equals(cron.getStatus())).collect(Collectors.toList());
        if (!cronList.isEmpty()) {
            for (SysCron cron : cronList) {
                cronTaskRegistrar.startTask(cron.getCronCode(), cron.getCronTxt(), () -> {
                    processService.run(cron.getProcCode(), JsonUtils.convertJsonToMap(cron.getExecuteParam()));
                });
                log.info("--> {}({}) cron task is running.", cron.getCronName(), cron.getCronCode());
            }
        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        // 设置核心线程数等于系统核数--8核
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        taskScheduler.setPoolSize(availableProcessors * 2 + 1);
        // 设置线程活跃时间（秒）
        taskScheduler.setAwaitTerminationSeconds(15 * 60);
        // 线程满了之后由调用者所在的线程来执行
        // 拒绝策略：CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 设置默认线程名称
        taskScheduler.setThreadNamePrefix("cron-task");
        // 等待所有任务结束后再关闭线程池，不关闭线程池
        taskScheduler.setWaitForTasksToCompleteOnShutdown(false);
        // 初始化
        taskScheduler.initialize();
        taskRegistrar.setTaskScheduler(taskScheduler);

        cronTaskRegistrar.setTaskRegistrar(taskRegistrar);

        init(cronService, cronTaskRegistrar, processService);
        log.info("all cron task init success.");
    }
}

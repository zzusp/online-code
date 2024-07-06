package com.onlinecode.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 孙鹏
 * @description 线程池配置
 * @date Created in 16:15 2024/6/26
 * @modified By
 */
@EnableAsync
@Configuration
public class ThreadPoolConfig {

    /**
     * 动态编译的代码中使用的线程池
     *
     * @return 线程池
     */
    @Bean(name = "dynamicCompilerTaskExecutor")
    public TaskExecutor dynamicCompilerTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数等于系统核数--8核
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(availableProcessors);
        // 设置最大线程数
        executor.setMaxPoolSize(availableProcessors * 2 + 1);
        //配置队列大小
        executor.setQueueCapacity(1024);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(600);
        // 设置默认线程名称
        executor.setThreadNamePrefix("compiler-task");
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 线程满了之后由调用者所在的线程来执行
        // 拒绝策略：CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();


        return executor;
    }

}

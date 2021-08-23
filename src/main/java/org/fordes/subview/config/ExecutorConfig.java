package org.fordes.subview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 *
 * @author Fordes on 2021/2/23
 */
@Configuration
@EnableAsync
public class ExecutorConfig {

    private static final int corePoolSize =1;// Runtime.getRuntime().availableProcessors();//核心线程数

    private static final int maxPoolSize = 1;//corePoolSize* 2;//最大线程数

    private static final int queueCapacity = 100;//队列容量

    private static final int keepAliveSeconds = 10;//允许空闲时间（秒）

    private static final int awaitTerminationSeconds = 30;//等待时间

    /**
     * 通用线程池
     */
    @Bean("universalTask")
    public TaskExecutor universalTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("universalTask--");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        return executor;
    }

}

package org.fordes.subtitles.view.config;

import cn.hutool.core.thread.ExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fordes on 2022/7/11
 */
@Configuration
public class ExecutorConfig {

    private final static int core = Runtime.getRuntime().availableProcessors();

    @Bean("globalExecutor")
    public ThreadPoolExecutor globalExecutor() {
        return ExecutorBuilder.create()
                .setCorePoolSize(2 * core)
                .setMaxPoolSize(2 * core)
                .setHandler(new ThreadPoolExecutor.CallerRunsPolicy())
                .build();
    }


}

package org.fordes.subview.utils;

import cn.hutool.core.thread.ExecutorBuilder;

import java.util.concurrent.ExecutorService;

/**
 * @author fordes on 2021/6/18
 */
public class ServiceExecutor {

    public static final int corePoolSize = Runtime.getRuntime().availableProcessors();

    private static ServiceExecutor instance;

    private static ExecutorService executor;

    private ServiceExecutor() {}

    public static ServiceExecutor getInstance() {
        if (instance == null) {
            initExecutor();
            instance = new ServiceExecutor();
        }
        return instance;
    }


    public ExecutorService getExecutor() {
        return executor;
    }



    private static void initExecutor(int poolSize) {
        if (poolSize <= 1) {
            executor = ExecutorBuilder.create()
                    .setCorePoolSize(1)
                    .setMaxPoolSize(1)
                    .setKeepAliveTime(0)
                    .build();
        }else if (poolSize >= corePoolSize) {
            executor = ExecutorBuilder.create()
                    .setCorePoolSize(corePoolSize)
                    .setMaxPoolSize(poolSize)
                    .setKeepAliveTime(10).build();
        }else {
            executor = ExecutorBuilder.create()
                    .setCorePoolSize(poolSize)
                    .setCorePoolSize(poolSize)
                    .setKeepAliveTime(0)
                    .build();

        }
    }

    private static void initExecutor() {
        initExecutor(1);
    }
}

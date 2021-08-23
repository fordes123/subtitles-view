package org.fordes.subview.task;

import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.utils.common.ApplicationConfig;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化程序，缓存数据至内存
 *
 * @author fordes on 2020/10/4
 */


@Slf4j
@Component
public class Initialize implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception{
    }


}
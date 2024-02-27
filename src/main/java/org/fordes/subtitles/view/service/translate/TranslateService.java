package org.fordes.subtitles.view.service.translate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.event.LoadingEvent;
import org.fordes.subtitles.view.event.TranslateEvent;
import org.fordes.subtitles.view.model.DTO.Subtitle;
import org.fordes.subtitles.view.model.DTO.TranslateResult;
import org.fordes.subtitles.view.model.PO.Version;
import org.fordes.subtitles.view.utils.TranslateUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author fordes on 2022/7/29
 */
@Slf4j
@Service
public abstract class TranslateService {

    @Resource
    private ThreadPoolExecutor globalExecutor;

    @Async
    public void translate(Subtitle subtitle, String target, String original, Version version,
                          boolean mode, Map<String, Object> config) {
        TimeInterval interval = DateUtil.timer();
        Singleton.get(Stage.class).fireEvent(new LoadingEvent(true));
        //根据接口限制，重设线程池
        int threadNum = Math.min(globalExecutor.getMaximumPoolSize(), version.getConcurrent() - 1);
        globalExecutor.setCorePoolSize(threadNum);
        globalExecutor.setMaximumPoolSize(threadNum);
        //根据接口限制对数据整合分段
        List<String> segmented = TranslateUtil.segmented(subtitle, version.getCarrying());

        //延迟队列
        DelayQueue<Segment> queue = new DelayQueue<>();
        for (int i = 0; i < segmented.size(); i++) {
            queue.put(new Segment(segmented.get(i), i, ((i + 1) % version.getCarrying()) - 1));
        }
        //添加任务, 提交至线程池
        Collection<Future<TranslateResult>> futures = CollUtil.newArrayList();
        try {
            while (!queue.isEmpty()) {
                Segment part = queue.take();
                Integer serial = part.getSerial();
                String segment = part.getData();

                Future<TranslateResult> task = globalExecutor
                        .submit(createTask(globalExecutor, serial, segment, target, original, version, config));
                futures.add(task);
            }

            //遍历获取结果
            for (Future<TranslateResult> e : futures) {
                TranslateResult item = e.get();
                if (item.isSuccess()) {
                    segmented.set(item.getSerial(), item.getData());
                } else {
                    throw new RuntimeException(item.getData());
                }
            }

            //合并结果
            TranslateUtil.reduction(subtitle, segmented, mode);
        } catch (Exception ex) {
            log.error(ExceptionUtil.stacktraceToString(ex));
//            ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("翻译失败", ex.getMessage()));
            Platform.runLater(() ->
                    Singleton.get(Stage.class).fireEvent(new TranslateEvent(TranslateEvent.FAIL, ex.getMessage())));
            return;
        } finally {
            log.debug("翻译线程结束，耗时：{} ms", interval.intervalMs());
        }
//        ApplicationInfo.stage.fireEvent(new ToastConfirmEvent("翻译完成", StrUtil.format("总耗时：{} ms", interval.intervalMs())));
//        Platform.runLater(() -> ApplicationInfo.stage.fireEvent(new LoadingEvent(false)));
        Platform.runLater(() -> Singleton.get(Stage.class).fireEvent(new TranslateEvent(TranslateEvent.SUCCESS,
                StrUtil.format("总耗时：{} ms", interval.intervalMs()))));
    }

    /**
     * 创建翻译线程
     *
     * @param executor 线程池 {@link ThreadPoolExecutor}
     * @param serial   序号，用于再整合结果时维持内容顺序
     * @param segment  待翻译内容
     * @param target   目标语言
     * @param original 源语言
     * @param version  接口版本
     * @param config   接口配置
     * @return 线程
     */
    public abstract Callable<TranslateResult> createTask(ThreadPoolExecutor executor, int serial,
                                                         String segment, String target, String original,
                                                         Version version, Map<String, Object> config);


    static class Segment implements Delayed {

        private final long executeTime;

        @Getter
        private final Integer serial;

        @Getter
        private final String data;

        public Segment(String data, Integer serial, long delay) {
            this.data = data;
            this.serial = serial;
            this.executeTime = System.nanoTime()+ TimeUnit.NANOSECONDS.convert(delay, TimeUnit.SECONDS);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.executeTime - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            Segment that = (Segment) o;
            return Long.compare(executeTime, that.executeTime);
        }
    }
}

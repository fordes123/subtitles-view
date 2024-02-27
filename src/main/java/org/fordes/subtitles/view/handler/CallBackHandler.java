package org.fordes.subtitles.view.handler;

/**
 * @author fordes on 2022/7/27
 */
@FunctionalInterface
public interface CallBackHandler<T> {

    void handle(T value);
}

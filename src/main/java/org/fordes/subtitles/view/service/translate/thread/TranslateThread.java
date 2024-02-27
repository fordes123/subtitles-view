package org.fordes.subtitles.view.service.translate.thread;

import lombok.AllArgsConstructor;

/**
 * 翻译线程抽象
 *
 * @author fordes on 2022/7/27
 */
@AllArgsConstructor
public abstract class TranslateThread {

    /**
     * 序号，将随结果返回，用于还原顺序
     */
    public Integer serial;

    /**
     * 服务地址，即api调用地址
     */
    public String serviceURL;

    /**
     * 目标语言 通常为代码
     */
    public String target;

    /**
     * 原语言 通常为代码
     */
    public String original;

    /**
     * 待翻译内容
     */
    public String content;
}

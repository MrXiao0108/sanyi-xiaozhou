package com.dzics.data.common.base.dto.log;

import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/4/7.
 * @since
 */
@Data
public class ReatimLogRes {
    /**
     * 时间
     */
    private String realTime;
    /**
     * 日志信息
     */
    private String message;

    /**
     * 日志消息生产客户端
     */
    private String clientId;

    /**
     * 日志类型 1正常日志2告警日志
     */
    private int messageType;
}

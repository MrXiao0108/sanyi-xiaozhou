package com.dzics.data.rabbitmq.service;

/**
 * rabbitmq发送接口
 *
 * @author ZhangChengJun
 * Date 2021/3/18.
 * @since
 */
public interface RabbitmqService {
    /**
     * 发送json 字符串
     *
     * @param jsonString
     */
    void sendJsonString(String exchange,String routing,String jsonString);

    boolean sendDataCenter(String key, String exchange, Object dzWorkpieceData);
}

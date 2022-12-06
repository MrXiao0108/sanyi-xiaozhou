package com.dzics.data.transfer.mq;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.util.core.id.IdUtil;
import com.dzics.common.util.core.id.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * rabbitmq发送接口
 *
 * @author ZhangChengJun
 * Date 2021/3/18.
 * @since
 */
@Slf4j
@Component
public class RabbitmqServiceImpl implements RabbitmqService {

    @Autowired
    public RabbitTemplate rabbitTemplate;

    /**
     * 发送json 字符串
     *
     * @param jsonString
     */
    @Override
    public void sendJsonString(String exchange, String routing, String jsonString) {
        try {
            log.debug("开始转发数据到队列：{}", jsonString);
            jsonString = jsonString.replaceAll("\u0000", "");
            long start = System.currentTimeMillis();
            CorrelationData correlationDataNumber = new CorrelationData(IdUtil.snowflakeStr() + "");
            MessageProperties messagePropertiesNumber = new MessageProperties();
            Message messageNumber = new Message(jsonString.getBytes("UTF-8"), messagePropertiesNumber);
            correlationDataNumber.setReturnedMessage(messageNumber);
            rabbitTemplate.convertAndSend(exchange, routing, jsonString, correlationDataNumber);
            long end = System.currentTimeMillis();
            log.info("完成转发数据到队列:{},耗时:{}", jsonString, (end - start));
        } catch (Throwable throwable) {
            log.error("接收底层数据上发到队列错误：{}", throwable.getMessage(), throwable);
        }
    }

    @Override
    public boolean sendDataCenter(String key, String exchange, Object o) {
        try {
            byte[] sd;
            if (o instanceof String) {
                sd = o.toString().getBytes(StandardCharsets.UTF_8);
            } else {
                sd = JSONObject.toJSONString(o).getBytes(StandardCharsets.UTF_8);
            }
            Message message = MessageBuilder.withBody(sd)
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setMessageId(UUID.randomUUID().toString()).build();
            rabbitTemplate.send(exchange, key, message);
            return true;
        } catch (Throwable e) {
            log.error("报工信息发送至队列异常：{},发送内容：{}", e.getMessage(), o);
            return false;
        }
    }
}

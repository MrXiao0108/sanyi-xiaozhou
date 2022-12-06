package com.dzics.data.acquisition.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.rabbitmq.service.impl.RabbitmqServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@Slf4j
public class AcqRabbitmq extends RabbitmqServiceImpl {

    @Override
    public boolean sendDataCenter(String key, String exchange, Object o) {
        try {
            Message message = MessageBuilder.withBody(JSONObject.toJSONString(o).getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setMessageId(UUID.randomUUID().toString()).build();
            this.rabbitTemplate.send(exchange, key, message);
            return true;
        } catch (Throwable e) {
            log.error("报工信息发送至队列异常：{},发送内容：{}", e.getMessage(), o);
            return false;
        }
    }
}

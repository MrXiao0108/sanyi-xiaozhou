package com.dzics.data.appoint.changsha.mom.consumer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dzics.data.appoint.changsha.mom.enums.DNCProgramEnum;
import com.dzics.data.appoint.changsha.mom.service.DncProgramService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: van
 * @since: 2022-07-04
 */
@Slf4j
@Component
public class RabbitMQConsumer {

    @Autowired
    private DncProgramService dncProgramService;

    @RabbitListener(queues = "mom.tcp.dnc.program.queue")
    public void dncProgram(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.info("RabbitMQConsumer [dncProgram] 队列的消息:{}", msg);
            Map<String, Object> map = JSONObject.parseObject(msg, new TypeReference<Map<String, Object>>() {
            });
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("RabbitMQConsumer [dncProgram] 队列的消息:{}, e{}", msg, e.toString());
            channel.basicReject(deliveryTag, true);
        }
    }
}

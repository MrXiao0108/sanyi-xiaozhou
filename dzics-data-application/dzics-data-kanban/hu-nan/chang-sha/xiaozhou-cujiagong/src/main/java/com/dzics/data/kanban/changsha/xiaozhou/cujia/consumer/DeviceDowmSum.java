package com.dzics.data.kanban.changsha.xiaozhou.cujia.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeviceDowmSum {

    @Value("${center.device.downsum.queue}")
    private String downSumQuery;

    @RabbitListener(queues = "${center.device.downsum.queue}")
    public void dzRefresh(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费停机次数信息:{}, 队列的消息:{}", downSumQuery, msg);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费停机次数信息:{},队列的消息：{}", downSumQuery, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }
}

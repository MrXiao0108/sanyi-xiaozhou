package com.dzics.data.kanban.changsha.changpaoguang.consumer;

import com.alibaba.fastjson.JSON;
import com.dzics.data.kanban.changsha.changpaoguang.impl.PushPaoGuang;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Classname CheckoutCousomer
 * @Description 检测数据发送到看板
 * @Date 2022/3/13 15:55
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class CheckoutCousomer {
    @Value("${center.device.checkout.queue}")
    private String checkOutName;
    @Autowired
    private PushPaoGuang deviceStatusPush;

    @RabbitListener(queues = "${center.device.checkout.queue}")
    public void dzRefresh(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费检测记录信息:{}, 队列的消息:{}", checkOutName, msg);
            Map maps = (Map) JSON.parse(msg);
            boolean isOk = deviceStatusPush.sendWorkpieceData(maps);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费检测记录信息:{},队列的消息：{}", checkOutName, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }
}

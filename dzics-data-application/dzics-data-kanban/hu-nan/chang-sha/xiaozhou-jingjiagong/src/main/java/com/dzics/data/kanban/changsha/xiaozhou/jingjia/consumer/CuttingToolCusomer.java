package com.dzics.data.kanban.changsha.xiaozhou.jingjia.consumer;

import com.dzics.data.kanban.changsha.xiaozhou.jingjia.service.impl.PushXiaoZhouJingJia;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @Classname CuttingToolCusomer
 * @Description 处理刀具信息到看板
 * @Date 2022/3/14 9:36
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class CuttingToolCusomer {

    @Value("${center.device.tool.queue}")
    private String cuttingToolName;

    @Autowired
    private PushXiaoZhouJingJia deviceStatusPush;

    @RabbitListener(queues = "${center.device.tool.queue}")
    public void dzRefresh(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费刀具信息:{}, 队列的消息:{}", cuttingToolName, msg);
            boolean isOk = deviceStatusPush.sendToolDetection(msg);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费刀具信息:{},队列的消息：{}", cuttingToolName, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }
}

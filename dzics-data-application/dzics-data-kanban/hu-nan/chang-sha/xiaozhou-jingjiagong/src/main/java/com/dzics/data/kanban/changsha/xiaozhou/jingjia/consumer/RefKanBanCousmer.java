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
 * 发送到看板消息监听
 *
 * @author ZhangChengJun
 * Date 2021/11/19.
 * @since
 */
@Component
@Slf4j
public class RefKanBanCousmer {

    @Value("${push.kanban.ref.queue.simple}")
    private String pushQueue;

    @Autowired
    private PushXiaoZhouJingJia deviceStatusPush;

    @RabbitListener(queues = "${push.kanban.ref.queue.simple}")
    public void pushKanbanQueue(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费刷新页面指令信息:{}, 队列的消息:{}", pushQueue, msg);
            deviceStatusPush.dzRefresh(msg);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费刷新页面指令信息:{},队列的消息：{}", pushQueue, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }


    @Value("${push.kanban.ref.queue.dead.simple}")
    private String pushDeadQueue;

    @RabbitListener(queues = "${push.kanban.ref.queue.dead.simple}")
    public void pushKanbanQueueDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.warn("死亡队列丢弃: {},处理发送到看板数据:{}", pushDeadQueue, msg);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("死亡队列丢弃: {},处理发送到看板数据:{} 失败:{}", pushDeadQueue, msg, e.getMessage(), e);
            channel.basicReject(deliveryTag, false);
        }
    }

}

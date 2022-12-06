package com.dzics.data.kanban.changsha.shuixiang.consumer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.kanban.changsha.shuixiang.service.impl.PushShuiXiang;
import com.dzics.data.pub.model.entity.DzEquipment;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeviceState {

    @Value("${center.device.status.queue}")
    private String stateQuery;
    @Autowired
    private PushShuiXiang deviceStatusPush;

    @RabbitListener(queues = "${center.device.status.queue}")
    public void dzRefresh(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费设备状态信息:{}, 队列的消息:{}", stateQuery, msg);
            DzEquipment dzEquipment = JSONObject.parseObject(msg, DzEquipment.class);
            deviceStatusPush.sendStateEquiment(dzEquipment);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费设备状态信息:{},队列的消息：{}", stateQuery, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }
}

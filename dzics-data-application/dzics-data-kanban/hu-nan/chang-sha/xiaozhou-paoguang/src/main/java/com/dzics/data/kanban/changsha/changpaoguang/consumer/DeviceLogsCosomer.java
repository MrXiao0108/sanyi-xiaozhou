package com.dzics.data.kanban.changsha.changpaoguang.consumer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.kanban.changsha.changpaoguang.impl.PushPaoGuang;
import com.dzics.data.pub.model.vo.DeviceLogsMsg;
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
public class DeviceLogsCosomer {

    @Value("${center.device.log.queue}")
    private String stateQuery;
    @Autowired
    private PushPaoGuang deviceStatusPush;

    @RabbitListener(queues = "${center.device.log.queue}")
    public void dzRefresh(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费日志信息:{}, 队列的消息:{}", stateQuery, msg);
            DeviceLogsMsg logsMsg = JSONObject.parseObject(msg, DeviceLogsMsg.class);
            deviceStatusPush.sendReatimLogs(logsMsg);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费日志信息:{},队列的消息：{}", stateQuery, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }
}

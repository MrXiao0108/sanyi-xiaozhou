package com.dzics.data.appoint.changsha.mom.consumer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.appoint.changsha.mom.config.mq.AGVConfig;
import com.dzics.data.appoint.changsha.mom.model.dto.AgvTask;
import com.dzics.data.appoint.changsha.mom.service.MOMAGVService;
import com.dzics.data.appoint.changsha.mom.service.MomOrderService;
import com.dzics.data.appoint.changsha.mom.util.AutomaticGuidedVehicle;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class MomAgvConsumer {

    @Autowired
    private MOMAGVService momagvService;

    @RabbitListener(queues = AGVConfig.HANDSHAKE_QUEUE)
    public void handshakeHandle(String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) {
        log.info("MomAgvConsumer [handshakeHandle] msg{}", msg);
        try {
            momagvService.handshakeResult();
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            log.error("MomAgvConsumer [handshakeHandle] e: " + e);
            try {
                channel.basicReject(deliveryTag, false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

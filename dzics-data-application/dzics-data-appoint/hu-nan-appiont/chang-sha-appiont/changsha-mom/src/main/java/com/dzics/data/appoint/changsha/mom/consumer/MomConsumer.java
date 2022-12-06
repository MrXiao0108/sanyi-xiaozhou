package com.dzics.data.appoint.changsha.mom.consumer;

import com.dzics.data.appoint.changsha.mom.config.mq.AGVConfig;
import com.dzics.data.appoint.changsha.mom.config.mq.MOMConfig;
import com.dzics.data.appoint.changsha.mom.service.MOMAGVService;
import com.dzics.data.appoint.changsha.mom.service.MomCommunicationLogService;
import com.dzics.data.common.base.exception.CustomException;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class MomConsumer {

    @Autowired
    private MomCommunicationLogService momCommunicationLogService;

    @RabbitListener(queues = MOMConfig.LOG_COMMUNICATION_QUEUE)
    public void communicationLogHandle(String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) {
        log.info("MomConsumer [communicationLogHandle] msg{}", msg);
        try {
            momCommunicationLogService.addHandle(msg);
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            log.error("MomConsumer [communicationLogHandle] e: " + e);
            try {
                channel.basicReject(deliveryTag, false);
            } catch (IOException ex) {
                throw new CustomException(ex.getMessage());
            }
        }
    }
}

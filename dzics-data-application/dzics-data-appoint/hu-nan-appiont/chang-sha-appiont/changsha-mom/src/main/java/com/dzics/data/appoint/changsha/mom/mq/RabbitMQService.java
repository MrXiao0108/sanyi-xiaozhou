package com.dzics.data.appoint.changsha.mom.mq;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.common.base.model.constant.LogType;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.util.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author: van
 * @since: 2022-07-12
 */
@Slf4j
@Service
public class RabbitMQService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    public SnowflakeUtil snowflakeUtil;
    @Value("${accq.realTime.equipment.routing}")
    private String directRealTimeEquipmentRouting;
    @Value("${accq.realTime.equipment.exchange}")
    private String directRealTimeEquipmentExchange;

    public void sendMsg(String exchange, String routing, String str) {
        log.info("RabbitMQService [sendMsg] exchange{}, routing{}, str{}", exchange, routing, str);
        try {
            rabbitTemplate.convertAndSend(exchange, routing, str);
        } catch (AmqpException e) {
            log.error("RabbitMQService [sendMsg] e{}", e.toString());
        }
    }

    public boolean sendRabbitmqLog(String js) {
        try {
            RabbitmqMessage rabbitmqMessage = new RabbitmqMessage();
            rabbitmqMessage.setMessage(js);
            rabbitmqMessage.setClientId(LogType.logType);
            String toJSONString = JSONObject.toJSONString(rabbitmqMessage);
            CorrelationData correlationData = new CorrelationData(snowflakeUtil.nextId() + "");
            MessageProperties messageProperties = new MessageProperties();
            Message message = new Message(toJSONString.getBytes("UTF-8"), messageProperties);
            correlationData.setReturnedMessage(message);
            rabbitTemplate.send(directRealTimeEquipmentExchange, directRealTimeEquipmentRouting, message);
            log.debug("发送日志信息到队列有队列处理 {}", toJSONString);
            return false;
        } catch (Throwable throwable) {
            log.error("发送日志信息到队列有队列处理: {}", throwable.getMessage(), throwable);
            return false;
        }
    }

    @Value("${car.direct.order.exchange}")
    private String directExchangeOrder;

    @Value("${car.direct.order.routing.deadLetterRouting}")
    private String deadLetterRouting;

    public void sendMsgOrder(String jsonString) {
        try {
            CorrelationData correlationDataNumber = new CorrelationData(snowflakeUtil.nextId() + "");
            MessageProperties messagePropertiesNumber = new MessageProperties();
            Message messageNumber = new Message(jsonString.getBytes("UTF-8"), messagePropertiesNumber);
            correlationDataNumber.setReturnedMessage(messageNumber);
            rabbitTemplate.send(directExchangeOrder, deadLetterRouting, messageNumber);
        } catch (Exception e) {
            log.error("订单发送至延时队列失败：errorMsg:" + e.getMessage() + "->msg：" + jsonString);
        }
    }
}

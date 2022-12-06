package com.dzics.data.appoint.changsha.mom.consumer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Administrator
 * @Classname CheckRecordConsumer
 * @Description 检测数据
 * @Date 2022/4/29 10:04
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class CheckRecordConsumer {

    @Value("${center.device.checkout.queue}")
    private String checkOutName;

    /**
     * {"checkDate":"2022-04-29","createTime":1651204286049,"detect01":8952.071,"detect02":7415.005,"detect03":3467.137,"detect04":68.565,"detect05":9999.999,"detectorTime":1651204286042,"equipmentNo":"01","equipmentType":3,"id":"1519887052218359809","lineNo":"1","machineNumber":"A1","name":"默认产品","orderNo":"DZ-1872","outOk":0,"outOk01":0,"outOk02":0,"outOk03":0,"outOk04":1,"outOk05":0,"producBarcode":"116","productNo":"1017","workNumber":"4"}
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${center.device.checkout.queue}")
    public void dzRefresh(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费检测记录信息:{}, 队列的消息:{}", checkOutName, msg);
            Map<String, Object> map = JSONObject.parseObject(msg, new TypeReference<Map<String, Object>>() {
            });
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费检测记录信息:{},队列的消息：{}", checkOutName, msg, e);
            channel.basicReject(deliveryTag, true);
        }
    }

}

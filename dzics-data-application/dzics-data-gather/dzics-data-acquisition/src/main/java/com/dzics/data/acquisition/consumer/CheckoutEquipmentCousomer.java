package com.dzics.data.acquisition.consumer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.acquisition.service.AccCommunicationLogService;
import com.dzics.data.acquisition.service.AccWorkpieceDataService;
import com.dzics.data.acquisition.service.impl.AcqRabbitmq;
import com.dzics.data.common.base.model.constant.redis.lock.RedisLockKey;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.pdm.model.entity.DzWorkpieceData;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 检测设备数据消费处理
 *
 * @author ZhangChengJun
 * Date 2021/9/30.
 * @since
 */
@Component
@Slf4j
public class CheckoutEquipmentCousomer {
    @Autowired
    public RedissonClient redissonClient;
    /**
     * 检测设备
     */
    @Value("${accq.read.cmd.queue.base.checkout.equipment}")
    private String queueCheckoutEquipment;
    @Value("${accq.read.cmd.queue.base.checkout.equipment.dead}")
    private String queueCheckoutEquipmentDead;
    @Autowired
    private AccWorkpieceDataService accDzWorkpieceDataService;
    @Autowired
    private AccCommunicationLogService accCommunicationLogService;
    @Autowired
    private AcqRabbitmq acqRabbitmq;
    @Value("${center.device.checkout.routingkey}")
    private String routingKey;
    @Value("${center.device.checkout.topicexchange}")
    private String topicexchange;

    /**
     * 检测设备
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.read.cmd.queue.base.checkout.equipment}")
    public void queueCheckoutEquipment(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.info("消费：{} 队列的消息:{}", queueCheckoutEquipment, msg);
        RLock lock = redissonClient.getLock(RedisLockKey.DZ_LOCK_DATA_04);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            DzWorkpieceData dzWorkpieceData = accDzWorkpieceDataService.handleCheckout(rabbitmqMessage);
            try {
                if (dzWorkpieceData != null) {
                    log.info("CheckoutEquipmentCousomer [queueCheckoutEquipment] dzWorkpieceData != null {}", JSONObject.toJSONString(dzWorkpieceData));
                    acqRabbitmq.sendDataCenter(routingKey, topicexchange, dzWorkpieceData);
                }
            } catch (Throwable e) {
                log.error("检测数据，转发到数据中心异常:{}", e.getMessage(), e);
            }
            try {
                accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, false, true);
            } catch (Throwable throwable) {
                log.error("处理检测数据:{} 保存完成后续处理时错误：{}", msg, throwable.getMessage(), throwable);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费数据处理失败V1: {} 队列的消息：{}", queueCheckoutEquipment, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }

    @RabbitListener(queues = "${accq.read.cmd.queue.base.checkout.equipment.dead}")
    public void queueCheckoutEquipmentDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.warn("消费：{} 队列 超时消息 :{}", queueCheckoutEquipmentDead, msg);
        RLock lock = redissonClient.getLock(RedisLockKey.DZ_LOCK_DATA_04);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            DzWorkpieceData dzWorkpieceData = accDzWorkpieceDataService.handleCheckout(rabbitmqMessage);
            accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, false, true);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费 超时队列 检测数据处理失败: {} 队列的消息：{}", queueCheckoutEquipmentDead, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }


}

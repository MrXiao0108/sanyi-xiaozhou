package com.dzics.data.acquisition.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dzics.data.acquisition.service.AccCommunicationLogService;
import com.dzics.data.acquisition.service.AccDeviceHandleState;
import com.dzics.data.acquisition.service.impl.AcqRabbitmq;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.model.constant.redis.lock.RedisLockKey;
import com.dzics.data.pub.model.entity.DzEquipment;
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
 * 设备状态解析
 *
 * @author ZhangChengJun
 * Date 2021/10/8.
 * @since
 */
@Component
@Slf4j
public class AnalysisStateCousomer {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 状态
     */
    @Value("${accq.read.cmd.queue.base.state}")
    private String queueState;

    @Value("${accq.read.cmd.queue.base.state.copy}")
    private String queueStateCopy;

    @Value("${accq.read.cmd.queue.base.state.dead}")
    private String queueStateDead;

    @Autowired
    private AccDeviceHandleState accqAnalysisStateService;

    @Autowired
    private AccCommunicationLogService accCommunicationLogService;

    @Value("${center.device.status.topicexchange}")
    private String exchange;
    @Value("${center.device.status.routingkey}")
    private String key;
    @Autowired
    private AcqRabbitmq acqRabbitmq;

    @RabbitListener(queues = "${accq.read.cmd.queue.base.state.dead}")
    public void analysisNumStateDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.warn("消费死亡队列：{} 队列的消息:{}", queueStateDead, msg);
        channel.basicAck(deliveryTag, true);
    }

    /**
     * 更新设备状态，处理数据不存储更新，只用于发送到前端
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.read.cmd.queue.base.state}")
    public void analysisNumState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.debug("AnalysisStateCousomer [analysisNumState] msg: {}", msg);
        RLock lock = redissonClient.getLock(RedisLockKey.DZ_LOCK_DATA_02);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            // 解析后把设备状态发送到数据中心
            try {
                DzEquipment dzEquipment = accqAnalysisStateService.analysisNumStatePush(rabbitmqMessage);
                if (dzEquipment != null) {
                    try {
                        acqRabbitmq.sendDataCenter(key, exchange, dzEquipment);
                    } catch (Throwable e) {
                        log.error("设备状态，转发到数据中心异常:{}", e.getMessage(), e);
                    }
                }
            } catch (Throwable throwable) {
                log.error("设置状态解析或发送到客户端错误:{}", throwable.getMessage(), throwable);
            }
            // 保存日志
            try {
                accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, true, true);
            } catch (Throwable e) {
                log.error("保存设备状态队列日志或更新指令表状态异常：{}", e.getMessage(), e);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费数据处理失败队列: {} 的消息：{} ", queueState, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 更新设备状态 只处理数据，不发送到前端
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.read.cmd.queue.base.state.copy}")
    public void analysisNumStateCopy(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.debug("消费：{} 队列的消息:{}", queueStateCopy, msg);
        try {
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费数据处理失败队列: {} 的消息：{} ", queueStateCopy, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }
}

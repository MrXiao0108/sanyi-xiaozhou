package com.dzics.data.acquisition.consumer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.acquisition.service.AccCommunicationLogService;
import com.dzics.data.acquisition.service.AccProNumService;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.model.constant.redis.lock.RedisLockKey;
import com.dzics.data.pdm.model.entity.DzEquipmentProNum;
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
 * 数据处理消费
 *
 * @author ZhangChengJun
 * Date 2021/2/9.
 * @since
 */
@Component
@Slf4j
public class AnalysisNumCousomer {

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private AccProNumService proNumService;
    /**
     * 基础数据底层发送过来的
     */
    @Value("${accq.read.cmd.queue.base}")
    private String queue;

    @Autowired
    private AccCommunicationLogService accCommunicationLogService;

    /**
     * 生产数据
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.read.cmd.queue.base}")
    public void analysisNum(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.debug("消费：{}, 队列的消息:{}", queue, msg);
        RLock lock = redissonClient.getLock(RedisLockKey.LOCK_DATA_SUM_KEY_01);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            DzEquipmentProNum dzEquipmentProNum = proNumService.analysisNum(rabbitmqMessage);
            accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, false, true);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费:{} 数据处理失败消息：{}", queue, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }

}

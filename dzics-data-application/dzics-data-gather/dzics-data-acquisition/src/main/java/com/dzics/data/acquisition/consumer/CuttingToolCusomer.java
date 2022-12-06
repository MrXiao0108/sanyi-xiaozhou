package com.dzics.data.acquisition.consumer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.acquisition.service.AccCommunicationLogService;
import com.dzics.data.acquisition.service.AccToolService;
import com.dzics.data.acquisition.service.impl.AcqRabbitmq;
import com.dzics.data.common.base.dto.GetToolInfoDataDo;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.model.constant.redis.lock.RedisLockKey;
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
import org.springframework.util.ObjectUtils;

/**
 * 刀具队列数据处理类
 *
 * @author ZhangChengJun
 * Date 2021/9/30.
 * @since
 */
@Component
@Slf4j
public class CuttingToolCusomer {

    @Autowired
    private AccToolService accqAnalysisStateService;
    @Autowired
    public RedissonClient redissonClient;
    @Autowired
    private AccCommunicationLogService accCommunicationLogService;
    @Autowired
    private AcqRabbitmq acqRabbitmq;
    /**
     * 刀具检测
     */
    @Value("${accq.cutting.tool.detection}")
    private String toolDetection;
    @Value("${accq.cutting.tool.detection.dead}")
    private String toolDetectionDead;
    @Value("${center.device.tool.routingkey}")
    private String routingKey;
    @Value("${center.device.tool.topicexchange}")
    private String topicexchange;

    @RabbitListener(queues = "${accq.cutting.tool.detection.dead}")
    public void cuttingToolDetectionDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.warn("处理刀具死亡队列消息: {} 队列的消息:{}", toolDetectionDead, msg);
        channel.basicAck(deliveryTag, true);
    }

    /**
     * 刀具信息
     *
     * @param msg: 消息内容
     */
    @RabbitListener(queues = "${accq.cutting.tool.detection}")
    public void cuttingToolDetection(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.debug("CuttingToolCusomer [cuttingToolDetection] 消息:{}", msg);
        RLock lock = redissonClient.getLock(RedisLockKey.DZ_LOCK_DATA_10);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            GetToolInfoDataDo dataDo = accqAnalysisStateService.getEqToolInfoList(rabbitmqMessage);
            if (ObjectUtils.isEmpty(dataDo)) {
                log.error("CuttingToolCusomer [cuttingToolDetection] ObjectUtils.isEmpty(dataDo) msg{}", msg);
            }
            try {
                try {
                    acqRabbitmq.sendDataCenter(routingKey, topicexchange, dataDo);
                } catch (Throwable e) {
                    log.error("刀具数据，转发到数据中心异常:{}", e.getMessage(), e);
                }
            } catch (Throwable e) {
                log.error("推送消息到看板失败：", e);
            }
            //刀具日志保存
            accCommunicationLogService.saveRabbitmqMessage(rabbitmqMessage, false, true);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费数据处理失败: {} 队列的消息：{}", toolDetection, msg, e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }
}

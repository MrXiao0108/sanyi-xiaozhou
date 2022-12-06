package com.dzics.data.acquisition.consumer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.acquisition.service.AccCommunicationLogService;
import com.dzics.data.acquisition.service.AccNumSignalService;
import com.dzics.data.acquisition.service.impl.AcqRabbitmq;
import com.dzics.data.common.base.model.constant.LogClientType;
import com.dzics.data.common.base.model.constant.RedisKey;
import com.dzics.data.common.base.model.constant.ShareVo;
import com.dzics.data.common.base.model.constant.redis.lock.RedisLockKey;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.pdm.model.entity.DzEquipmentProNumSignal;
import com.dzics.data.redis.util.RedisUtil;
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
 * 脉冲计数处理数据
 *
 * @author ZhangChengJun
 * Date 2021/9/30.
 * @since
 */
@Component
@Slf4j
public class DeviceSignalCousomer {
    /**
     * 脉冲队列
     */
    @Value("${accq.read.cmd.queue.base.pulse.signal}")
    private String queuePylseSignal;

    @Autowired
    public RedissonClient redissonClient;
    @Autowired
    private AccNumSignalService signalService;
    @Autowired
    private AccCommunicationLogService logService;
    @Autowired
    private AcqRabbitmq acqRabbitmq;

    @Value("${center.pulse.signal.routingkey}")
    private String routingKey;
    @Value("${center.pulse.signal.topicexchange}")
    private String topicexchange;
    @Autowired
    private RedisUtil<ShareVo> redisUtil;

    /**
     * 脉冲生产数据
     * queue: dzics-dev-gather-v1-pulse-signal
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.read.cmd.queue.base.pulse.signal}")
    public void queuePylseSignal(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        log.debug("消费：{} 队列的消息:{}", queuePylseSignal, msg);
        RLock lock = redissonClient.getLock(RedisLockKey.DZ_LOCK_DATA_05);
        try {
            lock.lock();
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            boolean isCheck = signalService.queuePylseSignalCheck(rabbitmqMessage);
            rabbitmqMessage.setCheck(isCheck);
            if (isCheck) {
                try {
                    DzEquipmentProNumSignal numSignal = signalService.queuePylseSignal(rabbitmqMessage);
                    try {
                        shareCenter(rabbitmqMessage, numSignal);
                        // 需要补偿的数值
                        if (numSignal != null) {
                            try {
                                signalService.setRedisSignalValue(numSignal.getEquimentId(), numSignal.getSendSignalTime());
                            } catch (Throwable e) {
                                log.error("设置缓存频率错误：设备ID:{}", numSignal.getEquimentId(), e);
                            }
                            long compensate = signalService.compensate(numSignal).longValue();
                            if (compensate > 0) {
                                queuePylseSignalCompenState(compensate, rabbitmqMessage, numSignal.getEquimentId());
                            }
                        }
                    } catch (Throwable throwable) {
                        log.error("执行补偿业务流程错误：{}", throwable.getMessage(), throwable);
                    }
                } catch (Throwable throwable) {
                    log.error("处理脉冲信号异常：{}", throwable.getMessage(), throwable);
                }
            }
            logService.saveRabbitmqMessage(rabbitmqMessage, false, true);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            Thread.sleep(3000);
            log.error("消费数据处理失败:{} 队列的消息：{}", queuePylseSignal, msg, e);
            channel.basicReject(deliveryTag, true);
        } finally {
            lock.unlock();
        }
    }

    private synchronized void queuePylseSignalCompenState(long compensate, RabbitmqMessage rabbitmqMessage, String equimentId) {
        log.info("进行补偿次数：{},订单：{},产线序号：{},设备类型：{}，设备序号：{}", compensate, rabbitmqMessage.getOrderCode(), rabbitmqMessage.getLineNo(), rabbitmqMessage.getDeviceType(), rabbitmqMessage.getDeviceCode());
        rabbitmqMessage.setClientId(LogClientType.ACC_SIGNAL);
        int sumProSig = 0;
        try {
            for (long i = 0; i < compensate; i++) {
                try {
                    log.info("开始补偿脉冲      sumProSig :{}", sumProSig);
                    try {
                        DzEquipmentProNumSignal signal = signalService.queuePylseSignal(rabbitmqMessage);
                        shareCenter(rabbitmqMessage, signal);
                        // 发送增加次数
                        sumProSig++;
                        log.info("补偿一次脉冲结束正常  sumProSig :{}", sumProSig);
                    } catch (Throwable throwable) {
                        log.error("补偿一次脉冲结束异常  sumProSig :{}", sumProSig, throwable);
                    }
                    logService.saveRabbitmqMessage(rabbitmqMessage, false, true);
                } catch (Throwable throwable) {
                    log.error("存储脉冲信号 rabbitmqMessage：{},错误:{}", rabbitmqMessage, throwable);
                }
            }
        } catch (Throwable e) {
            log.error("进行补偿执行 accqAnalysisNumSignalService.queuePylseSignal 错误：{}", e.getMessage(), e);
        }
        log.info("补偿完成次数：{}", sumProSig);
    }

    private void shareCenter(RabbitmqMessage rabbitmqMessage, DzEquipmentProNumSignal signal) {
        if (signal != null) {
            try {
                ShareVo vo = redisUtil.get(RedisKey.SHARED_DATA + rabbitmqMessage.getOrderCode() + rabbitmqMessage.getLineNo());
                if (vo != null && Boolean.TRUE.equals(vo.getOkShare())) {
                    acqRabbitmq.sendDataCenter(routingKey, topicexchange, signal);
                }
            } catch (Throwable throwable) {
                log.error("脉冲信号发送数据到中心失败：{}", throwable.getMessage());
            }
        }
    }
}

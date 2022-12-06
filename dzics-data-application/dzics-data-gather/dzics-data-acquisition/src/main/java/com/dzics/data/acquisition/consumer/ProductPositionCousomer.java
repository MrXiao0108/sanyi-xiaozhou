package com.dzics.data.acquisition.consumer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.acquisition.config.datareceive.PositionConfig;
import com.dzics.data.acquisition.service.AccCommunicationLogService;
import com.dzics.data.acquisition.service.ProductPositionService;
import com.dzics.data.acquisition.service.WorkingFlowService;
import com.dzics.data.acquisition.service.impl.AcqRabbitmq;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.common.base.model.dto.position.SendPosition;
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

/**
 * 消费产品位置队列消息
 *
 * @author ZhangChengJun
 * Date 2021/5/17.
 * @since
 */
@Component
@Slf4j
public class ProductPositionCousomer {

    @Value("${accq.product.position.query}")
    private String positionQuery;
    @Autowired
    public RedissonClient redissonClient;
    @Autowired
    private AccCommunicationLogService logService;
    @Autowired
    private WorkingFlowService workingFlowService;
    @Autowired
    private AcqRabbitmq acqRabbitmq;
    @Value("${center.workpiece.position.topicexchange}")
    private String topicexchange;
    @Value("${center.workpiece.position.routingkey}")
    private String routingKey;
    @Autowired
    private ProductPositionService productPositionService;

    /**
     * 报工基础处理
     * queue: dzics-dev-gather-v1-product-position
     * {
     * "MessageId":"ea6b57744a94467bba430493e8dbbf01",
     * "QueueName":"dzics-dev-gather-v1-product-position",
     * "ClientId":"DZROBOT",
     * "OrderCode":"DZ-1875",
     * "LineNo":"1",
     * "DeviceType":"6",
     * "DeviceCode":"17",
     * "Message":"A815|[1,ASANY-20-OCT-21 13:57 3547]",
     * "Timestamp":"2021-10-20 13:26:33.5410"
     * }
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${accq.product.position.query}")
    public void dzEncasementRecordState(@Payload String msg,
                                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                        Channel channel) throws Throwable {
        RLock lock = redissonClient.getLock(RedisLockKey.DZ_LOCK_DATA_MOM_112);
        try {
            lock.lock();
            log.info("队列: {},接收到报工数据:{}", positionQuery, msg);
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            try {
                SendPosition qrcode = workingFlowService.processingData(rabbitmqMessage);
                if (qrcode != null) {
                    acqRabbitmq.sendDataCenter(routingKey, topicexchange, qrcode);
                }
            } catch (Throwable e) {
                log.error("处理报工位置数据异常：{}", e.getMessage(), e);
            }
            logService.saveRabbitmqMessage(rabbitmqMessage, false, true);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费队列:{},信息:{},失败:{}", positionQuery, msg, e.getMessage(), e);
            channel.basicReject(deliveryTag, false);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 报工临时码 前置处理
     * queue: dzics-dev-gather-v1-product-position-temp-code
     * {
     * "MessageId":"ea6b57744a94467bba430493e8dbbf01",
     * "QueueName":"dzics-dev-gather-v1-product-position",
     * "ClientId":"DZROBOT",
     * "OrderCode":"DZ-1875",
     * "LineNo":"1",
     * "DeviceType":"6",
     * "DeviceCode":"17",
     * "Message":"A815|[1,123,456]",
     * "Timestamp":"2021-10-20 13:26:33.5410"
     * }
     *
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = PositionConfig.BAO_GONG_TEMP_CODE_QUEUE)
    public void baoGongTempCode(@Payload String msg,
                                @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                Channel channel) throws Throwable {
        try {
            log.info("ProductPositionCousomer [baoGongTempCode] msg:{}", msg);
            RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
            productPositionService.baoGongTempCodeWrapper(rabbitmqMessage);
            logService.saveRabbitmqMessage(rabbitmqMessage, false, true);
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("ProductPositionCousomer [baoGongTempCode] 信息:{},失败:{}", msg, e.getMessage());
            channel.basicReject(deliveryTag, false);
        }
    }
}

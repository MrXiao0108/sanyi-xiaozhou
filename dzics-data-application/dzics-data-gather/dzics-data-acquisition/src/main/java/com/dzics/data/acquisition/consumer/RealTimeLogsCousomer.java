package com.dzics.data.acquisition.consumer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.acquisition.service.impl.AcqRabbitmq;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.logms.model.entity.SysRealTimeLogs;
import com.dzics.data.logms.service.SysRealTimeLogsService;
import com.dzics.data.pub.model.vo.DeviceLogsMsg;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 日志处理
 *
 * @author ZhangChengJun
 * Date 2021/4/6.
 * @since
 */
@Component
@Slf4j
public class RealTimeLogsCousomer {

    @Value("${accq.read.cmd.queue.equipment.realTime}")
    private String queueRealTimeEquipment;

    @Value("${accq.read.cmd.queue.equipment.realTime.dead}")
    private String queueRealTimeEquipmentDead;

    @Autowired
    private SysRealTimeLogsService realTimeLogsService;

    @Autowired
    private AcqRabbitmq acqRabbitmq;

    @Value("${center.device.log.topicexchange}")
    private String topicexchange;

    @Value("${center.device.log.routingkey}")
    private String routingKey;

    @RabbitListener(queues = "${accq.read.cmd.queue.equipment.realTime}")
    public void dzEncasementRecordState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费队列的消息:{},传送日志信息:{},", queueRealTimeEquipment, msg);
            try {
                RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
                List<SysRealTimeLogs> logs = realTimeLogsService.saveRealTimeLog(rabbitmqMessage);
                try {
                    if (logs != null) {
                        for (SysRealTimeLogs timeLog : logs) {
                            DeviceLogsMsg logsMsg = new DeviceLogsMsg();
                            logsMsg.setTimestampTime(timeLog.getTimestampTime());
                            logsMsg.setClientId(timeLog.getClientId());
                            logsMsg.setMessage(timeLog.getMessage());
                            logsMsg.setDeviceType(timeLog.getDeviceType());
                            logsMsg.setMessageType(timeLog.getMessageType());
                            logsMsg.setOrderCode(timeLog.getOrderCode());
                            logsMsg.setLineNo(timeLog.getLineNo());
                            acqRabbitmq.sendDataCenter(routingKey, topicexchange, logsMsg);
                            log.debug("推送到看板消息:{}", logs);
                        }
                    }
                } catch (Throwable b) {
                    log.error("推送到看板消息异常:{}", b.getMessage(), b);
                }
            } catch (Throwable e) {
                log.error("处理日志信息异常", e);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费队列的消息:{},传送日志信息:{},失败", queueRealTimeEquipment, msg);
            channel.basicReject(deliveryTag, false);
        }
    }

    @RabbitListener(queues = "${accq.read.cmd.queue.equipment.realTime.dead}")
    public void dzEncasementRecordStateDead(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("死亡队列的消息:{},传送日志信息:{},", queueRealTimeEquipmentDead, msg);
            try {
                RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
                List<SysRealTimeLogs> b = realTimeLogsService.saveRealTimeLog(rabbitmqMessage);
            } catch (Throwable e) {
                log.error("死亡处理日志信息异常", e);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("死亡消费队列的消息:{},传送日志信息:{},失败", queueRealTimeEquipmentDead, msg);
            channel.basicReject(deliveryTag, false);
        }
    }

}

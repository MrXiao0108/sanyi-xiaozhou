package com.dzics.data.kanban.changsha.xiaozhou.jingjia.consumer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.common.base.model.dto.RabbitmqMessage;
import com.dzics.data.kanban.changsha.xiaozhou.jingjia.service.impl.PushXiaoZhouJingJia;
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
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author xnb
 * @date 2022/11/25 0025 11:13
 */
@Slf4j
@Component
public class AnalysisRunStateCousomer {


    @Autowired
    private SysRealTimeLogsService realTimeLogsService;
    @Autowired
    private PushXiaoZhouJingJia pushXiaoZhouCuJia;

    //设备运行队列配置
    @Value("${accq.read.cmd.queue.base.run.state}")
    private String queueRunState;
    @Value("${accq.run.state.routing}")
    private String routingKey;


    @RabbitListener(queues = "${accq.read.cmd.queue.base.run.state}")
    public void dzEncasementRecordState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费队列的消息:{},传送日志信息:{},", queueRunState, msg);
            try {
                RabbitmqMessage rabbitmqMessage = JSONObject.parseObject(msg, RabbitmqMessage.class);
                List<SysRealTimeLogs> logs = realTimeLogsService.saveRealTimeLog(rabbitmqMessage);
                if(!CollectionUtils.isEmpty(logs)){
                    for (SysRealTimeLogs log : logs) {
                        DeviceLogsMsg logsMsg = new DeviceLogsMsg();
                        logsMsg.setDeviceType(log.getDeviceType());
                        logsMsg.setTimestampTime(log.getTimestampTime());
                        logsMsg.setMessage(log.getMessage());
                        logsMsg.setClientId(log.getClientId());
                        logsMsg.setMessageType(log.getMessageType());
                        logsMsg.setLineNo(log.getLineNo());
                        logsMsg.setOrderCode(log.getOrderCode());
                        pushXiaoZhouCuJia.sendReatimLogs(logsMsg);
                    }
                }
            } catch (Throwable e) {
                log.error("处理日志信息异常", e);
            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费队列的消息:{},传送日志信息:{},失败", queueRunState, msg);
            channel.basicReject(deliveryTag, false);
        }
    }
}

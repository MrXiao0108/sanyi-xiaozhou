package com.dzics.data.appoint.changsha.mom.consumer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.appoint.changsha.mom.model.constant.MomConstant;
import com.dzics.data.appoint.changsha.mom.service.impl.WorkReportingCjServiceImpl;
import com.dzics.data.appoint.changsha.mom.service.impl.WorkReportingJjServiceImpl;
import com.dzics.data.appoint.changsha.mom.service.impl.WorkReportingPgServiceImpl;
import com.dzics.data.common.base.model.dto.position.SendPosition;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @Classname CheckRecordConsumer
 * @Description 报工数据
 * @Date 2022/4/29 10:04
 * @Created by NeverEnd
 */
@Component
@Slf4j
public class WorkReportConsumer {
    @Value("${center.workpiece.position.queue}")
    private String positionName;
    @Autowired
    private WorkReportingCjServiceImpl cjService;
    @Autowired
    private WorkReportingJjServiceImpl jjService;
    @Autowired
    private WorkReportingPgServiceImpl pgService;

    /**
     * @param msg
     * @param deliveryTag
     * @param channel
     * @throws Throwable
     */
    @RabbitListener(queues = "${center.workpiece.position.queue}")
    public void dzRefresh(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.info("消费报工记录信息:{}, 队列的消息:{}", positionName, msg);
            SendPosition sendPosition = JSONObject.parseObject(msg, SendPosition.class);
            String orderNo = sendPosition.getOrderNo();
//            粗加工订单号
            Boolean s = null;
            if (MomConstant.ORDER_DZ_1972.equals(orderNo) || MomConstant.ORDER_DZ_1973.equals(orderNo)) {
                s = cjService.sendWorkReportingData(sendPosition, null);
            }
//            精加工订单号
            if (MomConstant.ORDER_DZ_1974.equals(orderNo) || MomConstant.ORDER_DZ_1975.equals(orderNo)) {
                s = jjService.sendWorkReportingData(sendPosition, null);
            }
//            抛光订单号
            if (MomConstant.ORDER_DZ_1976.equals(orderNo)) {
                s = pgService.sendWorkReportingData(sendPosition, null);
            }
            if (Boolean.TRUE.equals(s)) {
                log.info("报工成功:{}", JSONObject.toJSONString(sendPosition));
            }
//            else {
//                log.info("报工失败:{}", JSONObject.toJSONString(sendPosition));
//            }
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("消费报工记录信息:{},队列的消息：{}", positionName, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }
}

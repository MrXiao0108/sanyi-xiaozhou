package com.dzics.data.appoint.changsha.mom.consumer;

import com.alibaba.fastjson.JSONObject;
import com.dzics.data.appoint.changsha.mom.model.entity.MomOrder;
import com.dzics.data.appoint.changsha.mom.service.MomOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCousomer {
    @Value("${car.direct.order.queue.delayed}")
    private String queueOrder;
    @Autowired
    private MomOrderService momOrderService;

    @RabbitListener(queues = "${car.direct.order.queue.delayed}")
    public void dzEncasementRecordState(@Payload String msg, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws Throwable {
        try {
            log.debug("消费队列:{},信息:{}", queueOrder, msg);
//            旧订单状态
            MomOrder byId = JSONObject.parseObject(msg, MomOrder.class);
//            当前订单最新状态
            MomOrder nowOrder = momOrderService.getById(byId.getProTaskOrderId());
            if (nowOrder.getOrderOperationResult() == 1) {
//               超过两分钟订单执行超时，自动回复之前的状态
                nowOrder.setProgressStatus(byId.getProgressStatus());
                nowOrder.setOrderOperationResult(2);
                nowOrder.setOrderOldState(byId.getOrderOldState());
                momOrderService.updateById(nowOrder);
                log.warn("订单执行超时，自动回复之前的状态:恢复前:{},恢复后:{}", JSONObject.toJSONString(byId), JSONObject.toJSONString(nowOrder));
            }
//           处理订单状态
            channel.basicAck(deliveryTag, true);
        } catch (Throwable e) {
            log.error("延迟处理订单状态错误，队列:{},信息:{}", queueOrder, msg, e);
            channel.basicReject(deliveryTag, false);
        }
    }
}

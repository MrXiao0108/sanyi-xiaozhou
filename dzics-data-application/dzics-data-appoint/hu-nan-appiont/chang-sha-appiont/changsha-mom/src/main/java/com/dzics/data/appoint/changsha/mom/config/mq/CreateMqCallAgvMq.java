package com.dzics.data.appoint.changsha.mom.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CreateMqCallAgvMq {

    /**
     *处理agv 反馈 交换机
     */
    @Value("${call.direct.agv.exchange}")
    private String directExchangeAgv;
    /**
     * 延时消费队列
     */
    @Value("${call.direct.agv.queue.delayed}")
    private String repeatTradeQueue;
    /**
     * 私信队列
     */
    @Value("${call.direct.agv.queue.deadLetter}")
    private String deadLetterQueue;

    @Value("${call.direct.agv.routing.repeatTradeRouting}")
    private String repeatTradeRouting;
    @Value("${call.direct.agv.routing.deadLetterRouting}")
    private String deadLetterRouting;
    /**
     * 创建订单交换机
     * @return
     */
    @Bean("callAgvDirectExchange")
    DirectExchange callAgvDirectExchange() {
        return new DirectExchange(directExchangeAgv, true, false);
    }




    /**
     * 用于延时消费的队列
     * durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
     * exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
     * autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
     * return new Queue("TestDirectQueue",true,true,false);
     * @return
     */
    @Bean(name = "repeatTradeQueueAgv")
    public Queue repeatTradeQueueAgv() {
        return new Queue(repeatTradeQueue, true,false,false);
    }




    //配置死信队列
    @Bean("deadLetterQueueAgv")
    public Queue deadLetterQueueAgv() {
        Map<String,Object> args = new HashMap<>();
        args.put("x-message-ttl", 2000);
        args.put("x-dead-letter-exchange", directExchangeAgv);
        args.put("x-dead-letter-routing-key", repeatTradeRouting);
        return  new Queue(deadLetterQueue, true, false, false, args);
    }

    /**
     * 绑定将队列和交换机绑定, 并设置用于匹配键：directRoutingOrder
     * @return
     */
    @Bean("repeatTradeQueueBindingDirectAgv")
    Binding repeatTradeQueueBindingDirectAgv() {
        return BindingBuilder.bind(repeatTradeQueueAgv()).to(callAgvDirectExchange()).with(repeatTradeRouting);
    }

    @Bean("bindingDirectDeadLetterAgv")
    Binding bindingDirectDeadLetterAgv() {
        return BindingBuilder.bind(deadLetterQueueAgv()).to(callAgvDirectExchange()).with(deadLetterRouting);
    }
}

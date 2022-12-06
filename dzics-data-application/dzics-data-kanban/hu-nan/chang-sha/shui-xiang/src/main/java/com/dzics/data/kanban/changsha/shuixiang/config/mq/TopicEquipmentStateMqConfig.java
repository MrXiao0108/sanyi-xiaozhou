package com.dzics.data.kanban.changsha.shuixiang.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 菲仕设备状态转发  至  上发MES队列
 */
@Configuration
public class TopicEquipmentStateMqConfig {
    @Value("${center.device.status.queue}")
    private String queue;
    @Value("${center.device.status.topicexchange}")
    private String topicExchange;
    @Value("${center.device.status.routingkey}")
    private String routingKey;

    @Bean(name = "queueEquipmentState")
    public Queue queueEquipmentState() {
        return new Queue(queue, true);
    }

    @Bean(name = "exchangeEquipmentState")
    public TopicExchange exchangeEquipmentState() {
        return new TopicExchange(topicExchange);
    }

    @Bean(name = "bindingEquipmentState")
    public Binding bindingState(@Qualifier("queueEquipmentState") Queue queue, @Qualifier("exchangeEquipmentState") TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }
}

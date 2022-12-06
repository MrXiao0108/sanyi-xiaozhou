package com.dzics.data.appoint.changsha.mom.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ZhangChengJun
 * Date 2021/12/31.
 * @since
 */
@Configuration
public class TopicPositionMqconfig {
    @Value("${center.workpiece.position.queue}")
    private String queueName;

    @Value("${center.workpiece.position.topicexchange}")
    private String topicexchange;

    @Value("${center.workpiece.position.routingkey}")
    private String routingKey;


    @Bean(name = "queueWorkpiece")
    public Queue queueWorkpiece() {
        return new Queue(queueName, true);
    }

    @Bean(name = "exchangeWorkpiece")
    public TopicExchange exchangeWorkpiece() {
        return new TopicExchange(topicexchange);
    }

    @Bean(name = "bindingWorkpiece")
    public Binding bindingWorkpiece(@Qualifier("queueWorkpiece") Queue queue, @Qualifier("exchangeWorkpiece") TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }
}

package com.dzics.data.kanban.changsha.xiaozhou.jingjia.config.mq;

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
public class TopicLogMqconfig {
    @Value("${center.device.log.queue}")
    private String queueName;

    @Value("${center.device.log.topicexchange}")
    private String topicexchange;

    @Value("${center.device.log.routingkey}")
    private String routingKey;


    @Bean(name = "queueLog")
    public Queue queueLog() {
        return new Queue(queueName, true);
    }

    @Bean(name = "exchangeLog")
    public TopicExchange exchangeLog() {
        return new TopicExchange(topicexchange);
    }

    @Bean(name = "bindingLog")
    public Binding bindingLog(@Qualifier("queueLog") Queue queue, @Qualifier("exchangeLog") TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }
}

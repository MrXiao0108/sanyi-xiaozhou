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
public class TopicCheckoutMqconfig {
    @Value("${center.device.checkout.queue}")
    private String queueName;

    @Value("${center.device.checkout.topicexchange}")
    private String topicexchange;

    @Value("${center.device.checkout.routingkey}")
    private String routingKey;


    @Bean(name = "queueDataDzicsCheckout")
    public Queue queueDataDzicsCheckout() {
        return new Queue(queueName, true);
    }

    @Bean(name = "exchangeDataDzicsCheckout")
    public TopicExchange exchangeDataDzicsCheckout() {
        return new TopicExchange(topicexchange);
    }

    @Bean(name = "bindingCheckout")
    public Binding bindingCheckout(@Qualifier("queueDataDzicsCheckout") Queue queue, @Qualifier("exchangeDataDzicsCheckout") TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }
}

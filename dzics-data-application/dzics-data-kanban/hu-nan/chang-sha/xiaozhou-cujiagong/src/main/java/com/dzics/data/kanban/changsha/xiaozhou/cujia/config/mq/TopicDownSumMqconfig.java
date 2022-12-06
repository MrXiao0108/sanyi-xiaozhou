package com.dzics.data.kanban.changsha.xiaozhou.cujia.config.mq;

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
public class TopicDownSumMqconfig {
    @Value("${center.device.downsum.queue}")
    private String queueName;
    @Value("${center.device.downsum.topicexchange}")
    private String topicexchange;
    @Value("${center.device.downsum.routingkey}")
    private String routingKey;

    @Bean(name = "queueDataDzicsDownSum")
    public Queue queueDataDzicsDownSum() {
        return new Queue(queueName, true);
    }

    @Bean(name = "exchangeDataDzicsDownSum")
    public TopicExchange exchangeDataDzicsDownSum() {
        return new TopicExchange(topicexchange);
    }

    @Bean(name = "bindingDownSum")
    public Binding bindingDownSum(@Qualifier("queueDataDzicsDownSum") Queue queue, @Qualifier("exchangeDataDzicsDownSum") TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }
}

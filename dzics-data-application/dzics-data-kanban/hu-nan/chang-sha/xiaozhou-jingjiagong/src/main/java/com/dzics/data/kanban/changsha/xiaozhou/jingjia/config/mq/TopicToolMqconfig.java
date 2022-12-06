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
public class TopicToolMqconfig {
    @Value("${center.device.tool.queue}")
    private String queueName;
    @Value("${center.device.tool.topicexchange}")
    private String topicexchange;
    @Value("${center.device.tool.routingkey}")
    private String routingKey;

    @Bean(name = "queueDataDzicsTool")
    public Queue queueDataDzicsTool() {
        return new Queue(queueName, true);
    }

    @Bean(name = "exchangeDataDzicsTool")
    public TopicExchange exchangeDataDzicsTool() {
        return new TopicExchange(topicexchange);
    }

    @Bean(name = "bindingTool")
    public Binding bindingTool(@Qualifier("queueDataDzicsTool") Queue queue, @Qualifier("exchangeDataDzicsTool") TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
    }
}

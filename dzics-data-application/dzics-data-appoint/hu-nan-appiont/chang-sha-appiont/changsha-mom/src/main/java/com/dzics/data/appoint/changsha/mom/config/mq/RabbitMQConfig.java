package com.dzics.data.appoint.changsha.mom.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private String queueName = "mom.tcp.dnc.program.queue";

    private String topicexchange = "mom.tcp.dnc.program.topicexchange";

    private String routingKey = "mom.tcp.dnc.program.routingkey";

    @Bean
    public Queue queueMOMTCPDNCProgram() {
        return new Queue(queueName, true);
    }

    @Bean
    public TopicExchange exchangeMOMTCPDNCProgram() {
        return new TopicExchange(topicexchange);
    }

    @Bean
    public Binding bindingMOMTCPDNCProgram() {
        return BindingBuilder
                .bind(this.queueMOMTCPDNCProgram())
                .to(this.exchangeMOMTCPDNCProgram())
                .with(routingKey);
    }
}

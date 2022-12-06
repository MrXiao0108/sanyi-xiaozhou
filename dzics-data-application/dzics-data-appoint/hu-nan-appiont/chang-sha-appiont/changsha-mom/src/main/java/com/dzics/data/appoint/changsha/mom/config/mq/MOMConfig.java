package com.dzics.data.appoint.changsha.mom.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MOMConfig {

    public static final String TOPIC_EXCHANGE = "mom.topic-exchange";

    @Bean
    public TopicExchange momExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    public static final String LOG_COMMUNICATION_QUEUE = "mom.log.communication.queue";

    public static final String LOG_COMMUNICATION_ROUTING = "mom.log.communication.routing";

    @Bean
    public Queue logCommunicationQueue() {
        return new Queue(LOG_COMMUNICATION_QUEUE, true);
    }

    @Bean
    public Binding bindingLogCommunication() {
        return BindingBuilder.bind(this.logCommunicationQueue()).to(this.momExchange()).with(LOG_COMMUNICATION_ROUTING);
    }
}

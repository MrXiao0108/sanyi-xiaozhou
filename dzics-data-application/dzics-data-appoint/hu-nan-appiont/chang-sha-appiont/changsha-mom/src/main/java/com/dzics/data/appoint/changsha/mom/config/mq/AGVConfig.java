package com.dzics.data.appoint.changsha.mom.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AGVConfig {

    public static final String HANDSHAKE_QUEUE = "mom.agv.handshake.queue";

    public static final String TOPIC_EXCHANGE = "mom.agv.topic-exchange";

    public static final String HANDSHAKE_ROUTING = "mom.agv.handshake.routing";

    @Bean
    public TopicExchange agvExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public Queue handshakeQueue() {
        return new Queue(HANDSHAKE_QUEUE, true);
    }

    @Bean
    public Binding bindingHandshake() {
        return BindingBuilder.bind(this.handshakeQueue()).to(this.agvExchange()).with(HANDSHAKE_ROUTING);
    }
}

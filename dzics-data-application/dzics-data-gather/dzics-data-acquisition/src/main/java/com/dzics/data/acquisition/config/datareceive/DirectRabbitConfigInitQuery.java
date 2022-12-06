package com.dzics.data.acquisition.config.datareceive;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化队列配置
 *
 * @author neverend
 */
@Configuration
public class DirectRabbitConfigInitQuery {
//============================================================================================
    /**
     * 累计数量基础数据底层发送过来的
     */
    @Value("${accq.read.cmd.queue.base}")
    private String queue;
    @Value("${accq.read.cmd.queue.base.exchange}")
    private String exchange;
    @Value("${accq.read.cmd.queue.base.routing}")
    private String routing;

    @Bean(name = "directQueueBase")
    public Queue directQueueBase() {
        return new Queue(queue, true);
    }

    @Bean("directExchangeBase")
    DirectExchange directExchangeBase() {
        return new DirectExchange(exchange, true, false);
    }

    @Bean("bindingDirectBase")
    Binding bindingDirectBase() {
        return BindingBuilder.bind(directQueueBase()).to(directExchangeBase()).with(routing);
    }

}


package com.dzics.data.kanban.changsha.xiaozhou.cujia.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xnb
 * @date 2022/11/25 0025 11:56
 */
@Configuration
public class DirectRunStateConfig {
    /**
     * 设备运行状态队列
     */
    @Value("${accq.read.cmd.queue.base.run.state}")
    private String queue;
    @Value("${accq.run.state.routing}")
    private String Routing;
    @Value("${accq.run.state.exchange}")
    private String Exchange;


    @Bean("RunStateExchange")
    DirectExchange couponDirectSignalExchange() {
        return new DirectExchange(Exchange, true, false);
    }

    @Bean(name = "RunStateQueue")
    public Queue carDirectPylseSignal() {
        return new Queue(queue, true, false, false);
    }

    @Bean("RunStateRouting")
    Binding bindingDirectSignalRouting() {
        return BindingBuilder.bind(carDirectPylseSignal()).to(couponDirectSignalExchange()).with(Routing);
    }
}

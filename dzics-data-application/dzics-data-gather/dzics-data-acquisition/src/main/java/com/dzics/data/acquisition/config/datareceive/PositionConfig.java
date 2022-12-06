package com.dzics.data.acquisition.config.datareceive;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname PositionConfig
 * @Description 描述
 * @Date 2022/3/14 11:12
 * @Created by NeverEnd
 */
@Configuration
public class PositionConfig {

    @Value("${accq.product.position.exchange}")
    private String positionExchange;

    @Bean(name = "directExchangeBaseposition")
    DirectExchange directExchangeBaseposition() {
        return new DirectExchange(positionExchange, true, false);
    }

    /**
     * 机器人报工看板队列
     * product position 生产产品当前工序
     */
    @Value("${accq.product.position.query}")
    private String positionQuery;
    @Value("${accq.product.position.routing}")
    private String positionRouting;

    @Bean(name = "directQueueposition")
    public Queue directQueueposition() {
        return new Queue(positionQuery, true);
    }

    @Bean(name = "bindingDirectBaseposition")
    Binding bindingDirectBaseposition() {
        return BindingBuilder.bind(directQueueposition()).to(directExchangeBaseposition()).with(positionRouting);
    }

    /**
     * 报工（临时码）处理队列
     */
    public final static String BAO_GONG_TEMP_CODE_QUEUE = "dzics-dev-gather-v1-product-position-temp-code";
    public final static String BAO_GONG_TEMP_CODE_ROUTING = "dzics-dev-gather-v1-product-position-temp-code-routing";

    @Bean
    public Queue baoGongTempCodeQueue() {
        return new Queue(BAO_GONG_TEMP_CODE_QUEUE, true);
    }

    @Bean
    public Binding baoGongTempCodeBinding() {
        return BindingBuilder
                .bind(baoGongTempCodeQueue())
                .to(directExchangeBaseposition())
                .with(BAO_GONG_TEMP_CODE_ROUTING);
    }
}

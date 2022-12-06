package com.dzics.data.acquisition.server.config;

import com.dzics.data.acquisition.server.netty.TCPServer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname NettyConfig
 * @Description 描述
 * @Date 2022/8/25 18:19
 * @Created by NeverEnd
 */
@Configuration
public class NettyConfig {

    private final TCPServer tcpServer;

    public NettyConfig(TCPServer tcpServer) {
        this.tcpServer = tcpServer;
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener() {
        return new ApplicationListener<ApplicationReadyEvent>() {
            @Override
            public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
                tcpServer.start();
            }
        };
    }
}

package com.dzics.data.appoint.changsha.mom;


import com.dzics.data.appoint.changsha.mom.server.netty.TCPServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.dzics.data"})
@MapperScan({"com.dzics.data.*.db.dao","com.dzics.data.appoint.changsha.mom.db.dao"})
@EnableCaching
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
public class ChangShaMomApp {
    public static void main(String[] args) {
        SpringApplication.run(ChangShaMomApp.class, args);
    }

    private final TCPServer tcpServer;

    public ChangShaMomApp(TCPServer tcpServer) {
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

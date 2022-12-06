package com.dzics.data.kanban.changsha.changpaoguang;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.dzics.data"})
@MapperScan("com.dzics.data.*.db.dao")
@EnableCaching
@EnableAsync
@EnableRabbit
public class ChangPaoGuangApp {
    public static void main(String[] args) {
        SpringApplication.run(ChangPaoGuangApp.class, args);
    }
}

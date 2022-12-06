package com.dzics.data.acquisition;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.dzics.data"})
@MapperScan("com.dzics.data.*.db.dao")
@EnableTransactionManagement
@EnableCaching
@EnableAsync
@EnableRabbit
public class DzicsDataAcquisition {

    public static void main(String[] args) {
        SpringApplication.run(DzicsDataAcquisition.class, args);
    }

}

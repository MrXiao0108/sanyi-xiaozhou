package com.dzics.data.kanban.changsha.xiaozhou.jingjia;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Classname XiaoZhouJingJiaApplication
 * @Description 描述
 * @Date 2022/4/7 8:55
 * @Created by NeverEnd
 */
@SpringBootApplication(scanBasePackages = {"com.dzics.data"})
@MapperScan("com.dzics.data.*.db.dao")
@EnableCaching
@EnableAsync
@EnableRabbit
public class XiaoZhouJingJiaApplication {
    public static void main(String[] args) {
        SpringApplication.run(XiaoZhouJingJiaApplication.class, args);
    }
}

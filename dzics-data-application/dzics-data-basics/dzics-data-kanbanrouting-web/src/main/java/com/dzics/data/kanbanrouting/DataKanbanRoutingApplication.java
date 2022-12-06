package com.dzics.data.kanbanrouting;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author ZhangChengJun
 * Date 2021/5/10.
 * @since
 */
@SpringBootApplication(scanBasePackages = {"com.dzics.data"})
@MapperScan("com.dzics.data.*.db.dao")
@EnableTransactionManagement
@EnableCaching
public class DataKanbanRoutingApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataKanbanRoutingApplication.class, args);
    }

}

package com.dzics.data.udp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author ZhangChengJun
 * Date 2022/1/12.
 * @since
 */
@SpringBootApplication(scanBasePackages = "com.dzics")
@EnableAsync
public class UdpApplication {

    public static void main(String[] args) {
        SpringApplication.run(UdpApplication.class, args);
    }
}

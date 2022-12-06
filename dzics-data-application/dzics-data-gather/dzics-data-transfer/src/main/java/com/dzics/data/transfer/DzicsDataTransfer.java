package com.dzics.data.transfer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.dzics.data"})
public class DzicsDataTransfer {

    public static void main(String[] args) {
        SpringApplication.run(DzicsDataTransfer.class, args);
    }

}

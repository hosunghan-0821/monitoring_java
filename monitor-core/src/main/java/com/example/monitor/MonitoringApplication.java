package com.example.monitor;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = {"com.example.monitor", "firefox","chrome","s3","module.discord"})
public class MonitoringApplication {

    public static void main(String[] args) {
        Thread.currentThread().setName("Main-Thread");
        SpringApplication.run(MonitoringApplication.class, args);
    }

}

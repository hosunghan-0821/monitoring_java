package com.dopee.sync;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication(scanBasePackages = {"com.dopee.sync","module.database"})
public class DataBaseCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataBaseCoreApplication.class, args);
    }

}

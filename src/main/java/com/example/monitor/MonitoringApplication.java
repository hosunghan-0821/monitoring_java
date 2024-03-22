package com.example.monitor;

import com.example.monitor.chrome.ChromeDriverManager;
import com.example.monitor.monitoring.MonitorCore;
import com.example.monitor.monitoring.Product;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

import static com.example.monitor.monitoring.ElementFindString.*;

@EnableScheduling
@RequiredArgsConstructor
@SpringBootApplication
public class MonitoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitoringApplication.class, args);
    }

}

package com.example.monitor.chrome;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;

@Component
public class ChromeDriverManager {

    private final ChromeDriver chromeDriver;

    private final WebDriverWait wait;

    public ChromeDriverManager() {

        ChromeOptions chromeOptions = setOptions();
        chromeDriver = new ChromeDriver(chromeOptions);
        wait = new WebDriverWait(chromeDriver, Duration.ofMillis(5000)); // 최대 5초 대기
    }

    public ChromeDriver getChromeDriver() {
        assert (chromeDriver != null);
        return chromeDriver;
    }

    public WebDriverWait getWebDriverWait(){
        assert (wait != null);
        return wait;
    }

    private ChromeOptions setOptions() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("window-size=1920x1080");
        options.addArguments("start-maximized");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-automation");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("detach", true);
        return options;
    }
}

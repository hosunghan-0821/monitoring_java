package com.example.monitor.chrome;


import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;

@Component
public class ChromeDriverToolFactory {

    private final HashMap<String, ChromeDriverTool> factroyHashMap = new HashMap<String, ChromeDriverTool>();

    public ChromeDriverTool getChromeDriverTool(String key) {
        return factroyHashMap.get(key);
    }
    public void makeChromeDriverTool(String key) {
        ChromeDriver chromeDriver = new ChromeDriver(setOptions());
        WebDriverWait wait = new WebDriverWait(chromeDriver, Duration.ofMillis(5000)); // 최대 5초 대기
        ChromeDriverTool chromeDriverTool = new ChromeDriverTool(chromeDriver, wait, new HashMap<>());

        factroyHashMap.put(key, chromeDriverTool);
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

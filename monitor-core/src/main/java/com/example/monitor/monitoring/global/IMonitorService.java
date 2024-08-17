package com.example.monitor.monitoring.global;

import chrome.ChromeDriverTool;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public interface IMonitorService {

    void runLoadLogic(ChromeDriverTool chromeDriverTool);

    void runFindProductLogic(ChromeDriverTool chromeDriverTool);

    void login(ChromeDriver driver, WebDriverWait wait);
}

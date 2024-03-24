package com.example.monitor.chrome;


import com.example.monitor.monitoring.julian.JulianProduct;
import lombok.Getter;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class ChromeDriverTool {

    private final ChromeDriver chromeDriver;

    private final WebDriverWait webDriverWait;

    private final HashMap<String, JulianProduct> dataHashMap;

    private final ReentrantLock functionLock;

    private boolean isLoadData = false;
    public ChromeDriverTool(ChromeDriver chromeDriver, WebDriverWait webDriverWait, HashMap<String, JulianProduct> dataHashMap) {
        this.chromeDriver = chromeDriver;
        this.webDriverWait = webDriverWait;
        this.dataHashMap = dataHashMap;
        functionLock = new ReentrantLock();
    }

    public void isLoadData(boolean bool){
        isLoadData = bool;
    }
}

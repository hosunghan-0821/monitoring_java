package com.example.monitor.chrome;


import com.example.monitor.monitoring.julian.JulianProduct;
import lombok.Getter;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class ChromeDriverTool {

    private final ChromeDriver chromeDriver;

    private final WebDriverWait webDriverWait;

    private final HashMap<String, JulianProduct> dataHashMap;

    private final HashSet<String> productKeySet;

    private boolean isLoadData = false;

    private boolean isRunning = true;

    public ChromeDriverTool(ChromeDriver chromeDriver, WebDriverWait webDriverWait, HashMap<String, JulianProduct> dataHashMap, HashSet<String> productKeySet) {
        this.chromeDriver = chromeDriver;
        this.webDriverWait = webDriverWait;
        this.dataHashMap = dataHashMap;
        this.productKeySet = productKeySet;
    }

    public void isLoadData(boolean bool){
        isLoadData = bool;
    }

    public void isRunning(boolean bool) {isRunning = bool;}
}

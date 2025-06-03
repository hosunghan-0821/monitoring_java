package com.example.monitor.monitoring.global;

import org.openqa.selenium.By;

public interface Locator {
    String selector();

    default By byXpath() {
        return By.xpath(selector());
    }
    default By byId(){
        return By.id(selector());
    }
}

package com.example.monitor.scheduler;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.chrome.ChromeDriverToolFactory;
import com.example.monitor.discord.DiscordBot;
import com.example.monitor.monitoring.MonitorCore;
import com.example.monitor.monitoring.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.monitor.discord.DiscordString.ALL_CATEGORIES_CHANNEL;
import static com.example.monitor.discord.DiscordString.PROMO_CHANNEL;
import static com.example.monitor.monitoring.ElementFindString.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MonitorScheduler {

    private final MonitorCore monitorCore;


    private final ChromeDriverToolFactory chromeDriverToolFactory;


    @Scheduled(initialDelay = 60000, fixedDelay = 60000)// 1분마다 실행
    public void monitoring() {
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(ALL_CATEGORIES);
        monitorCore.runFindProductLogic(chromeDriverTool, ALL_CATEGORIES_URL, ALL_CATEGORIES, ALL_CATEGORIES_CHANNEL);

    }

    @Scheduled(initialDelay = 60000, fixedDelay = 60000)// 10분마다 실행
    public void monitorCategoryPromo() {

        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(PROMO);
        monitorCore.runFindProductLogic(chromeDriverTool, PROMO_URL, PROMO, PROMO_CHANNEL);
    }
}

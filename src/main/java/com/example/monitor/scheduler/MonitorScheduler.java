package com.example.monitor.scheduler;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.julian.JulianMonitorCore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.monitor.discord.DiscordString.ALL_CATEGORIES_CHANNEL;
import static com.example.monitor.discord.DiscordString.PROMO_CHANNEL;
import static com.example.monitor.monitoring.julian.JulianFindString.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MonitorScheduler {

    private final JulianMonitorCore julianMonitorCore;


    private final ChromeDriverToolFactory chromeDriverToolFactory;


//    @Scheduled(initialDelay = 60000, fixedDelay = 60000)// 1분마다 실행
    public void monitoring() {
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(ALL_CATEGORIES);
        julianMonitorCore.runFindProductLogic(chromeDriverTool, ALL_CATEGORIES_URL, ALL_CATEGORIES, ALL_CATEGORIES_CHANNEL);

    }
//    @Scheduled(initialDelay = 60000, fixedDelay = 600000)// 10분마다 실행
    public void monitorCategoryPromo() {
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(PROMO);
        julianMonitorCore.runFindProductLogic(chromeDriverTool, PROMO_URL, PROMO, PROMO_CHANNEL);
    }
}

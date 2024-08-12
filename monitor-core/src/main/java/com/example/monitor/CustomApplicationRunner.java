package com.example.monitor;

import chrome.ChromeDriverTool;
import chrome.ChromeDriverToolFactory;
import com.example.monitor.infra.discord.DiscordBot;

import com.example.monitor.monitoring.biffi.BiffiMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFMonitorCore;
import com.example.monitor.monitoring.gebnegozi.GebenegoziMonitorCore;
import com.example.monitor.monitoring.julian.JulianMonitorCore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import s3.service.S3UploaderService;

import static com.example.monitor.monitoring.julian.JulianFindString.ALL_CATEGORIES;
import static com.example.monitor.monitoring.julian.JulianFindString.JULIAN_LOG_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class CustomApplicationRunner implements ApplicationRunner {

    private final ChromeDriverToolFactory chromeDriverToolFactory;


    private final JulianMonitorCore julianMonitorCore;

    private final DoubleFMonitorCore doubleFMonitorCore;

    private final BiffiMonitorCore biffiMonitorCore;

    private final GebenegoziMonitorCore gebenegoziMonitorCore;

    private final DiscordBot discordBot;

    private final S3UploaderService s3UploaderService;


    @Override
    public void run(ApplicationArguments args) throws Exception {

//        chromeDriverToolFactory.makePrivateChromeDriverTool("test");
//
//        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool("test");
//
//        ChromeDriver driver = chromeDriverTool.getChromeDriver();
//        WebDriverWait wait = chromeDriverTool.getWebDriverWait();
//
//        driver.get("https://stores.antonioli.eu");


//        chromeDriverToolFactory.makeChromeDriverTool(DOUBLE_F);
//        chromeDriverToolFactory.makeChromeDriverTool(ALL_CATEGORIES);
//        chromeDriverToolFactory.makeChromeDriverTool(PROMO);
//        chromeDriverToolFactory.makeChromeDriverTool(BIFFI);
//        chromeDriverToolFactory.makeChromeDriverTool(GEBE);
//        discordBot.setChromeDriverTool(chromeDriverToolFactory);
//        discordBot.setS3UploaderService(s3UploaderService);
//
//
//        Thread gebeneThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                log.info(GEBENE_LOG_PREFIX + "============================ Load GEBENE Product Start ============================");
//                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(GEBE);
//                gebenegoziMonitorCore.runLoadLogic(chromeDriverTool);
//                log.info(GEBENE_LOG_PREFIX + "============================ Load GEBENE Product Finish ============================");
//            }
//        });
//
//        gebeneThread.start();
//
//        Thread biffiThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                log.info(BIFFI_LOG_PREFIX + "============================ Load BIFFI Product Start ============================");
//                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(BIFFI);
//                biffiMonitorCore.runLoadLogic(chromeDriverTool);
//                log.info(BIFFI_LOG_PREFIX + "============================ Load BIFFI Product Finish ============================");
//            }
//        });
//
//        biffiThread.start();
//
//
//        Thread doubleFThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(DOUBLE_F);
//                log.info(DOUBLE_F_LOG_PREFIX + "============================ Load DOUBLE_F Product Start ============================");
//                doubleFMonitorCore.runLoadLogic(chromeDriverTool);
//                log.info(DOUBLE_F_LOG_PREFIX + "============================ Load DOUBLE_F Product Finish ============================");
//            }
//        });
//        doubleFThread.start();
//
//
//        Thread julianThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(ALL_CATEGORIES);
//                log.info(JULIAN_LOG_PREFIX + "============================ Load Julian Product Start ============================");
//                julianMonitorCore.runLoadLogic(chromeDriverTool);
//                log.info(JULIAN_LOG_PREFIX + "============================ Load Julian Product Finish ============================");
//
//            }
//        });
//
//        julianThread.start();


    }
}

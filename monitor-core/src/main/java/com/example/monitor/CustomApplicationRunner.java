package com.example.monitor;

import chrome.ChromeDriverTool;
import chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.biffi.BiffiMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFMonitorCore;
import com.example.monitor.monitoring.eic.EicMonitorCore;
import com.example.monitor.monitoring.gebnegozi.GebenegoziMonitorCore;
import com.example.monitor.monitoring.julian.JulianMonitorCore;
import com.example.monitor.monitoring.style.StyleMonitorCore;
import com.example.monitor.monitoring.vietti.ViettiMonitorCore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.discord.DiscordBot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.MDC;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import s3.service.S3UploaderService;

import java.util.List;

import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI;
import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI_LOG_PREFIX;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F_LOG_PREFIX;
import static com.example.monitor.monitoring.eic.EicFindString.EIC;
import static com.example.monitor.monitoring.eic.EicFindString.EIC_DISCOUNT;
import static com.example.monitor.monitoring.eic.EicFindString.EIC_LOG_PREFIX;
import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.GNB;
import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.GEBENE_LOG_PREFIX;
import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.GNB_STONE_ISLAND;
import static com.example.monitor.monitoring.julian.JulianFindString.ALL_CATEGORIES;
import static com.example.monitor.monitoring.julian.JulianFindString.JULIAN;
import static com.example.monitor.monitoring.julian.JulianFindString.JULIAN_LOG_PREFIX;
import static com.example.monitor.monitoring.style.StyleFindString.STYLE;
import static com.example.monitor.monitoring.style.StyleFindString.STYLE_LOG_PREFIX;
import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI;
import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI_LOG_PREFIX;

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

    private final StyleMonitorCore styleMonitorCore;

    private final ViettiMonitorCore viettiMonitorCore;

    private final EicMonitorCore eicMonitorCore;

    private final DiscordBot discordBot;

    private final S3UploaderService s3UploaderService;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        chromeDriverToolFactory.makeChromeDriverTool(STYLE);
        chromeDriverToolFactory.makeChromeDriverTool(ALL_CATEGORIES);
        chromeDriverToolFactory.makeChromeDriverTool(DOUBLE_F);
        chromeDriverToolFactory.makeChromeDriverTool(BIFFI);
        chromeDriverToolFactory.makeChromeDriverTool(GNB,1500);
        chromeDriverToolFactory.makeChromeDriverTool(GNB_STONE_ISLAND,3000);
        chromeDriverToolFactory.makeChromeDriverTool(VIETTI);
        chromeDriverToolFactory.makeChromeDriverTool(EIC);
        chromeDriverToolFactory.makeChromeDriverTool(EIC_DISCOUNT);

        discordBot.setChromeDriverTool(chromeDriverToolFactory);
        discordBot.setS3UploaderService(s3UploaderService);

        Thread eicThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MDC.put("threadName", EIC); // MDC에 쓰레드 이름 저장
                log.info(EIC_LOG_PREFIX + "============================ Load Eic Product Start ============================");
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(EIC);
                eicMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(EIC_LOG_PREFIX + "============================ Load Eic Product Finish ============================");

                MDC.clear(); // MDC 데이터 정리
            }
        });
        eicThread.setName(EIC);
        eicThread.start();

        Thread eicDiscountThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MDC.put("threadName", EIC_DISCOUNT); // MDC에 쓰레드 이름 저장
                log.info(EIC_LOG_PREFIX + "============================ Load Eic DISCOUNT Product Start ============================");
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(EIC_DISCOUNT);
                eicMonitorCore.runLoadLogicDiscountChange(chromeDriverTool);
                log.info(EIC_LOG_PREFIX + "============================ Load Eic DISCOUNT Product Finish ============================");

                MDC.clear(); // MDC 데이터 정리
            }
        });
        eicDiscountThread.setName(EIC_DISCOUNT);
        eicDiscountThread.start();

        Thread viettiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MDC.put("threadName", VIETTI); // MDC에 쓰레드 이름 저장
                log.info(VIETTI_LOG_PREFIX + "============================ Load Vietti Product Start ============================");
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(VIETTI);
                viettiMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(VIETTI_LOG_PREFIX + "============================ Load Vietti Product Finish ============================");
                MDC.clear(); // MDC 데이터 정리

            }
        });
        viettiThread.setName(VIETTI);
        viettiThread.start();


        Thread styleThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MDC.put("threadName", STYLE); // MDC에 쓰레드 이름 저장
                log.info(STYLE_LOG_PREFIX + "============================ Load Style Product Start ============================");
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(STYLE);
                styleMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(STYLE_LOG_PREFIX + "============================ Load Style Product Finish ============================");
                MDC.clear(); // MDC 데이터 정리
            }
        });
        styleThread.setName(STYLE);
        styleThread.start();

        Thread gebeneThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Load만 동일하게
                log.info(GEBENE_LOG_PREFIX + "============================ Load GEBENE STONE ISLAND Product Start ============================");
                ChromeDriverTool chromeDriverToolStone = chromeDriverToolFactory.getChromeDriverTool(GNB_STONE_ISLAND);
                gebenegoziMonitorCore.runLoadLogicStoneIsland(chromeDriverToolStone);
                log.info(GEBENE_LOG_PREFIX + "============================ Load GEBENE STONE ISLAND Product Finish ============================");


                MDC.put("threadName", GNB); // MDC에 쓰레드 이름 저장
                log.info(GEBENE_LOG_PREFIX + "============================ Load GEBENE Product Start ============================");
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(GNB);
                gebenegoziMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(GEBENE_LOG_PREFIX + "============================ Load GEBENE Product Finish ============================");
                MDC.clear(); // MDC 데이터 정리
            }
        });
        gebeneThread.setName(GNB);
        gebeneThread.start();

        Thread biffiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MDC.put("threadName", BIFFI); // MDC에 쓰레드 이름 저장
                log.info(BIFFI_LOG_PREFIX + "============================ Load BIFFI Product Start ============================");
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(BIFFI);
                biffiMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(BIFFI_LOG_PREFIX + "============================ Load BIFFI Product Finish ============================");
                MDC.clear(); // MDC 데이터 정리
            }
        });
        biffiThread.setName(BIFFI);
        biffiThread.start();


        Thread doubleFThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(DOUBLE_F);
                try {
                    Thread.sleep(1000);
                    chromeDriverTool.getChromeDriver().get("https://www.thedoublef.com");
                    Thread.sleep(1000);
                } catch (Exception e) {

                }

                MDC.put("threadName", DOUBLE_F); // MDC에 쓰레드 이름 저장
                log.info(DOUBLE_F_LOG_PREFIX + "============================ Load DOUBLE_F Product Start ============================");
                doubleFMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(DOUBLE_F_LOG_PREFIX + "============================ Load DOUBLE_F Product Finish ============================");
                MDC.clear(); // MDC 데이터 정리
            }
        });
        doubleFThread.setName(DOUBLE_F);
        doubleFThread.start();


        Thread julianThread = new Thread(new Runnable() {
            @Override
            public void run() {
                MDC.put("threadName", JULIAN); // MDC에 쓰레드 이름 저장
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(ALL_CATEGORIES);
                log.info(JULIAN_LOG_PREFIX + "============================ Load Julian Product Start ============================");
                julianMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(JULIAN_LOG_PREFIX + "============================ Load Julian Product Finish ============================");
                MDC.clear(); // MDC 데이터 정리

            }
        });
        julianThread.setName(JULIAN);
        julianThread.start();

    }

    public void doRun(ChromeDriver driver, List<WebElement> elements, int startIndex, int totalSizes) {

        if (totalSizes == startIndex) {
            return;
        }
        for (int i = startIndex; i < elements.size(); i++) {
            WebElement productElement = elements.get(i);

            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }

            Actions actions = new Actions(driver);
            actions.moveToElement(productElement);
            actions.perform();
        }

        elements = driver.findElements(By.xpath("//div[@id='searchedItemDisplay']//ul//li[@class='jt-goods-list-elem']"));

        doRun(driver, elements, totalSizes, elements.size());

    }
}

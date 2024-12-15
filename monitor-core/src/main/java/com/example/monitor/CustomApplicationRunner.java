package com.example.monitor;

import chrome.ChromeDriverTool;
import chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.antonioli.AntonioliMonitorCore;
import com.example.monitor.monitoring.eic.EicMonitorCore;
import com.example.monitor.monitoring.style.StyleFindString;
import com.example.monitor.monitoring.vietti.ViettiFindString;
import com.example.monitor.monitoring.vietti.ViettiMonitorCore;
import com.example.monitor.monitoring.vietti.ViettiProduct;
import module.discord.DiscordBot;

import com.example.monitor.monitoring.biffi.BiffiMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFMonitorCore;
import com.example.monitor.monitoring.gebnegozi.GebenegoziMonitorCore;
import com.example.monitor.monitoring.julian.JulianMonitorCore;
import com.example.monitor.monitoring.style.StyleMonitorCore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import s3.service.S3UploaderService;

import java.util.Collections;

import static com.example.monitor.monitoring.antonioli.AntonioliFindString.ANTONIOLI;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.ANTONIOLI_LOG_PREFIX;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.ANTONIOLI_MAIN_URL;
import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI;
import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI_LOG_PREFIX;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F_LOG_PREFIX;
import static com.example.monitor.monitoring.eic.EicFindString.EIC;
import static com.example.monitor.monitoring.eic.EicFindString.EIC_LOG_PREFIX;
import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.GEBE;
import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.GEBENE_LOG_PREFIX;
import static com.example.monitor.monitoring.julian.JulianFindString.ALL_CATEGORIES;
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
        chromeDriverToolFactory.makeChromeDriverTool(GEBE);
        chromeDriverToolFactory.makeChromeDriverTool(VIETTI, 60000);
        chromeDriverToolFactory.makeChromeDriverTool(EIC);

        discordBot.setChromeDriverTool(chromeDriverToolFactory);
        discordBot.setS3UploaderService(s3UploaderService);

        Thread eicThread = new Thread(new Runnable() {
            @Override
            public void run() {

                log.info(EIC_LOG_PREFIX + "============================ Load Eic Product Start ============================");
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(EIC);
                eicMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(EIC_LOG_PREFIX + "============================ Load Eic Product Finish ============================");
            }
        });
        eicThread.start();

        Thread viettiThread = new Thread(new Runnable() {
            @Override
            public void run() {

                log.info(VIETTI_LOG_PREFIX + "============================ Load Vietti Product Start ============================");
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(VIETTI);
                viettiMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(VIETTI_LOG_PREFIX + "============================ Load Vietti Product Finish ============================");
            }
        });
        viettiThread.start();


        Thread styleThread = new Thread(new Runnable() {
            @Override
            public void run() {

                log.info(STYLE_LOG_PREFIX + "============================ Load Style Product Start ============================");
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(STYLE);
                styleMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(STYLE_LOG_PREFIX + "============================ Load Style Product Finish ============================");
            }
        });
        styleThread.start();

        Thread gebeneThread = new Thread(new Runnable() {
            @Override
            public void run() {

                log.info(GEBENE_LOG_PREFIX + "============================ Load GEBENE Product Start ============================");
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(GEBE);
                gebenegoziMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(GEBENE_LOG_PREFIX + "============================ Load GEBENE Product Finish ============================");
            }
        });

        gebeneThread.start();

        Thread biffiThread = new Thread(new Runnable() {
            @Override
            public void run() {

                log.info(BIFFI_LOG_PREFIX + "============================ Load BIFFI Product Start ============================");
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(BIFFI);
                biffiMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(BIFFI_LOG_PREFIX + "============================ Load BIFFI Product Finish ============================");
            }
        });

        biffiThread.start();


        Thread doubleFThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(DOUBLE_F);
                log.info(DOUBLE_F_LOG_PREFIX + "============================ Load DOUBLE_F Product Start ============================");
                doubleFMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(DOUBLE_F_LOG_PREFIX + "============================ Load DOUBLE_F Product Finish ============================");
            }
        });
        doubleFThread.start();


        Thread julianThread = new Thread(new Runnable() {
            @Override
            public void run() {

                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(ALL_CATEGORIES);
                log.info(JULIAN_LOG_PREFIX + "============================ Load Julian Product Start ============================");
                julianMonitorCore.runLoadLogic(chromeDriverTool);
                log.info(JULIAN_LOG_PREFIX + "============================ Load Julian Product Finish ============================");

            }
        });

        julianThread.start();
    }
}

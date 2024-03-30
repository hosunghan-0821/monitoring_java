package com.example.monitor;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.chrome.ChromeDriverToolFactory;
import com.example.monitor.discord.DiscordBot;
import com.example.monitor.monitoring.dobulef.DoubleFBrandHashMap;
import com.example.monitor.monitoring.dobulef.DoubleFMonitorCore;
import com.example.monitor.monitoring.julian.JulianMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFFindString;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import com.example.monitor.monitoring.julian.JulianProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;
import static com.example.monitor.monitoring.julian.JulianFindString.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomApplicationRunner implements ApplicationRunner {

    private final ChromeDriverToolFactory chromeDriverToolFactory;


    private final JulianMonitorCore julianMonitorCore;

    private final DoubleFMonitorCore doubleFMonitorCore;

    private final DiscordBot discordBot;

    @Override
    public void run(ApplicationArguments args) throws Exception {


        chromeDriverToolFactory.makeChromeDriverTool(DOUBLE_F);
        chromeDriverToolFactory.makeChromeDriverTool(ALL_CATEGORIES);
        chromeDriverToolFactory.makeChromeDriverTool(PROMO);
        discordBot.setChromeDriverTool(chromeDriverToolFactory);


        Thread doubleFThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(DOUBLE_F);
                log.info("============================ Load DOUBLE_F Product Start ============================");
                doubleFMonitorCore.runLoadLogic(chromeDriverTool);
                log.info("============================ Load DOUBLE_F Product Finish ============================");
            }
        });
        doubleFThread.start();


        Thread julianThread = new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("============================ Load Julian Product Start ============================");
                try {
                    //로그인
                    ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(ALL_CATEGORIES);
                    ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
                    WebDriverWait wait = chromeDriverTool.getWebDriverWait();
                    julianMonitorCore.login(chromeDriver);

                    for (int i = 1; i < 3; i++) {
                        //페이지 이동
                        julianMonitorCore.changeUrl(chromeDriver, ALL_CATEGORIES_URL + "?page=" + i);

                        //하위 데이터
                        List<WebElement> productDataDivs = julianMonitorCore.getInnerProductDivs(wait);

                        //상품 하위 데이터 조회
                        List<JulianProduct> productData = julianMonitorCore.getProductData(productDataDivs);

                        //정보가져오기
                        julianMonitorCore.loadData(chromeDriverTool.getDataHashMap(), productData);

                    }
                    //로드체크
                    chromeDriverTool.isLoadData(true);
                    log.info("== ALL CATEGORIES LOAD DATA FINISH ==");
                } catch (Exception e) {
                    log.error("All Category Data Load Error");
                    e.printStackTrace();
                }


                try {
                    //로그인
                    ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(PROMO);
                    ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
                    WebDriverWait wait = chromeDriverTool.getWebDriverWait();
                    julianMonitorCore.login(chromeDriver);

                    for (int i = 1; i < 3; i++) {
                        //페이지 이동
                        julianMonitorCore.changeUrl(chromeDriver, PROMO_URL + "?page=" + i);

                        //하위 데이터
                        List<WebElement> productDataDivs = julianMonitorCore.getInnerProductDivs(wait);

                        //상품 하위 데이터 조회
                        List<JulianProduct> productData = julianMonitorCore.getProductData(productDataDivs);

                        //정보가져오기
                        julianMonitorCore.loadData(chromeDriverTool.getDataHashMap(), productData);

                    }
                    //Load 확인
                    chromeDriverTool.isLoadData(true);
                    log.info("== PROMO LOAD DATA FINISH ==");
                } catch (Exception e) {
                    log.error("== PROMO LOAD DATA ERROR ==");
                    e.printStackTrace();
                }

                log.info("============================ Load Julian Product Finish ============================");
            }
        });

        julianThread.start();


    }
}

package com.example.monitor;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.chrome.ChromeDriverToolFactory;
import com.example.monitor.infra.converter.controller.IConverterFacade;
import com.example.monitor.infra.converter.dto.ConvertProduct;
import com.example.monitor.infra.discord.DiscordBot;
import com.example.monitor.infra.sender.ProductSender;
import com.example.monitor.infra.sender.SearchProduct;
import com.example.monitor.monitoring.biffi.BiffiMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import com.example.monitor.monitoring.julian.JulianMonitorCore;
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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.example.monitor.monitoring.biffi.BiffiFindString.*;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;
import static com.example.monitor.monitoring.julian.JulianFindString.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class CustomApplicationRunner implements ApplicationRunner {

    private final ChromeDriverToolFactory chromeDriverToolFactory;


    private final JulianMonitorCore julianMonitorCore;

    private final DoubleFMonitorCore doubleFMonitorCore;

    private final BiffiMonitorCore biffiMonitorCore;

    private final DiscordBot discordBot;

    private final ProductSender productSender;

    private final IConverterFacade iConverterFacade;

    @Override
    public void run(ApplicationArguments args) throws Exception {


        chromeDriverToolFactory.makeChromeDriverTool(DOUBLE_F);
        chromeDriverToolFactory.makeChromeDriverTool(ALL_CATEGORIES);
        chromeDriverToolFactory.makeChromeDriverTool(PROMO);
        chromeDriverToolFactory.makeChromeDriverTool(BIFFI);
        discordBot.setChromeDriverTool(chromeDriverToolFactory);


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
                log.info(JULIAN_LOG_PREFIX + "============================ Load Julian Product Start ============================");
                try {
                    //로그인
                    ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(ALL_CATEGORIES);

                    ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
                    WebDriverWait wait = chromeDriverTool.getWebDriverWait();
                    HashMap<String, JulianProduct> brandHashMap = julianMonitorCore.getJulianBrandHashData().getBrandHashMap(ALL_CATEGORIES);


                    julianMonitorCore.login(chromeDriver, wait);

                    for (int i = 1; i < 3; i++) {
                        String url = julianMonitorCore.getUrl(ALL_CATEGORIES_URL, i);
                        //페이지 이동
                        julianMonitorCore.changeUrl(chromeDriver, url);

                        //하위 데이터
                        List<WebElement> productDataDivs = julianMonitorCore.getInnerProductDivs(wait);

                        //상품 하위 데이터 조회
                        List<JulianProduct> productData = julianMonitorCore.getProductData(productDataDivs, url);

                        //정보가져오기
                        julianMonitorCore.loadData(brandHashMap, productData);

                    }
                    //로드체크
                    chromeDriverTool.isLoadData(true);
                    log.info(JULIAN_LOG_PREFIX + "== ALL CATEGORIES LOAD DATA FINISH ==");
                } catch (Exception e) {
                    log.error(JULIAN_LOG_PREFIX + "All Category Data Load Error");
                    e.printStackTrace();
                }


                try {
                    //로그인
                    ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(PROMO);
                    ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
                    WebDriverWait wait = chromeDriverTool.getWebDriverWait();
                    HashMap<String, JulianProduct> brandHashMap = julianMonitorCore.getJulianBrandHashData().getBrandHashMap(PROMO);

                    julianMonitorCore.login(chromeDriver, wait);

                    for (int i = 1; i < 3; i++) {
                        String url = julianMonitorCore.getUrl(PROMO_URL, i);
                        //페이지 이동
                        julianMonitorCore.changeUrl(chromeDriver, url);

                        //하위 데이터
                        List<WebElement> productDataDivs = julianMonitorCore.getInnerProductDivs(wait);

                        //상품 하위 데이터 조회
                        List<JulianProduct> productData = julianMonitorCore.getProductData(productDataDivs, url);

                        //정보가져오기
                        julianMonitorCore.loadData(brandHashMap, productData);

                    }
                    //Load 확인
                    chromeDriverTool.isLoadData(true);
                    log.info(JULIAN_LOG_PREFIX + "== PROMO LOAD DATA FINISH ==");
                } catch (Exception e) {
                    log.error(JULIAN_LOG_PREFIX + "== PROMO LOAD DATA ERROR ==");
                    e.printStackTrace();
                }

                log.info(JULIAN_LOG_PREFIX + "============================ Load Julian Product Finish ============================");
            }
        });

        julianThread.start();

//
        // 브랜드 체크 코드
//        chromeDriverToolFactory.makeChromeDriverTool(DOUBLE_F);
//        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(DOUBLE_F);
//        ChromeDriver driver = chromeDriverTool.getChromeDriver();
//        WebDriverWait wait = chromeDriverTool.getWebDriverWait();
//
//        driver.get(DOUBLE_F_MAIN_PAGE);
//        doubleFMonitorCore.acceptCookie(wait);
//        doubleFMonitorCore.login(driver, wait);
//        String url = "https://www.thedoublef.com/bu_en/" + "woman" + "/designers/" + "new-balance" + "/";
//        List<DoubleFProduct> pageProductData = doubleFMonitorCore.getPageProductData(driver, wait, url, "new-balance");
//
//
//        for (DoubleFProduct doubleFProduct : pageProductData) {
//            log.info(doubleFProduct.toString());
//        }
//
//        List<ConvertProduct> convertProductList = pageProductData.stream()
//                .map(v -> v.changeToConvertProduct(DOUBLE_F))
//                .collect(Collectors.toList());
//
//        iConverterFacade.convertProduct(convertProductList);


    }
}

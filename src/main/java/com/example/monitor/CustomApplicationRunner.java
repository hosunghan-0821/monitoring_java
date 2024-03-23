package com.example.monitor;

import com.example.monitor.chrome.ChromeDriverManager;
import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.MonitorCore;
import com.example.monitor.monitoring.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static com.example.monitor.monitoring.ElementFindString.ALL_CATEGORIES_URL;
import static com.example.monitor.monitoring.ElementFindString.PROMO_URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomApplicationRunner implements ApplicationRunner {

    private final ChromeDriverToolFactory chromeDriverToolFactory;

    private final ChromeDriverManager chromeDriverManager;

    private final MonitorCore monitorCore;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        chromeDriverToolFactory.makeChromeDriverTool("ALL_CATEGORIES");
        chromeDriverToolFactory.makeChromeDriverTool("PROMO");

        log.info("Load Data From Site");


        try {
            //로그인
            ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool("ALL_CATEGORIES");
            ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
            WebDriverWait wait = chromeDriverTool.getWebDriverWait();
            monitorCore.login(chromeDriver);

            for (int i = 1; i < 3; i++) {
                //페이지 이동
                monitorCore.changeUrl(chromeDriver, ALL_CATEGORIES_URL + "?page=" + i);

                //하위 데이터
                List<WebElement> productDataDivs = monitorCore.getInnerProductDivs(wait);

                //상품 하위 데이터 조회
                List<Product> productData = monitorCore.getProductData(productDataDivs);

                //정보가져오기
                monitorCore.loadData(chromeDriverTool.getDataHashMap(),productData);
                chromeDriverTool.isLoadData(true);
            }
            System.out.println("ALl CATEGORIES HASH MAP" + chromeDriverTool.getDataHashMap().size());
        } catch (Exception e) {
            log.error("All Category Data Load Error");
            e.printStackTrace();
        }

        try {
            //로그인
            ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool("PROMO");
            ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
            WebDriverWait wait = chromeDriverTool.getWebDriverWait();
            monitorCore.login(chromeDriver);

            for (int i = 1; i < 3; i++) {
                //페이지 이동
                monitorCore.changeUrl(chromeDriver, PROMO_URL + "?page=" + i);

                //하위 데이터
                List<WebElement> productDataDivs = monitorCore.getInnerProductDivs(wait);

                //상품 하위 데이터 조회
                List<Product> productData = monitorCore.getProductData(productDataDivs);

                //정보가져오기
                monitorCore.loadData(chromeDriverTool.getDataHashMap(),productData);
                chromeDriverTool.isLoadData(true);
            }
            System.out.println("PROMO HASH MAP"+chromeDriverTool.getDataHashMap().size());
        } catch (Exception e) {
            log.error("PROMO Data Load Error");
            e.printStackTrace();
        }


    }
}

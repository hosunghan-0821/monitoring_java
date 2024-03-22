package com.example.monitor;

import com.example.monitor.chrome.ChromeDriverManager;
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

import static com.example.monitor.monitoring.ElementFindString.ALL_CATEGORIES_URL;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomApplicationRunner implements ApplicationRunner {

    private final ChromeDriverManager chromeDriverManager;

    private final MonitorCore monitorCore;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        log.info("Load Data From Site");
        monitorCore.getMonitoringLock().lock();
        log.info("Load Data GetLock");
        monitorCore.setChromeDriver(chromeDriverManager.getChromeDriver());
        monitorCore.setWebDriverWait(chromeDriverManager.getWebDriverWait());

        try {
            //로그인
            monitorCore.login();

            for (int i = 1; i < 3; i++) {
                //페이지 이동
                monitorCore.changeUrl(ALL_CATEGORIES_URL + "?page=" + i);

                //하위 데이터
                List<WebElement> productDataDivs = monitorCore.getInnerProductDivs();

                //상품 하위 데이터 조회
                List<Product> productData = monitorCore.getProductData(productDataDivs);

                //정보가져오기
                monitorCore.loadData(productData);
            }
        } catch (Exception e) {
            log.error("Data Load Error");
            e.printStackTrace();
        } finally {
            monitorCore.getMonitoringLock().unlock();
        }


    }
}

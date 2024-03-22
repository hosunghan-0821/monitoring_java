package com.example.monitor.scheduler;

import com.example.monitor.chrome.ChromeDriverManager;
import com.example.monitor.monitoring.MonitorCore;
import com.example.monitor.monitoring.MonitorHashMap;
import com.example.monitor.monitoring.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static com.example.monitor.monitoring.ElementFindString.ALL_CATEGORIES_URL;

@Slf4j
@RequiredArgsConstructor
@Service
public class MonitorScheduler {

    private final MonitorCore monitorCore;

    private final MonitorHashMap monitorHashMap;


    @Scheduled(initialDelay = 10000, fixedDelay = 60000)// 1분마다 실행
    public void monitoring() {
        log.info("START: == FIND NEW PRODUCT ==");

        if (monitorCore.getMonitoringLock().isLocked()) {
            log.error("Data Load 중...");
        }
        monitorCore.getMonitoringLock().lock();
        try {
            for (int i = 1; i < 3; i++) {
                //페이지 이동
                monitorCore.changeUrl(ALL_CATEGORIES_URL + "?page=" + i);

                //하위 데이터
                List<WebElement> productDataDivs = monitorCore.getInnerProductDivs();

                //상품 하위 데이터 조회
                List<Product> productData = monitorCore.getProductData(productDataDivs);

                //정보가져오기
                List<Product> newProductList = monitorCore.findNewProduct(productData);

                if (newProductList.size() > 0) {
                    //새상품 Discord에 알림 보내면 끝
                } else {
                    log.info("PAGE: " + i + "새 상품 없음");
                }
            }
        }catch (NoSuchWindowException e){
            e.printStackTrace();
            log.error("Chrome Driver Down!!");
            monitorCore.getMonitoringLock().unlock();

        }
        catch (Exception e) {
            log.error("자동 로그아웃");
            monitorCore.getMonitoringLock().unlock();
            // 모니터링 다시 시작
            monitorCore.login();
            monitoring();
        }

        log.info("END:  == FIND NEW PRODUCT ==");
        monitorCore.getMonitoringLock().unlock();
    }
}

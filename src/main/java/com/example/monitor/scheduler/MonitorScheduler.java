package com.example.monitor.scheduler;

import com.example.monitor.chrome.ChromeDriverManager;
import com.example.monitor.discord.DiscordBot;
import com.example.monitor.monitoring.MonitorCore;
import com.example.monitor.monitoring.MonitorHashMap;
import com.example.monitor.monitoring.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.monitor.monitoring.ElementFindString.ALL_CATEGORIES_URL;

@Slf4j
@RequiredArgsConstructor
@Service
public class MonitorScheduler {

    private final MonitorCore monitorCore;

    private final MonitorHashMap monitorHashMap;

    private final DiscordBot discordBot;


    @Scheduled(initialDelay = 10000, fixedDelay = 60000)// 1분마다 실행
    public void monitoring() {
        log.info("START: == FIND NEW PRODUCT ==");

        if (monitorCore.getMonitoringLock().isLocked()) {
            log.error("Data Load 중...");
        }
        monitorCore.getMonitoringLock().lock();

        List<Product> findProductList = new ArrayList<>();

        try {
            for (int i = 1; i < 3; i++) {
                //페이지 이동
                monitorCore.changeUrl(ALL_CATEGORIES_URL + "?page=" + i);

                //하위 데이터
                List<WebElement> productDataDivs = monitorCore.getInnerProductDivs();

                //상품 하위 데이터 조회
                List<Product> productData = monitorCore.getProductData(productDataDivs);

                //데이터 누적 HashMap 수정을 위해서
                findProductList.addAll(productData);

                //정보가져오기
                List<Product> newProductList = monitorCore.findNewProduct(productData);

                if (productData.size() != 48) {
                    log.info("한 페이지에 size 개수 변동 확인요망! 현재사이즈 = " + newProductList.size());
                }
                if (!newProductList.isEmpty()) {
                    //새상품 Discord에 알림 보내면 끝
                    for (Product product : newProductList) {
                        discordBot.sendNewProductInfo("모니터링",product);
                        log.info("New Product = " + product);
                    }
                } else {
                    log.info("PAGE-" + i + ":새 상품 없음");
                }
            }
            // 이후에 HashMap 재 정립
            monitorHashMap.getProductHashMap().clear();
            monitorCore.loadData(findProductList);

        } catch (NoSuchWindowException e) {
            e.printStackTrace();
            log.error("Chrome Driver Down!!");
            monitorCore.getMonitoringLock().unlock();

        } catch (Exception e) {
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

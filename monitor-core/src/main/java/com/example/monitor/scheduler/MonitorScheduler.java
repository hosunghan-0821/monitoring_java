package com.example.monitor.scheduler;

import chrome.ChromeDriverTool;
import chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.biffi.BiffiMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFMonitorCore;
import com.example.monitor.monitoring.gebnegozi.GebenegoziMonitorCore;
import com.example.monitor.monitoring.julian.JulianMonitorCore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI;
import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI_LOG_PREFIX;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F_LOG_PREFIX;
import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.GEBE;
import static com.example.monitor.monitoring.julian.JulianFindString.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class MonitorScheduler {

    private final JulianMonitorCore julianMonitorCore;

    private final BiffiMonitorCore biffiMonitorCore;

    private final DoubleFMonitorCore doubleFMonitorCore;

    private final GebenegoziMonitorCore gebenegoziMonitorCore;

    private final ChromeDriverToolFactory chromeDriverToolFactory;




    @Scheduled(initialDelay = 60000 * 2, fixedDelay = 60000)// 1분마다 실행
    public void monitorJulianAllCategories() {
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(ALL_CATEGORIES);
        julianMonitorCore.runFindProductLogic(chromeDriverTool);

    }

    @Deprecated
//    @Scheduled(initialDelay = 60000 * 2, fixedDelay = 60000 * 10)// 10분마다 실행
    public void monitorJulianPromo() {
//        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(PROMO);
//        julianMonitorCore.runFindProductLogic(chromeDriverTool, PROMO_URL, PROMO, PROMO_CHANNEL);
    }

    @Scheduled(initialDelay = 60000 * 7, fixedDelay = 60000 * 30)// 30분마다 실행
    public void monitorDoubleF() {
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(DOUBLE_F);
        doubleFMonitorCore.runFindProductLogic(chromeDriverTool);
    }

    @Scheduled(initialDelay = 60000 * 5, fixedDelay = 60000 * 20)// 20분마다 실행
    public void monitorBIFFI() {
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(BIFFI);
        biffiMonitorCore.runFindProductLogic(chromeDriverTool);
    }


    @Scheduled(initialDelay = 60000, fixedDelay = 60000 * 60 * 24 * 3)// 3일마다 실행
    public void clearKeySet() {
        //Julian
        log.info(JULIAN_LOG_PREFIX + "새 등록 상품 풀 초기화 (중복방지용) : All Categories, Promo");
        julianMonitorCore.getJulianBrandHashData().getProductKeySet().clear();


        //DoubleF
        log.info(DOUBLE_F_LOG_PREFIX + "새 등록 상품 풀 초기화 (중복방지용) : DoubleF ");
        doubleFMonitorCore.getDoubleFBrandHashData().getProductKeySet().clear();

        //biffi
        log.info(BIFFI_LOG_PREFIX + "새 등록 상품 풀 초기화 (중복방지용) : biffi ");
        biffiMonitorCore.getBiffiBrandHashData().getProductKeySet().clear();

    }

    @Scheduled(initialDelay = 60000 * 4, fixedDelay = 60000 * 30)// 30분마다 실행
    public void monitorGebene() {
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(GEBE);
        gebenegoziMonitorCore.runFindProductLogic(chromeDriverTool);
    }


}

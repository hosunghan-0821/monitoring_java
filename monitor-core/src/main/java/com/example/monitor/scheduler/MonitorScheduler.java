package com.example.monitor.scheduler;

import chrome.ChromeDriverTool;
import chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.biffi.BiffiMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFMonitorCore;
import com.example.monitor.monitoring.eic.EicFindString;
import com.example.monitor.monitoring.eic.EicMonitorCore;
import com.example.monitor.monitoring.gebnegozi.GebenegoziMonitorCore;
import com.example.monitor.monitoring.julian.JulianMonitorCore;
import com.example.monitor.monitoring.style.StyleMonitorCore;
import com.example.monitor.monitoring.vietti.ViettiMonitorCore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI;
import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI_LOG_PREFIX;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F_LOG_PREFIX;
import static com.example.monitor.monitoring.eic.EicFindString.EIC;
import static com.example.monitor.monitoring.eic.EicFindString.EIC_DISCOUNT;
import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.GNB;
import static com.example.monitor.monitoring.julian.JulianFindString.*;
import static com.example.monitor.monitoring.style.StyleFindString.STYLE;
import static com.example.monitor.monitoring.style.StyleFindString.STYLE_LOG_PREFIX;
import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI;
import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI_LOG_PREFIX;

@Slf4j
@RequiredArgsConstructor
@Service
public class MonitorScheduler {

    private final JulianMonitorCore julianMonitorCore;

    private final BiffiMonitorCore biffiMonitorCore;

    private final DoubleFMonitorCore doubleFMonitorCore;

    private final GebenegoziMonitorCore gebenegoziMonitorCore;

    private final ChromeDriverToolFactory chromeDriverToolFactory;

    private final StyleMonitorCore styleMonitorCore;

    private final ViettiMonitorCore viettiMonitorCore;

    private final EicMonitorCore eicMonitorCore;


    @Scheduled(initialDelay = 60000 * 2, fixedDelay = 60000)// 1분마다 실행
    public void monitorJulianAllCategories() {
        MDC.put("threadName", JULIAN); // MDC에 쓰레드 이름 저장
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(ALL_CATEGORIES);
        julianMonitorCore.runFindProductLogic(chromeDriverTool);
        MDC.clear(); // MDC 데이터 정리
    }

    @Scheduled(initialDelay = 60000 * 7, fixedDelay = 60000 * 30)// 30분마다 실행
    public void monitorDoubleF() {
        MDC.put("threadName", DOUBLE_F); // MDC에 쓰레드 이름 저장
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(DOUBLE_F);
        doubleFMonitorCore.runFindProductLogic(chromeDriverTool);
        MDC.clear(); // MDC 데이터 정리
    }

    @Scheduled(initialDelay = 60000 * 5, fixedDelay = 60000 * 20)// 20분마다 실행
    public void monitorBIFFI() {
        MDC.put("threadName", BIFFI); // MDC에 쓰레드 이름 저장
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(BIFFI);
        biffiMonitorCore.runFindProductLogic(chromeDriverTool);
        MDC.clear(); // MDC 데이터 정리
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

        //Vietti
        log.info(VIETTI_LOG_PREFIX + "새 등록 상품 풀 초기화 (중복방지용) : Vietti");
        viettiMonitorCore.getViettiBrandHashData().getProductKeySet().clear();

        //Style
        log.info(STYLE_LOG_PREFIX + "새 등록 상품 풀 초기화 (중복방지용) : Style");
        styleMonitorCore.getStyleBrandHashData().getProductKeySet().clear();

        //eic
        log.info(EicFindString.EIC_LOG_PREFIX + "새 등록 상품 풀 초기화 (중복방지용) : eic");
        eicMonitorCore.getEicBrandHashData().getProductKeySet().clear();

    }

    @Scheduled(initialDelay = 60000 * 4, fixedDelay = 60000 * 5)// 30분마다 실행
    public void monitorGebene() {
        MDC.put("threadName", GNB); // MDC에 쓰레드 이름 저장
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(GNB);
        gebenegoziMonitorCore.runFindProductLogic(chromeDriverTool);
        MDC.clear(); // MDC 데이터 정리
    }

    @Scheduled(initialDelay = 60000 * 4, fixedDelay = 60000 * 5)// 5분마다 실행
    public void monitorStyle() {
        MDC.put("threadName", STYLE); // MDC에 쓰레드 이름 저장
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(STYLE);
        styleMonitorCore.runFindProductLogic(chromeDriverTool);
        MDC.clear(); // MDC 데이터 정리
    }

    @Scheduled(initialDelay = 60000 * 4, fixedDelay = 60000 * 5)// 5분마다 실행
    public void monitorVietti() {
        MDC.put("threadName", VIETTI); // MDC에 쓰레드 이름 저장
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(VIETTI);
        viettiMonitorCore.runFindProductLogic(chromeDriverTool);
        MDC.clear(); // MDC 데이터 정리

    }

    @Scheduled(initialDelay = 60000 * 10, fixedDelay = 60000 * 30)// 30분마다 실행
    public void monitorEic() {
        MDC.put("threadName", EIC); // MDC에 쓰레드 이름 저장
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(EIC);
        eicMonitorCore.runFindProductLogic(chromeDriverTool);
        MDC.clear(); // MDC 데이터 정리
    }

    @Scheduled(initialDelay = 60000 * 10, fixedDelay = 60000 * 30)// 30분마다 실행
    public void monitorEicDiscount() {
        MDC.put("threadName", EIC_DISCOUNT); // MDC에 쓰레드 이름 저장
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(EIC_DISCOUNT);
        eicMonitorCore.runFindProductLogicForDiscountChange(chromeDriverTool);
        MDC.clear(); // MDC 데이터 정리
    }
}

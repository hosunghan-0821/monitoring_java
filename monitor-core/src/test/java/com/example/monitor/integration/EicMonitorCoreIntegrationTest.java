package com.example.monitor.integration;


import com.example.monitor.monitoring.eic.EicBrandHashData;
import com.example.monitor.monitoring.eic.EicFindString;
import com.example.monitor.monitoring.eic.EicMonitorCore;
import com.example.monitor.monitoring.eic.EicProduct;
import module.discord.DiscordBot;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.monitor.monitoring.eic.EicFindString.EIC_LOG_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestConfiguration.class})
public class EicMonitorCoreIntegrationTest {

    @Autowired
    private EicMonitorCore eicMonitorCore;

    @Autowired
    private DiscordBot discordBot;
    @Autowired
    private EicBrandHashData eicBrandHashData;

    private static ChromeDriver driver;

    private static WebDriverWait wait;

    private static String[] brandNameList;


    @BeforeAll
    static void init() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("window-size=1920x1080");
        options.addArguments("start-maximized");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-automation");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("detach", true);

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofMillis(10000)); // 최대 5초 대기
        brandNameList = Arrays.copyOfRange(EicFindString.brandNameList, 1, 2);
    }

    @AfterAll
    static void end() {
        driver.quit();
    }


    @Test
    @Order(1)
    @DisplayName(EIC_LOG_PREFIX + "데이터 로드 테스트")
    void loadDataTest() {

        //given 페이지 로그인 및 쿠키 확인
        eicMonitorCore.login(driver, wait);

        eicMonitorCore.loadData(driver, wait, brandNameList);

        Map<String, EicProduct> brandHashMap = eicMonitorCore.getEicBrandHashData().getBrandHashMap(brandNameList[0]);
        assertThat(brandHashMap.size()).isGreaterThan(1);

    }

    @Test
    @Order(3)
    @DisplayName(EIC_LOG_PREFIX + "상품 colorCode 확인 테스트")
    void getDetailProductInfo() {



        //given
        String url = eicMonitorCore.makeBrandUrl(brandNameList[0]);
        //when
        List<EicProduct> findProductList = eicMonitorCore.getPageProductData(driver, wait, url, brandNameList[0]);
        eicMonitorCore.getDetailProductInfo(driver, wait, findProductList.get(0));
        EicProduct detailEicProduct = findProductList.get(0);
        //then
        assertThat(detailEicProduct.getColorCode()).isNotNull();

    }


    @Test
    @Order(5)
    @DisplayName(EIC_LOG_PREFIX + "다른상품 알람 테스트 새제품 존재")
    void findDifferentAlarmNewProduct() {

        //given
        Map<String, EicProduct> brandHashMap = eicBrandHashData.getBrandHashMap(brandNameList[0]);
        for (Map.Entry<String, EicProduct> entry : brandHashMap.entrySet()) {
            String key = entry.getKey();
            brandHashMap.remove(key);
            break;
        }

        //when
        List<EicProduct> newProductList = eicMonitorCore.findDifferentAndAlarm(driver, wait, brandNameList);

        //then
        EicProduct eicProduct = newProductList.get(0);
        assertThat(eicProduct.getColorCode()).isNotNull();
        assertThat(newProductList.size()).isGreaterThanOrEqualTo(1);

    }


    @Test
    @Order(6)
    @DisplayName(EIC_LOG_PREFIX + "다른상품 알람테스트 할인율 변경")
    void findDifferentAlarmDiscountChange() {

        //given
        Map<String, EicProduct> brandHashMap = eicBrandHashData.getBrandHashMap(brandNameList[0]);
        for (Map.Entry<String, EicProduct> entry : brandHashMap.entrySet()) {
            brandHashMap.get(entry.getKey()).updateDiscountPercentage("20%");
            break;
        }

        //when
        List<EicProduct> discountChangeProduct = eicMonitorCore.findDifferentAndAlarm(driver, wait, brandNameList);

        //then
        assertThat(discountChangeProduct.size()).isGreaterThanOrEqualTo(1);

        try{
            Thread.sleep(5000);
        }catch (Exception e) {

        }
    }
}

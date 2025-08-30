package com.example.monitor.integration;


import chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.vietti.ViettiBrandHashData;
import com.example.monitor.monitoring.vietti.ViettiFindString;
import com.example.monitor.monitoring.vietti.ViettiMonitorCore;
import com.example.monitor.monitoring.vietti.ViettiMonitorRetry;
import com.example.monitor.monitoring.vietti.ViettiProduct;
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

import static com.example.monitor.monitoring.vietti.ViettiFindString.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestConfiguration.class})
class ViettiMonitorCoreIntegrationTest {
    @Autowired
    private ViettiMonitorCore viettiMonitorCore;

    @Autowired
    private ViettiMonitorRetry viettiMonitorRetry;

    @Autowired
    private DiscordBot discordBot;
    
    @Autowired
    private ViettiBrandHashData viettiBrandHashData;

    @Autowired
    private ChromeDriverToolFactory chromeDriverToolFactory;

    private static ChromeDriver driver;

    private static WebDriverWait wait;

    private static String[] brandNameList;
    private static String[] brandUrlList;

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
        wait = new WebDriverWait(driver, Duration.ofMillis(120000)); // 최대 120초 대기
        brandNameList = Arrays.copyOfRange(VIETTI_BRAND_NAME_LIST, 0, 1);
        brandUrlList = Arrays.copyOfRange(VIETTI_BRAND_URL_LIST, 0, 1);
    }

    @AfterAll
    static void end() {
        driver.quit();
    }


    @Test
    @Order(1)
    @DisplayName(ViettiFindString.VIETTI_LOG_PREFIX + "데이터 로드 테스트")
    void loadDataTest() {

        //given 로그인
        viettiMonitorCore.login(driver, wait);

        //when
        viettiMonitorCore.loadData(driver, wait, brandUrlList, brandNameList);

        Map<String, ViettiProduct> brandHashMap = viettiBrandHashData.getBrandHashMap(brandNameList[0]);
        assertThat(brandHashMap.size()).isGreaterThan(1);

    }

    @Test
    @Order(2)
    @DisplayName(ViettiFindString.VIETTI_LOG_PREFIX + "데이터 조회 및 상품정보 확인 테스트")
    void getPageProductDataTest() {

        String brandName = brandNameList[0];
        String brandUrl = brandUrlList[0];
        Map<String, ViettiProduct> brandHashMap = viettiBrandHashData.getBrandHashMap(brandName);
        //when
        List<ViettiProduct> findProductList = viettiMonitorRetry.getPageProductData(driver, wait, brandUrl, brandName);

        //then
        assertThat(findProductList.size()).isGreaterThanOrEqualTo(1);

        for (ViettiProduct viettiProduct : findProductList) {
            System.out.println(viettiProduct.getId());
            assertThat(viettiProduct.getId()).isNotNull().isNotBlank();
            assertThat(viettiProduct.getImageSrc()).isNotNull().isNotBlank();
            assertThat(viettiProduct.getProductLink()).isNotNull().isNotBlank();
            assertThat(viettiProduct.getName()).isNotNull().isNotBlank();
            assertThat(viettiProduct.getPrice()).isNotNull().isNotBlank();
        }

    }

    @Test
    @Order(3)
    @DisplayName(ViettiFindString.VIETTI_LOG_PREFIX + "상품 상세정보 확인 테스트")
    void getDetailProductInfo() {

        //given
        String brandName = brandNameList[0];
        String brandUrl = brandUrlList[0];
        //when
        List<ViettiProduct> findProductList = viettiMonitorRetry.getPageProductData(driver, wait, brandUrl, brandName);
        viettiMonitorCore.getProductMoreInfo(driver, wait, findProductList.get(0));
        ViettiProduct detailViettiProduct = findProductList.get(0);
        //then
        assertThat(detailViettiProduct.getSku()).isNotNull();
        assertThat(detailViettiProduct.getMadeBy()).isNotNull();

    }

    @Test
    @Order(4)
    @DisplayName(ViettiFindString.VIETTI_LOG_PREFIX + "다른상품 알람 테스트 새제품 없음")
    void findDifferentAlarmNothing() {
        //when
        List<ViettiProduct> newProductList = viettiMonitorCore.findDifferentAndAlarm(driver, wait, brandUrlList, brandNameList);

        //then
        assertThat(newProductList.size()).isEqualTo(0);

    }

    @Test
    @Order(5)
    @DisplayName(ViettiFindString.VIETTI_LOG_PREFIX + "다른상품 알람 테스트 새제품 존재")
    void findDifferentAlarmNewProduct() {

        //given
        Map<String, ViettiProduct> brandHashMap = viettiBrandHashData.getBrandHashMap(brandNameList[0]);
        for (Map.Entry<String, ViettiProduct> entry : brandHashMap.entrySet()) {
            String key = entry.getKey();
            brandHashMap.remove(key);
            break;
        }

        //when
        List<ViettiProduct> newProductList = viettiMonitorCore.findDifferentAndAlarm(driver, wait, brandUrlList, brandNameList);

        //then
        ViettiProduct viettiProduct = newProductList.get(0);
        assertThat(viettiProduct.getSku()).isNotNull();
        assertThat(viettiProduct.getMadeBy()).isNotNull();
        assertThat(newProductList.size()).isEqualTo(1);
    }

    @Test
    @Order(6)
    @DisplayName(ViettiFindString.VIETTI_LOG_PREFIX + "다른상품 알람테스트 할인율 변경")
    void findDifferentAlarmDiscountChange() {

        //given
        Map<String, ViettiProduct> brandHashMap = viettiBrandHashData.getBrandHashMap(brandNameList[0]);
        for (Map.Entry<String, ViettiProduct> entry : brandHashMap.entrySet()) {
            brandHashMap.get(entry.getKey()).updateDiscountPercentage("99%");
            break;
        }

        //when
        List<ViettiProduct> discountChangeProduct = viettiMonitorCore.findDifferentAndAlarm(driver, wait, brandUrlList, brandNameList);

        //then
        assertThat(discountChangeProduct.size()).isEqualTo(1);
    }
}

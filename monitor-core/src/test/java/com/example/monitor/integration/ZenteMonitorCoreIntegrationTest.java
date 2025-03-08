package com.example.monitor.integration;


import chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.dobulef.DoubleFBrandHashData;
import com.example.monitor.monitoring.dobulef.DoubleFFindString;
import com.example.monitor.monitoring.dobulef.DoubleFMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import com.example.monitor.monitoring.zente.ZenteBrandHashData;
import com.example.monitor.monitoring.zente.ZenteFindString;
import com.example.monitor.monitoring.zente.ZenteMonitorCore;
import com.example.monitor.monitoring.zente.ZenteProduct;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F_MAIN_PAGE;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.MANS_PREFIX;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.NEW_PRODUCT;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.manBrandNameList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ZenteMonitorCoreIntegrationTest {


    @Autowired
    private ZenteMonitorCore zenteMonitorCore;

    @Autowired
    private DiscordBot discordBot;
    @Autowired
    private ZenteBrandHashData zenteBrandHashData;

    @Autowired
    private ChromeDriverToolFactory chromeDriverToolFactory;


    private static ChromeDriver driver;

    private static WebDriverWait wait;

    private static String[][] brandNameList;


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
        wait = new WebDriverWait(driver, Duration.ofMillis(5000)); // 최대 5초 대기
        brandNameList = ZenteFindString.ZENTE_URL_INFO;
    }

    @AfterAll
    static void end() {
        driver.quit();
    }


    @Test
    @Order(1)
    @DisplayName(ZenteFindString.ZENTE_LOG_PREFIX + "데이터 로드 테스트")
    void loadDataTest() {


        zenteMonitorCore.loadData(driver, wait, brandNameList);

        Map<String, ZenteProduct> brandHashMap = zenteBrandHashData.getBrandHashMap(brandNameList[0][0], brandNameList[0][1]);
        assertThat(brandHashMap.size()).isGreaterThan(1);


    }


    @Test
    @Order(2)
    @DisplayName(ZenteFindString.ZENTE_LOG_PREFIX + "데이터 조회 및 원산지 확인 테스트")
    void getPageProductDataTest() {


        Map<String, ZenteProduct> brandHashMap = zenteBrandHashData.getBrandHashMap(brandNameList[0][0], brandNameList[0][1]);
        //when
        List<ZenteProduct> findProductList = zenteMonitorCore.getPageProductData(driver, wait, brandNameList[0][2], brandNameList[0][0], brandNameList[0][1]);

        //then
        assertThat(findProductList.size()).isGreaterThanOrEqualTo(1);

        for (ZenteProduct zenteProduct : findProductList) {
            assertThat(zenteProduct.getProductLink()).isNotNull().isNotBlank();
            assertThat(zenteProduct.getName()).isNotNull().isNotBlank();
            assertThat(zenteProduct.getPrice()).isNotNull().isNotBlank();
            assertThat(brandHashMap.containsKey(zenteProduct.getId())).isEqualTo(true);
        }

    }

    @Test
    @Order(3)
    @DisplayName(ZenteFindString.ZENTE_LOG_PREFIX + "상품 원산지 확인 테스트")
    void getDetailProductInfo() {

        //given

        //when
        List<ZenteProduct> findProductList = zenteMonitorCore.getPageProductData(driver, wait, brandNameList[0][2], brandNameList[0][0], brandNameList[0][1]);

        zenteMonitorCore.getDetailProductInfo(driver, wait, findProductList.get(0));
        ZenteProduct zenteProduct = findProductList.get(0);
        //then
        assertThat(zenteProduct.getSku()).isNotNull();
        assertThat(zenteProduct.getSku()).isNotBlank();

    }


    @Test
    @Order(4)
    @DisplayName(ZenteFindString.ZENTE_LOG_PREFIX + "다른상품 알람 테스트 새제품 존재")
    void findDifferentAlarmNewProduct() {

        //given
        Map<String, ZenteProduct> brandHashMap = zenteBrandHashData.getBrandHashMap(brandNameList[0][0], brandNameList[0][1]);
        //when
        List<ZenteProduct> findProductList = zenteMonitorCore.getPageProductData(driver, wait, brandNameList[0][2], brandNameList[0][0], brandNameList[0][1]);

        for (Map.Entry<String, ZenteProduct> entry : brandHashMap.entrySet()) {
            String key = entry.getKey();
            brandHashMap.remove(key);
            break;
        }

        //when
        List<ZenteProduct> newProductList = zenteMonitorCore.findDifferentAndAlarm(driver, wait, brandNameList);

        //then
        ZenteProduct zenteProduct = newProductList.get(0);
        assertThat(zenteProduct.getSku()).isNotNull();
        assertThat(newProductList.size()).isEqualTo(1);
    }
}

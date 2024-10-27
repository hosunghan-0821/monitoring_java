package com.example.monitor.integration;

import com.example.monitor.monitoring.antonioli.AntonioliBrandHashData;
import com.example.monitor.monitoring.antonioli.AntonioliMonitorCore;
import com.example.monitor.monitoring.antonioli.AntonioliProduct;
import com.example.monitor.monitoring.dobulef.DoubleFFindString;
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

import static com.example.monitor.monitoring.antonioli.AntonioliFindString.ANTONIOLI_LOG_PREFIX;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.MANS_PREFIX;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.manBrandNameList;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestConfiguration.class})
public class AntonioliMonitorCoreIntegrationTest {

    @Autowired
    private AntonioliMonitorCore antonioliMonitorCore;

    @Autowired
    private DiscordBot discordBot;
    @Autowired
    private AntonioliBrandHashData antonioliBrandHashData;

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
        options.addArguments("--user-data-dir=/Users/hanhosung/private/private");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofMillis(5000)); // 최대 5초 대기
        brandNameList = Arrays.copyOfRange(manBrandNameList, 1, 2);
    }

    @AfterAll
    static void end() {
        driver.quit();
    }


    @Test
    @Order(1)
    @DisplayName(ANTONIOLI_LOG_PREFIX + "데이터 로드 테스트")
    void loadDataTest() {

        //given 페이지 로그인 및 쿠키 확인
        antonioliMonitorCore.login(driver, wait);


        antonioliMonitorCore.loadData(driver, wait, brandNameList, MANS_PREFIX);

        Map<String, AntonioliProduct> brandHashMap = antonioliBrandHashData.getBrandHashMap(MANS_PREFIX, brandNameList[0]);
        assertThat(brandHashMap.size()).isGreaterThan(1);

    }

    @Test
    @Order(3)
    @DisplayName(ANTONIOLI_LOG_PREFIX + "상품 원산지 확인 테스트")
    void getDetailProductInfo() {

        //given 페이지 로그인 및 쿠키 확인
        antonioliMonitorCore.login(driver, wait);

        //given
        String url = antonioliMonitorCore.makeBrandUrl(brandNameList[0], MANS_PREFIX);
        //when
        List<AntonioliProduct> findProductList = antonioliMonitorCore.getPageProductDataOrNull(driver, wait, url, brandNameList[0]);
        antonioliMonitorCore.getDetailProductInfo(driver, wait, findProductList.get(0));
        AntonioliProduct detailAntonioliProduct = findProductList.get(0);
        //then
        assertThat(detailAntonioliProduct.getMadeBy()).isNotNull();

    }


    @Test
    @Order(5)
    @DisplayName(ANTONIOLI_LOG_PREFIX + "다른상품 알람 테스트 새제품 존재")
    void findDifferentAlarmNewProduct() {

        //given
        Map<String, AntonioliProduct> brandHashMap = antonioliBrandHashData.getBrandHashMap(DoubleFFindString.MANS_PREFIX, brandNameList[0]);
        for (Map.Entry<String, AntonioliProduct> entry : brandHashMap.entrySet()) {
            String key = entry.getKey();
            brandHashMap.remove(key);
            break;
        }

        //when
        List<AntonioliProduct> newProductList = antonioliMonitorCore.findDifferentAndAlarm(driver, wait, brandNameList, DoubleFFindString.MANS_PREFIX);

        //then
        AntonioliProduct antonioliProduct = newProductList.get(0);
        assertThat(antonioliProduct.getMadeBy()).isNotNull();
        assertThat(newProductList.size()).isEqualTo(1);

    }

    @Test
    @Order(6)
    @DisplayName(ANTONIOLI_LOG_PREFIX + "다른상품 알람테스트 할인율 변경")
    void findDifferentAlarmDiscountChange() {

        //given
        Map<String, AntonioliProduct> brandHashMap = antonioliBrandHashData.getBrandHashMap(DoubleFFindString.MANS_PREFIX, brandNameList[0]);
        for (Map.Entry<String, AntonioliProduct> entry : brandHashMap.entrySet()) {
            brandHashMap.get(entry.getKey()).updateDiscountPercentage("20%");
            break;
        }

        //when
        List<AntonioliProduct> discountChangeProduct = antonioliMonitorCore.findDifferentAndAlarm(driver, wait, brandNameList, DoubleFFindString.MANS_PREFIX);

        //then
        assertThat(discountChangeProduct.size()).isEqualTo(1);

        try{
            Thread.sleep(5000);
        }catch (Exception e) {

        }
    }

}

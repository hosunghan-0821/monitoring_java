package com.example.monitor.integration;

import chrome.ChromeDriverToolFactory;
import com.example.monitor.infra.converter.controller.IConverterFacade;
import com.example.monitor.infra.discord.DiscordBot;
import com.example.monitor.monitoring.biffi.BiffiBrandHashData;
import com.example.monitor.monitoring.biffi.BiffiFindString;
import com.example.monitor.monitoring.biffi.BiffiMonitorCore;
import com.example.monitor.monitoring.biffi.BiffiProduct;
import org.junit.jupiter.api.*;
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

import static com.example.monitor.monitoring.biffi.BiffiFindString.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestConfiguration.class})
@Disabled
class BiffiMonitorCoreIntegrationTest {
    @Autowired
    private BiffiMonitorCore biffiMonitorCore;

    @Autowired
    private BiffiBrandHashData biffiBrandHashData;
    @Autowired
    private ChromeDriverToolFactory chromeDriverToolFactory;

    @Value("${biffi.user.id}")
    private String userId;

    @Value("${biffi.user.pw}")
    private String userPw;

    @Autowired
    private DiscordBot discordBot;

    @Autowired
    private IConverterFacade iConverterFacade;

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
        wait = new WebDriverWait(driver, Duration.ofMillis(5000)); // 최대 5초 대기

        brandNameList = Arrays.copyOfRange(BIFFI_BRAND_NAME_LIST, 0, 1);
        brandUrlList = Arrays.copyOfRange(BIFFI_BRAND_URL_LIST, 0, 1);


    }

    @AfterAll
    static void end() {
        driver.quit();
    }

    @Test
    @Order(1)
    @DisplayName(BiffiFindString.BIFFI_LOG_PREFIX + "데이터 로드 테스트")
    void loadDataTest() {

        //given
        biffiMonitorCore.login(driver, wait);

        //when
        biffiMonitorCore.loadData(driver, wait, brandNameList, brandUrlList);

        //then
        assertThat(biffiBrandHashData.getBrandHashMap(brandNameList[0]).size()).isGreaterThan(1);
    }

    @Test
    @Order(2)
    @DisplayName(BiffiFindString.BIFFI_LOG_PREFIX + "데이터 조회 및 원산지 확인 테스트")
    void getPageProductDataTest() {
        //given
        String brandName = brandNameList[0];
        String url = brandUrlList[0];

        //when
        List<BiffiProduct> biffiProductList = biffiMonitorCore.getPageProductData(driver, wait, url, brandName);

        //then
        assertThat(biffiProductList.size()).isGreaterThanOrEqualTo(1);

        BiffiProduct biffiProduct = biffiProductList.get(1);

        biffiMonitorCore.getProductOrigin(driver, wait, biffiProduct);

        assertThat(biffiProduct.getSku()).isNotNull();
        assertThat(biffiProduct.getPrice()).isNotNull();
        assertThat(biffiProduct.getProductLink()).isNotNull();

        Map<String, BiffiProduct> brandHashMap = biffiBrandHashData.getBrandHashMap(brandName);
        for (BiffiProduct biffiProductData : biffiProductList) {
            assertThat(brandHashMap.containsKey(biffiProductData.getSku())).isEqualTo(true);
        }

    }


    @Test
    @Order(4)
    @DisplayName(BiffiFindString.BIFFI_LOG_PREFIX + "다른상품 알람 테스트 새제품 없음")
    void findDifferentAlarmNothing() {
        //when
        List<BiffiProduct> differentAndAlarm = biffiMonitorCore.findDifferentAndAlarm(driver, wait, brandUrlList, brandNameList);

        //then
        assertThat(differentAndAlarm.size()).isEqualTo(0);
    }

    @Test
    @Order(5)
    @DisplayName(BiffiFindString.BIFFI_LOG_PREFIX + "다른상품 알람 테스트 새제품 존재")
    void findDifferentAlarmNewProduct() {

        //given
        Map<String, BiffiProduct> brandHashMap = biffiBrandHashData.getBrandHashMap(brandNameList[0]);
        for (Map.Entry<String, BiffiProduct> entry : brandHashMap.entrySet()) {
            String key = entry.getKey();
            brandHashMap.remove(key);
            break;
        }

        //when
        List<BiffiProduct> differentAndAlarm = biffiMonitorCore.findDifferentAndAlarm(driver, wait, brandUrlList, brandNameList);

        //then
        assertThat(differentAndAlarm.size()).isEqualTo(1);

    }


}
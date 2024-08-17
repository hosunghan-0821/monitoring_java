package com.example.monitor.integration;

import chrome.ChromeDriverToolFactory;
import com.example.monitor.infra.discord.DiscordBot;
import com.example.monitor.monitoring.gebnegozi.GebenegoziBrandHashData;
import com.example.monitor.monitoring.gebnegozi.GebenegoziMonitorCore;
import com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString;
import com.example.monitor.monitoring.gebnegozi.GebenegoziProduct;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
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

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestConfiguration.class})
class GebenegoziMonitorCoreIntegrationTest {

    @Autowired
    private GebenegoziMonitorCore gebenegoziMonitorCore;

    @Autowired
    private DiscordBot discordBot;
    @Autowired
    private GebenegoziBrandHashData gebenegoziBrandHashData;

    @Autowired
    private ChromeDriverToolFactory chromeDriverToolFactory;


    @Value("${gebenegozi.user.id}")
    private String userId;

    @Value("${gebenegozi.user.pw}")
    private String userPw;

    private static ChromeDriver driver;

    private static WebDriverWait wait;

    private static String[][] brandInfo;


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
        brandInfo = Arrays.copyOfRange(GebenegoziProdcutFindString.GEBE_URL_LIST, 0, 1);
    }

    @AfterAll
    static void end() {
        driver.quit();
    }

    @Test
    @Order(1)
    @DisplayName(GebenegoziProdcutFindString.GEBENE_LOG_PREFIX + "데이터 로드 테스트")
    void loadDataTest() {
        //given 페이지 로그인 및 쿠키 확인
        driver.get(GebenegoziProdcutFindString.GEBENE_MAIN_URL);

        gebenegoziMonitorCore.login(driver, wait);

        gebenegoziMonitorCore.loadData(driver, wait, brandInfo);

        String key = brandInfo[0][2];
        Map<String, GebenegoziProduct> brandHashMap = gebenegoziBrandHashData.getBrandHashMap(key);
        assertThat(brandHashMap.size()).isGreaterThan(1);
    }


    @Test
    @Order(2)
    @DisplayName(GebenegoziProdcutFindString.GEBENE_LOG_PREFIX + "다른상품 알람 테스트 새제품 존재")
    void findDifferentAlarmNewProduct() {

        String key = brandInfo[0][2];
        //given
        Map<String, GebenegoziProduct> brandHashMap = gebenegoziBrandHashData.getBrandHashMap(key);
        for (var entry : brandHashMap.entrySet()) {
            String productKey = entry.getKey();
            brandHashMap.remove(productKey);
            gebenegoziBrandHashData.getProductKeySet().remove(entry.getValue().getId() + entry.getValue().getSku());
            break;
        }
        //when
        List<GebenegoziProduct> newProductList = gebenegoziMonitorCore.findDifferentAndAlarm(driver, wait, brandInfo);

        //then
        GebenegoziProduct gebenegoziProduct = newProductList.get(0);
        assertThat(gebenegoziProduct.getMadeBy()).isNotNull();
        assertThat(gebenegoziProduct.getSku()).isNotNull();
        assertThat(newProductList.size()).isEqualTo(1);
    }

    @Test
    @Order(3)
    @Disabled
    @DisplayName(GebenegoziProdcutFindString.GEBENE_LOG_PREFIX + "이미지 저장 테스트")
    void imageDownloadTest(){
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

        gebenegoziMonitorCore.login(driver,wait);

        String cookie = driver.manage().getCookieNamed("JSESSIONID").getValue();
        File file = gebenegoziMonitorCore.downloadImageOrNull("http://93.46.41.5:1995/image/2000014056008_1.jpg", cookie);
        assertThat(file).isNotNull();
    }

}
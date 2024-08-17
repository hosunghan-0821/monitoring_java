package com.example.monitor.integration;

import com.example.monitor.infra.discord.DiscordBot;
import com.example.monitor.monitoring.julian.JulianFindString;
import com.example.monitor.monitoring.julian.JulianMonitorCore;
import com.example.monitor.monitoring.julian.JulianProduct;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestConfiguration.class})
class JulianMonitorCoreIntegrationTest {

    @Value("${julian.user.id}")
    private String userId;

    @Value("${julian.user.pw}")
    private String userPw;

    @Autowired
    private JulianMonitorCore julianMonitorCore;


    private static ChromeDriver driver;

    private static WebDriverWait wait;

    @Autowired
    private DiscordBot discordBot;

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
        wait = new WebDriverWait(driver, Duration.ofMillis(3000)); // 최대 10초 대기


    }

    @AfterAll
    static void end() {
        driver.quit();
    }

    @Test
    @Order(1)
    @DisplayName(JulianFindString.JULIAN_LOG_PREFIX + "데이터 로드 테스트")
    void dataLoad() {

        //given
        HashMap<String, JulianProduct> productHashMap = julianMonitorCore.getJulianBrandHashData().getBrandHashMap(JulianFindString.ALL_CATEGORIES);
        julianMonitorCore.login(driver, wait);

        //when
        List<JulianProduct> julianProductList = getJulianProducts(JulianFindString.ALL_CATEGORIES_URL);


        julianMonitorCore.loadData(productHashMap, julianProductList);

        //then
        assertThat(productHashMap.size()).isGreaterThan(1);

    }


    @Test
    @Order(2)
    @DisplayName(JulianFindString.JULIAN_LOG_PREFIX + "데이터 조회 및 원산지 확인 테스트")
    void getPageProductDataTest() {
        //given
        List<JulianProduct> julianProductList = getJulianProducts(JulianFindString.ALL_CATEGORIES_URL);
        HashMap<String, JulianProduct> productHashMap = julianMonitorCore.getJulianBrandHashData().getBrandHashMap(JulianFindString.ALL_CATEGORIES);

        //then
        for (JulianProduct julianProduct : julianProductList) {

            assertThat(julianProduct.getSku()).isNotNull();
            assertThat(julianProduct.getImageUrl()).isNotNull();
            assertThat(julianProduct.getProductLink()).isNotNull();
            assertThat(julianProduct.getName()).isNotNull();
            assertThat(julianProduct.getPrice()).isNotNull();
            assertThat(productHashMap.containsKey(julianProduct.getSku())).isEqualTo(true);
            julianMonitorCore.getProductMoreInfo(driver, wait, julianProduct);
            System.out.println(julianProduct);
        }
//        julianMonitorCore.getProductMoreInfo(driver, wait, julianProductList.get(4));
//        System.out.println( julianProductList.get(4));
    }


    private List<JulianProduct> getJulianProducts(String category) {
        String url = julianMonitorCore.getUrl(category, 1);
        julianMonitorCore.changeUrl(driver, url);
        List<WebElement> innerProductDivs = julianMonitorCore.getInnerProductDivs(wait);
        //when
        List<JulianProduct> julianProductList = julianMonitorCore.getProductData(innerProductDivs, url);

        return julianProductList;
    }
}
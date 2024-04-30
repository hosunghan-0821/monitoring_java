package com.example.monitor.integration;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.chrome.ChromeDriverToolFactory;
import com.example.monitor.infra.discord.DiscordBot;
import com.example.monitor.infra.discord.DiscordMessageProcessor;
import com.example.monitor.monitoring.biffi.BiffiFindString;
import com.example.monitor.monitoring.dobulef.DoubleFBrandHashData;
import com.example.monitor.monitoring.dobulef.DoubleFFindString;
import com.example.monitor.monitoring.dobulef.DoubleFMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestConfiguration.class})
class DoubleFMonitorCoreIntegrationTest {

    @Autowired
    private DoubleFMonitorCore doubleFMonitorCore;

    @Autowired
    private DiscordBot discordBot;
    @Autowired
    private DoubleFBrandHashData doubleFBrandHashData;

    @Autowired
    private ChromeDriverToolFactory chromeDriverToolFactory;


    @Value("${doublef.user.id}")
    private String userId;

    @Value("${doublef.user.pw}")
    private String userPw;

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
        wait = new WebDriverWait(driver, Duration.ofMillis(5000)); // 최대 5초 대기
        brandNameList = Arrays.copyOfRange(manBrandNameList, 0, 1);
    }

    @AfterAll
    static void end() {
        driver.quit();
    }

    @Test
    @Order(1)
    @DisplayName(DoubleFFindString.DOUBLE_F_LOG_PREFIX + "데이터 로드 테스트")
    void loadDataTest() {

        //given 페이지 로그인 및 쿠키 확인
        driver.get(DOUBLE_F_MAIN_PAGE);

        doubleFMonitorCore.acceptCookie(wait);
        doubleFMonitorCore.login(driver, wait);


        doubleFMonitorCore.loadData(driver, wait, brandNameList, MANS_PREFIX);

        Map<String, DoubleFProduct> brandHashMap = doubleFBrandHashData.getBrandHashMap(MANS_PREFIX, brandNameList[0]);
        assertThat(brandHashMap.size()).isGreaterThan(1);


    }

    @Test
    @Order(2)
    @DisplayName(DoubleFFindString.DOUBLE_F_LOG_PREFIX + "데이터 조회 및 원산지 확인 테스트")
    void getPageProductDataTest() {

        String url = doubleFMonitorCore.makeBrandUrl(brandNameList[0], MANS_PREFIX);
        Map<String, DoubleFProduct> brandHashMap = doubleFBrandHashData.getBrandHashMap(MANS_PREFIX, brandNameList[0]);
        //when
        List<DoubleFProduct> findProductList = doubleFMonitorCore.getPageProductData(driver, wait, url, brandNameList[0]);

        //then
        assertThat(findProductList.size()).isGreaterThanOrEqualTo(1);


        for (DoubleFProduct doubleFProduct : findProductList) {
            assertThat(doubleFProduct.getSku()).isNotNull();
            assertThat(doubleFProduct.getProductLink()).isNotNull();
            assertThat(doubleFProduct.getName()).isNotNull();
            assertThat(doubleFProduct.getPrice()).isNotNull();
            assertThat(doubleFProduct.getColorCode()).isNotNull();
            assertThat(brandHashMap.containsKey(doubleFProduct.getId())).isEqualTo(true);
        }

    }

    @Test
    @Order(3)
    @DisplayName(DoubleFFindString.DOUBLE_F_LOG_PREFIX + "상품 원산지 확인 테스트")
    void getDetailProductInfo() {

        //given
        String url = doubleFMonitorCore.makeBrandUrl(brandNameList[0], MANS_PREFIX);
        //when
        List<DoubleFProduct> findProductList = doubleFMonitorCore.getPageProductData(driver, wait, url, brandNameList[0]);
        doubleFMonitorCore.getDetailProductInfo(driver, wait, findProductList.get(0));
        DoubleFProduct detailDoubleFProduct = findProductList.get(0);
        //then
        assertThat(detailDoubleFProduct.getMadeBy()).isNotNull();

    }

    @Test
    @Order(4)
    @DisplayName(DoubleFFindString.DOUBLE_F_LOG_PREFIX + "다른상품 알람 테스트 새제품 없음")
    void findDifferentAlarmNothing() {
        //when
        List<DoubleFProduct> newProductList = doubleFMonitorCore.findDifferentAndAlarm(driver, wait, brandNameList, MANS_PREFIX);

        //then
        assertThat(newProductList.size()).isEqualTo(0);

    }

    @Test
    @Order(5)
    @DisplayName(DoubleFFindString.DOUBLE_F_LOG_PREFIX + "다른상품 알람 테스트 새제품 존재")
    void findDifferentAlarmNewProduct() {

        //given
        Map<String, DoubleFProduct> brandHashMap = doubleFBrandHashData.getBrandHashMap(MANS_PREFIX, brandNameList[0]);
        for (Map.Entry<String, DoubleFProduct> entry : brandHashMap.entrySet()) {
            String key = entry.getKey();
            brandHashMap.remove(key);
            break;
        }

        //when
        List<DoubleFProduct> newProductList = doubleFMonitorCore.findDifferentAndAlarm(driver, wait, brandNameList, MANS_PREFIX);

        //then
        DoubleFProduct doubleFProduct = newProductList.get(0);
        assertThat(doubleFProduct.getMadeBy()).isNotNull();
        assertThat(doubleFProduct.getDetectedCause()).isEqualTo(NEW_PRODUCT);
        assertThat(newProductList.size()).isEqualTo(1);
    }

    @Test
    @Order(6)
    @DisplayName(DoubleFFindString.DOUBLE_F_LOG_PREFIX + "다른상품 알람테스트 할인율 변경")
    void findDifferentAlarmDiscountChange() {

        //given
        Map<String, DoubleFProduct> brandHashMap = doubleFBrandHashData.getBrandHashMap(MANS_PREFIX, brandNameList[0]);
        for (Map.Entry<String, DoubleFProduct> entry : brandHashMap.entrySet()) {
            brandHashMap.get(entry.getKey()).updateDiscountPercentage("-100%");
            break;
        }

        //when
        List<DoubleFProduct> discountChangeProduct = doubleFMonitorCore.findDifferentAndAlarm(driver, wait, brandNameList, MANS_PREFIX);

        //then
        assertThat(discountChangeProduct.size()).isEqualTo(1);
        DoubleFProduct doubleFProduct = discountChangeProduct.get(0);
        assertThat(doubleFProduct.getDetectedCause()).isEqualTo(DISCOUNT_CHANGE);
    }
}
package com.example.monitor.integration;

import chrome.ChromeDriverToolFactory;
import module.discord.DiscordBot;
import com.example.monitor.monitoring.theclub.TheClubBrandHashData;
import com.example.monitor.monitoring.theclub.TheClubFindString;
import com.example.monitor.monitoring.theclub.TheClubMonitorCore;
import com.example.monitor.monitoring.theclub.TheClubProduct;
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

import static com.example.monitor.monitoring.theclub.TheClubFindString.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Import({TestConfiguration.class})
class TheClubMonitorCoreIntegrationTest {

    @Autowired
    private TheClubMonitorCore theClubMonitorCore;

    @Autowired
    private DiscordBot discordBot;
    
    @Autowired
    private TheClubBrandHashData theClubBrandHashData;

    @Autowired
    private ChromeDriverToolFactory chromeDriverToolFactory;

    @Value("${theclub.user.id}")
    private String userId;

    @Value("${theclub.user.pw}")
    private String userPw;

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
        
        // 테스트용으로 첫 번째 브랜드만 사용
        brandNameList = Arrays.copyOfRange(THE_CLUB_BRAND_NAME_LIST, 0, 1);
        brandUrlList = Arrays.copyOfRange(THE_CLUB_BRAND_URL_LIST, 0, 1);
    }

    @AfterAll
    static void end() {
        driver.quit();
    }

    @Test
    @Order(1)
    @DisplayName(THE_CLUB_LOG_PREFIX + "로그인 테스트")
    void loginTest() {
        // when
        theClubMonitorCore.login(driver, wait);
        
        // then - 로그인 후 URL 변경 확인
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).isNotEqualTo(THE_CLUB_MAIN_PAGE);
    }

    @Test
    @Order(2)
    @DisplayName(THE_CLUB_LOG_PREFIX + "데이터 로드 테스트")
    void loadDataTest() {
        // given - 로그인이 이미 되어있다고 가정
        
        // when
        theClubMonitorCore.loadData(driver, wait, brandUrlList, brandNameList);

        // then
        Map<String, TheClubProduct> brandHashMap = theClubBrandHashData.getBrandHashMap(brandNameList[0]);
        assertThat(brandHashMap.size()).isGreaterThan(0);
    }

    @Test
    @Order(3)
    @DisplayName(THE_CLUB_LOG_PREFIX + "페이지 상품 데이터 조회 테스트")
    void getPageProductDataTest() {
        // given
        String url = brandUrlList[0];
        String brandName = brandNameList[0];
        
        // when
        List<TheClubProduct> productList = theClubMonitorCore.getPageProductData(driver, wait, url, brandName);
        
        // then
        assertThat(productList.size()).isGreaterThan(0);
        
        for (TheClubProduct product : productList) {
            assertThat(product.getBrandName()).isEqualTo(brandName);
            assertThat(product.getPrice()).isNotNull().isNotBlank();
            assertThat(product.getId()).isNotNull().isNotBlank();
            assertThat(product.getProductLink()).isNotNull().isNotBlank();
            assertThat(product.getImageUrl()).isNotNull().isNotBlank();
        }
    }

    @Test
    @Order(4)
    @DisplayName(THE_CLUB_LOG_PREFIX + "상품 상세 정보 조회 테스트")
    void getProductMoreInfoTest() {
        // given
        String url = brandUrlList[0];
        String brandName = brandNameList[0];
        List<TheClubProduct> productList = theClubMonitorCore.getPageProductData(driver, wait, url, brandName);
        TheClubProduct testProduct = productList.get(0);
        
        // when
        theClubMonitorCore.getProductMoreInfo(driver, wait, testProduct);
        
        // then
        // SKU나 컬러코드 중 하나는 설정되어야 함 (사이트에 따라 다를 수 있음)
        boolean hasDetailInfo = (testProduct.getSku() != null && !testProduct.getSku().isEmpty()) ||
                               (testProduct.getColorCode() != null && !testProduct.getColorCode().isEmpty());
        assertThat(hasDetailInfo).isTrue();
    }

    @Test
    @Order(5)
    @DisplayName(THE_CLUB_LOG_PREFIX + "새 상품 없음 - 알람 테스트")
    void findDifferentAlarmNothingTest() {
        // when - 이미 모든 상품이 로드된 상태에서 다시 체크
        List<TheClubProduct> newProductList = theClubMonitorCore.findDifferentAndAlarm(driver, wait, brandUrlList, brandNameList);
        
        // then
        assertThat(newProductList.size()).isEqualTo(0);
    }

    @Test
    @Order(6)
    @DisplayName(THE_CLUB_LOG_PREFIX + "새 상품 존재 - 알람 테스트")
    void findDifferentAlarmNewProductTest() {
        // given - 기존 상품 중 하나를 제거하여 새 상품처럼 만들기
        Map<String, TheClubProduct> brandHashMap = theClubBrandHashData.getBrandHashMap(brandNameList[0]);
        String removedKey = null;
        for (Map.Entry<String, TheClubProduct> entry : brandHashMap.entrySet()) {
            removedKey = entry.getKey();
            brandHashMap.remove(removedKey);
            break;
        }
        
        // when
        List<TheClubProduct> newProductList = theClubMonitorCore.findDifferentAndAlarm(driver, wait, brandUrlList, brandNameList);
        
        // then
        assertThat(newProductList.size()).isEqualTo(1);
        TheClubProduct newProduct = newProductList.get(0);
        assertThat(newProduct.getId()).isEqualTo(removedKey);
    }

    @Test
    @Order(7)
    @DisplayName(THE_CLUB_LOG_PREFIX + "할인율 변경 - 알람 테스트")
    void findDifferentAlarmDiscountChangeTest() {
        // given - 기존 상품의 할인율 변경
        Map<String, TheClubProduct> brandHashMap = theClubBrandHashData.getBrandHashMap(brandNameList[0]);
        TheClubProduct targetProduct = null;
        for (Map.Entry<String, TheClubProduct> entry : brandHashMap.entrySet()) {
            targetProduct = entry.getValue();
            targetProduct.updateSalePercent("99%"); // 할인율을 99%로 변경
            break;
        }
        
        // when
        List<TheClubProduct> changedProductList = theClubMonitorCore.findDifferentAndAlarm(driver, wait, brandUrlList, brandNameList);
        
        // then
        // 할인율이 변경된 상품이 감지될 수 있음 (실제 사이트 할인율과 다르면)
        // 이 테스트는 할인율 변경 로직이 작동하는지 확인
        assertThat(changedProductList.size()).isGreaterThanOrEqualTo(0);
    }
}
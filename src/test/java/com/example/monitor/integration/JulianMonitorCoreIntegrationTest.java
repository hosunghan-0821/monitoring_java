package com.example.monitor.integration;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.julian.JulianFindString;
import com.example.monitor.monitoring.julian.JulianMonitorCore;
import com.example.monitor.monitoring.julian.JulianProduct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JulianMonitorCoreIntegrationTest {

    @Value("${julian.user.id}")
    private String userId;

    @Value("${julian.user.pw}")
    private String userPw;

    @Autowired
    private JulianMonitorCore julianMonitorCore;

    @Autowired
    private ChromeDriverToolFactory chromeDriverToolFactory;


    private ChromeDriver driver;

    private WebDriverWait wait;

    @BeforeEach
    void init() {
        chromeDriverToolFactory.makeChromeDriverTool("test");
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool("test");
        driver = chromeDriverTool.getChromeDriver();
        wait = chromeDriverTool.getWebDriverWait();
    }

    @AfterEach
    void end() {
        driver.quit();
    }


    @Test
    @DisplayName(JulianFindString.JULIAN_LOG_PREFIX + "데이터 조회 및 원산지 확인 테스트")
    void getPageProductDataTest() {
        //given
        julianMonitorCore.login(driver, wait);

        String url = julianMonitorCore.getUrl(JulianFindString.ALL_CATEGORIES_URL, 1);
        julianMonitorCore.changeUrl(driver, url);
        List<WebElement> innerProductDivs = julianMonitorCore.getInnerProductDivs(wait);
        //when
        List<JulianProduct> julianProductList = julianMonitorCore.getProductData(innerProductDivs, url);

        //then
        JulianProduct julianProduct = julianProductList.get(0);

        assertThat(julianProductList.size()).isGreaterThanOrEqualTo(1);

        assertThat(julianProduct.getSku()).isNotNull();
        assertThat(julianProduct.getImageUrl()).isNotNull();
        assertThat(julianProduct.getProductLink()).isNotNull();
        assertThat(julianProduct.getName()).isNotNull();
        assertThat(julianProduct.getPrice()).isNotNull();

        for (JulianProduct julianProductData : julianProductList) {
            System.out.println(julianProductData.toString());
        }


    }
}
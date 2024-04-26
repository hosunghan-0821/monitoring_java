package com.example.monitor.monitoring.dobulef;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.global.MonitoringProduct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class DoubleFMonitorCoreTest {

    @Autowired
    private DoubleFMonitorCore doubleFMonitorCore;

    @Autowired
    private ChromeDriverToolFactory chromeDriverToolFactory;


    @Value("${doublef.user.id}")
    private String userId;

    @Value("${doublef.user.pw}")
    private String userPw;

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
    @DisplayName(DoubleFFindString.DOUBLE_F_LOG_PREFIX + "데이터 조회 및 원산지 확인 테스트")
    void getPageProductDataTest() {

        //given 페이지 로그인
        driver.get(DOUBLE_F_MAIN_PAGE);

        doubleFMonitorCore.acceptCookie(wait);
        doubleFMonitorCore.login(driver, wait);
        String url = doubleFMonitorCore.makeBrandUrl(manBrandNameList[0], MANS_PREFIX);

        //when
        List<DoubleFProduct> pageProductData = doubleFMonitorCore.getPageProductData(driver, wait, url, manBrandNameList[0]);

        //then
        assertThat(pageProductData.size()).isGreaterThanOrEqualTo(1);

        DoubleFProduct doubleFProduct = pageProductData.get(0);
        assertThat(doubleFProduct.getSku()).isNotNull();
        assertThat(doubleFProduct.getProductLink()).isNotNull();
        assertThat(doubleFProduct.getName()).isNotNull();
        assertThat(doubleFProduct.getPrice()).isNotNull();
        assertThat(doubleFProduct.getColorCode()).isNotNull();

        doubleFMonitorCore.getDetailProductInfo(driver, wait, doubleFProduct);
        assertThat(doubleFProduct.getMadeBy()).isNotNull();

        for (DoubleFProduct doubleFProductData : pageProductData) {
            System.out.println(doubleFProductData.toString());
        }
    }
}
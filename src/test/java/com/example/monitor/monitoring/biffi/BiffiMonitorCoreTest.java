package com.example.monitor.monitoring.biffi;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.dobulef.DoubleFFindString;
import com.example.monitor.monitoring.dobulef.DoubleFMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
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
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.manBrandNameList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
class BiffiMonitorCoreTest {
    @Autowired
    private BiffiMonitorCore biffiMonitorCore;

    @Autowired
    private ChromeDriverToolFactory chromeDriverToolFactory;

    @Value("${biffi.user.id}")
    private String userId;

    @Value("${biffi.user.pw}")
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
    @DisplayName(BiffiFindString.BIFFI_LOG_PREFIX + "데이터 조회 및 원산지 확인 테스트")
    void getPageProductDataTest() {
        //given
        biffiMonitorCore.login(driver,wait);
        String brandUrl = BiffiFindString.BIFFI_BRAND_URL_LIST[0];
        String brandName = BiffiFindString.BIFFI_BRAND_NAME_LIST[0];

        //when
        List<BiffiProduct> biffiProductList = biffiMonitorCore.getPageProductData(driver, wait, brandUrl, brandName);

        assertThat(biffiProductList.size()).isGreaterThanOrEqualTo(1);

        //then
        BiffiProduct biffiProduct = biffiProductList.get(0);

        biffiMonitorCore.getProductOrigin(driver,wait,biffiProduct);

        assertThat(biffiProduct.getMadeBy()).isNotNull();
        assertThat(biffiProduct.getSku()).isNotNull();
        assertThat(biffiProduct.getPrice()).isNotNull();
        assertThat(biffiProduct.getProductLink()).isNotNull();

        for (BiffiProduct biffiProductData : biffiProductList) {
            System.out.println(biffiProductData.toString());
        }
    }
}
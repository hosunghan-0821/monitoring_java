package com.example.monitor.monitoring.biffi;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.chrome.ChromeDriverToolFactory;
import com.example.monitor.infra.converter.dto.ConvertProduct;
import com.example.monitor.monitoring.dobulef.DoubleFFindString;
import com.example.monitor.monitoring.dobulef.DoubleFMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import com.example.monitor.monitoring.global.MonitoringProduct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI;
import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI_BRAND_NAME_LIST;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.manBrandNameList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BiffiMonitorCoreTest {
    @Autowired
    private BiffiMonitorCore biffiMonitorCore;

    @Autowired
    private BiffiBrandHashMap biffiBrandHashMap;
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
        chromeDriverToolFactory.makeChromeDriverTool(BIFFI);
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(BIFFI);
        driver = chromeDriverTool.getChromeDriver();
        wait = chromeDriverTool.getWebDriverWait();
    }

    @AfterEach
    void end() {
        driver.quit();
    }

    @Test
    @Order(1)
    @DisplayName(BiffiFindString.BIFFI_LOG_PREFIX + "데이터 로드 테스트")
    void loadDataTest() {

        //given
        String brandName = BIFFI_BRAND_NAME_LIST[0];
        String[] brandNameList = new String[1];
        brandNameList[0] = brandName;

        String brandUrl = BiffiFindString.BIFFI_BRAND_URL_LIST[0];
        String[] brandUrlList = new String[1];
        brandUrlList[0] = brandUrl;

        biffiMonitorCore.login(driver, wait);

        //when
        biffiMonitorCore.loadData(driver, wait, brandNameList, brandUrlList);

        //then
        assertThat(biffiBrandHashMap.getBrandHashMap(brandName).size()).isGreaterThan(1);
    }

    @Test
    @Order(2)
    @DisplayName(BiffiFindString.BIFFI_LOG_PREFIX + "데이터 조회 및 원산지 확인 테스트")
    void getPageProductDataTest() {
        //given
        biffiMonitorCore.login(driver, wait);
        String brandUrl = BiffiFindString.BIFFI_BRAND_URL_LIST[0];
        String brandName = BIFFI_BRAND_NAME_LIST[0];


        //when
        List<BiffiProduct> biffiProductList = biffiMonitorCore.getPageProductData(driver, wait, brandUrl, brandName);

        assertThat(biffiProductList.size()).isGreaterThanOrEqualTo(1);

        //then
        BiffiProduct biffiProduct = biffiProductList.get(0);

        biffiMonitorCore.getProductOrigin(driver, wait, biffiProduct);

        assertThat(biffiProduct.getMadeBy()).isNotNull();
        assertThat(biffiProduct.getSku()).isNotNull();
        assertThat(biffiProduct.getPrice()).isNotNull();
        assertThat(biffiProduct.getProductLink()).isNotNull();

        Map<String, BiffiProduct> brandHashMap = biffiBrandHashMap.getBrandHashMap(brandName);
        for (BiffiProduct biffiProductData : biffiProductList) {
            assertThat(brandHashMap.containsKey(biffiProductData.getSku())).isEqualTo(true);
        }

        for (MonitoringProduct monitoringProduct : biffiProductList) {
            ConvertProduct convertProduct = monitoringProduct.changeToConvertProduct(BIFFI);
            assertThat(convertProduct).isNotNull();
            assertThat(convertProduct.getBrandName()).isEqualTo(brandName);
            assertThat(convertProduct.getProductLink()).isNotNull();
            assertThat(convertProduct.getSku()).isNotNull();
            assertThat(convertProduct.getInputPrice()).isGreaterThan(0.0);

        }
    }


}
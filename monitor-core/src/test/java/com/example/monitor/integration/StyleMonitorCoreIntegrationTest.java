package com.example.monitor.integration;

import module.discord.DiscordBot;
import com.example.monitor.monitoring.style.StyleFindString;
import com.example.monitor.monitoring.style.StyleMonitorCore;
import com.example.monitor.monitoring.style.StyleProduct;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static module.discord.DiscordString.STYLE_NEW_PRODUCT_CHANNEL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestConfiguration.class})
public class StyleMonitorCoreIntegrationTest {


    @Autowired
    private StyleMonitorCore styleMonitorCore;
    
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
    @DisplayName(StyleFindString.STYLE_LOG_PREFIX + "데이터 로드 및 알람 테스트")
    void dataLoad() {

        //given
        Map<String, StyleProduct> brandHashMap = styleMonitorCore.getStyleBrandHashData().getBrandHashMap(StyleFindString.STYLE_BRAND_NAME_LIST[0]);
        styleMonitorCore.login(driver, wait);
        
        //when
        List<StyleProduct> styleProductList = styleMonitorCore.getPageProductData(driver, wait, StyleFindString.STYLE_BRAND_URL_LIST[0], StyleFindString.STYLE_BRAND_NAME_LIST[0]);

        //then
        for (StyleProduct styleProduct : styleProductList) {
            brandHashMap.put(styleProduct.getId(), styleProduct);

            assertThat(styleProduct.getImageUrl()).isNotNull();
            assertThat(styleProduct.getProductLink()).isNotNull();
            assertThat(styleProduct.getName()).isNotNull();
            assertThat(styleProduct.getPrice()).isNotNull();
        }
        styleMonitorCore.getProductMoreInfo(driver,wait,styleProductList.get(4));
        assertThat(styleProductList.get(4).getSku()).isNotNull();
        assertThat(styleProductList.get(4).getMadeBy()).isNotNull();
        assertThat(brandHashMap.size()).isGreaterThan(1);

        System.out.println(styleProductList.get(4));
        StyleProduct styleProduct = styleProductList.get(4);
        discordBot.sendNewProductInfoCommon(
                STYLE_NEW_PRODUCT_CHANNEL,
                styleProduct.makeDiscordMessageDescription(),
                styleProduct.getProductLink(),
                styleProduct.getImageUrl(),
                Stream.of(styleProduct.getSku()).toArray(String[]::new)
        );
        try{
            Thread.sleep(10000);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

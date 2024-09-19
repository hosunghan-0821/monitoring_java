package com.example.monitor.integration;


import com.example.monitor.monitoring.style.StyleFindString;
import com.example.monitor.monitoring.style.StyleMonitorCore;
import com.example.monitor.monitoring.style.StyleProduct;
import com.example.monitor.monitoring.vietti.ViettiFindString;
import com.example.monitor.monitoring.vietti.ViettiMonitorCore;
import com.example.monitor.monitoring.vietti.ViettiMonitorRetry;
import com.example.monitor.monitoring.vietti.ViettiProduct;
import module.discord.DiscordBot;
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
import static module.discord.DiscordString.VIETTI_NEW_PRODUCT_CHANNEL;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestConfiguration.class})
public class ViettiMonitorCoreIntegrationTest {
    @Autowired
    private ViettiMonitorCore viettiMonitorCore;

    @Autowired
    private ViettiMonitorRetry viettiMonitorRetry;


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
        wait = new WebDriverWait(driver, Duration.ofMillis(120000)); // 최대 60초 대기
    }

    @AfterAll
    static void end() {
        driver.quit();
    }


    @Test
    @Order(1)
    @DisplayName(ViettiFindString.VIETTI_LOG_PREFIX + "데이터 로드 및 알람 테스트")
    void dataLoad() {

        //given
        Map<String, ViettiProduct> brandHashMap = viettiMonitorCore.getViettiBrandHashData().getBrandHashMap(ViettiFindString.VIETTI_BRAND_NAME_LIST[0]);
        viettiMonitorCore.login(driver, wait);

        //when
        List<ViettiProduct> viettiProducts = viettiMonitorRetry.getPageProductData(driver, wait, ViettiFindString.VIETTI_BRAND_URL_LIST[0], ViettiFindString.VIETTI_BRAND_NAME_LIST[0]);

        //then
        for (ViettiProduct viettiProduct : viettiProducts) {
            brandHashMap.put(viettiProduct.getId(), viettiProduct);

            assertThat(viettiProduct.getImageSrc()).isNotNull();
            assertThat(viettiProduct.getProductLink()).isNotNull();
            assertThat(viettiProduct.getName()).isNotNull();
            assertThat(viettiProduct.getPrice()).isNotNull();
        }
        viettiMonitorCore.getProductMoreInfo(driver, wait, viettiProducts.get(0));
        assertThat(viettiProducts.get(4).getSku()).isNotNull();
        assertThat(viettiProducts.get(4).getMadeBy()).isNotNull();
        assertThat(brandHashMap.size()).isGreaterThan(1);

        System.out.println(viettiProducts.get(4));
        ViettiProduct viettiProduct = viettiProducts.get(4);
        discordBot.sendNewProductInfoCommon(
                VIETTI_NEW_PRODUCT_CHANNEL,
                viettiProduct.makeDiscordMessageDescription(),
                viettiProduct.getProductLink(),
                viettiProduct.getImageSrc(),
                Stream.of(viettiProduct.getSku()).toArray(String[]::new)
        );
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

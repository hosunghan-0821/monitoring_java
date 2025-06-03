package com.example.monitor.unit.snapshot;

import com.example.monitor.integration.TestConfiguration;
import com.example.monitor.monitoring.dobulef.DoubleFMonitorCore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F_MAIN_PAGE;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.manBrandNameList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({TestConfiguration.class})
public class DoubleFSnapShot {

    @Autowired
    private DoubleFMonitorCore doubleFMonitorCore;

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
        brandNameList = Arrays.copyOfRange(manBrandNameList, 2, 3);
    }

    @Test
    @DisplayName("[Snapshot Maker] DOUBLEF-LOGIN Page: DOM 스냅숏 추출 및 저장 테스트")
    void snapshot_login_page() {
        driver.get("https://www.thedoublef.com");

        // 2) SPA·XHR 로딩이 끝날 때까지 대기
        wait.until(web -> ((JavascriptExecutor) web).executeScript("return document.readyState").equals("complete"));

        driver.get("https://www.thedoublef.com/bu_en/man/");

        wait.until(web -> ((JavascriptExecutor) web).executeScript("return document.readyState").equals("complete"));

        // 3) 최종 DOM 스냅숏 추출 & 저장
        String dom = driver.getPageSource();
        try {
            Files.writeString(Path.of("src/test/resources/doublef/login-snapshot.html"), dom, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DOM 스냅숏이 실패하였습니다 : " + e.getMessage());
        }

    }

    @Test
    @DisplayName("[Snapshot Maker] DOUBLEF-상품 Page: DOM 스냅숏 추출 및 저장 테스트")
    void snapshot_product_page() {

        //given 페이지 로그인 및 쿠키 확인
        driver.get(DOUBLE_F_MAIN_PAGE);
        doubleFMonitorCore.acceptCookie(wait);
        doubleFMonitorCore.login(driver, wait);
        driver.get("https://www.thedoublef.com/bu_en/woman/designers/prada/");
        wait.until(web -> ((JavascriptExecutor) web).executeScript("return document.readyState").equals("complete"));
        // 3) 최종 DOM 스냅숏 추출 & 저장
        String dom = driver.getPageSource();
        try {
            Files.writeString(Path.of("src/test/resources/doublef/product-snapshot.html"), dom, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("DOM 스냅숏이 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DOM 스냅숏이 실패하였습니다 : " + e.getMessage());
        }

    }

    @AfterAll
    static void quit() {
        driver.quit();
    }


}

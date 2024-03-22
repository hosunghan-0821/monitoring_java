package com.example.monitor;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@EnableScheduling
@RequiredArgsConstructor
@SpringBootApplication
public class MonitoringApplication implements CommandLineRunner {

    private final ChromeDriverManager chromeDriverManager;

    public static void main(String[] args) {
        SpringApplication.run(MonitoringApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Chrome 옵션 설정
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--no-sandbox");
//        options.addArguments("window-size=1920x1080");
//        options.addArguments("start-maximized");
//        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
//        options.setExperimentalOption("useAutomationExtension", false);
//        options.addArguments("--disable-automation");
//        options.addArguments("--disable-blink-features=AutomationControlled");
//        options.setExperimentalOption("detach", true);
        //       WebDriver driver = new ChromeDriver(options);

        ChromeDriver driver = chromeDriverManager.getChromeDriver();
        // WebDriver 생성

        driver.get("https://b2bfashion.online/");
        WebElement id = driver.findElement(By.id("email"));
        id.sendKeys("dopeesince2022@gmail.com");

        WebElement password = driver.findElement(By.id("pass"));
        password.sendKeys("dopeesince2022@gmail.com");

        WebElement loginButton = driver.findElement(By.id("submit_login"));
        loginButton.click();

        Thread.sleep(1000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(5000)); // 최대 5초 대기
        WebElement allCategories = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='title title_font']//span[contains(text(),'ALL CATEGORIES')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", allCategories);
        Thread.sleep(1000);

        WebElement topDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("box-product-grid")));
        List<WebElement> childDivs = topDiv.findElements(By.xpath("./div"));
        for (WebElement child : childDivs) {
            WebElement image = child.findElement(By.xpath(".//img[@class='img-responsive']"));
            WebElement name = child.findElement(By.xpath(".//div[@class='product_name']"));
            WebElement reference = child.findElement(By.xpath(".//div[@class='produt_reference']"));

            String imageSrc = image.getAttribute("src");

            Product product = Product.builder().Id(name.getText()).Id(reference.getText()).imageSrc(imageSrc).build();

            System.out.println("image link = " + imageSrc);
            System.out.println("name = " + name.getText());
            System.out.println("reference = " + reference.getText());
        }

        driver.quit();
    }
}

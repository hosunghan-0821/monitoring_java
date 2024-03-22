package com.example.monitor.monitoring;

import com.example.monitor.chrome.ChromeDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.monitor.monitoring.ElementFindString.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitorCore {

    private final MonitorHashMap monitorHashMap;

    private final ReentrantLock monitoringLock = new ReentrantLock();
    private ChromeDriver driver;
    private WebDriverWait wait;

    public void setChromeDriver(ChromeDriver driver) {
        assert (driver != null);
        this.driver = driver;
    }

    public void setWebDriverWait(WebDriverWait wait) {
        assert (wait != null);
        this.wait = wait;
    }

    public void changeUrl(String url){
        driver.get(url);
    }
    public void login() {
        assert (driver != null);
        assert (wait != null);

        driver.get("https://b2bfashion.online/");
        WebElement id = driver.findElement(By.id(ID_FORM));
        id.sendKeys("dopeesince2022@gmail.com");

        WebElement password = driver.findElement(By.id(PASS_FORM));
        password.sendKeys("dopeesince2022@gmail.com");

        WebElement loginButton = driver.findElement(By.id(SUBMIT_FORM));
        loginButton.click();
    }

    public void findAllCategories() {
        assert (driver != null);
        assert (wait != null);

        WebElement allCategories = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(FIND_ALL_CATEGORIES)));
        ((JavascriptExecutor) driver).executeScript(CLICK_BUTTON_WITH_SCRIPTS, allCategories);
    }

    public List<WebElement> getInnerProductDivs() {
        WebElement topDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(PRODUCT_TOP_DIV)));
        return topDiv.findElements(By.xpath(CHILD_DIV));
    }

    public List<Product> getProductData(List<WebElement> childDivs) {

        List<Product> productList = new ArrayList<>();

        for (WebElement child : childDivs) {
            WebElement image = child.findElement(By.xpath(PRODUCT_IMAGE));
            WebElement name = child.findElement(By.xpath(PRODUCT_NAME));
            WebElement reference = child.findElement(By.xpath(PRODUCT_ID));

            String imageSrc = image.getAttribute("src");

            Product product = Product.builder().Id(name.getText()).Id(reference.getText()).imageSrc(imageSrc).build();
            productList.add(product);

            log.info("image link = " + imageSrc);
            log.info("name = " + name.getText());
            log.info("reference = " + reference.getText());

        }

        return productList;

    }

    public void loadData(List<Product> productData) {
        HashMap<String, Product> productHashMap = monitorHashMap.getProductHashMap();
        for (Product product : productData) {
            if (!productHashMap.containsKey(product.getId())) {
                 productHashMap.put(product.getId(),product);
            } else {
                log.error("Load 시 겹치는 ID 존재 확인 필요 상품정보 " + product.toString());
            }
        }
    }

    public List<Product> findNewProduct(List<Product> productData){
        HashMap<String, Product> productHashMap = monitorHashMap.getProductHashMap();
        List<Product> newProductList = new ArrayList<>();

        for (Product product : productData) {
            if (!productHashMap.containsKey(product.getId())) {
                System.out.println("새로운 상품 등장" + product);
                newProductList.add(product);
            }
        }

        return newProductList;
    }

    public ReentrantLock getMonitoringLock() {
        return monitoringLock;
    }
}

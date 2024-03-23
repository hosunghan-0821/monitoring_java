package com.example.monitor.monitoring;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.discord.DiscordBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.monitor.monitoring.ElementFindString.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitorCore {


    private final DiscordBot discordBot;
    @Value("${user.id}")
    private String userId;

    @Value("${user.pw}")
    private String userPw;


    public void runFindProductLogic(ChromeDriverTool chromeDriverTool, String pageUrl, String category) {
        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();
        HashMap<String, Product> dataHashMap = chromeDriverTool.getDataHashMap();

        if (!chromeDriverTool.isLoadData()) {
            log.error("Data Load 중...");
            return;
        }
        log.info("== " + category + " DATA IS LOADED ==");
        log.info("== " + category + " FIND NEW PRODUCT ==");
        List<Product> findProductList = new ArrayList<>();

        try {
            for (int i = 1; i < 3; i++) {
                //페이지 이동
                changeUrl(chromeDriver, pageUrl + "?page=" + i);

                //하위 데이터
                List<WebElement> productDataDivs = getInnerProductDivs(wait);

                //상품 하위 데이터 조회
                List<Product> productData = getProductData(productDataDivs);

                //데이터 누적 HashMap 수정을 위해서
                findProductList.addAll(productData);

                //정보가져오기
                List<Product> newProductList = findNewProduct(dataHashMap, productData);

                if (productData.size() != 48) {
                    log.info("한 페이지에 size 개수 변동 확인요망! 현재사이즈 = " + newProductList.size());
                }
                if (!newProductList.isEmpty()) {
                    //새상품 Discord에 알림 보내면 끝
                    for (Product product : newProductList) {
                        product.setCategory(category);
                        discordBot.sendNewProductInfo("모니터링", product);
                        log.info("New Product = " + product);
                    }
                } else {
                    log.info("PAGE-" + i + ":새 상품 없음");
                }
            }
            // 이후에 HashMap 재 정립
            dataHashMap.clear();
            loadData(dataHashMap, findProductList);

        } catch (NoSuchWindowException e) {
            log.error("Chrome Driver Down!!");
            return;
        } catch (Exception e) {
            log.error("자동 로그아웃");
            // 모니터링 다시 시작
            login(chromeDriver);
        }
        log.info("END:  == FIND NEW PRODUCT ==");
    }

    public void changeUrl(ChromeDriver driver, String url) {
        driver.get(url);
    }

    public void login(ChromeDriver driver) {
        assert (driver != null);


        driver.get("https://b2bfashion.online/");
        WebElement id = driver.findElement(By.id(ID_FORM));
        id.sendKeys(userId);

        WebElement password = driver.findElement(By.id(PASS_FORM));
        password.sendKeys(userPw);

        WebElement loginButton = driver.findElement(By.id(SUBMIT_FORM));
        loginButton.click();
    }


    public List<WebElement> getInnerProductDivs(WebDriverWait wait) {
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
            List<WebElement> priceElementList = child.findElements(By.xpath(".//p[@class='price']"));
            String priceString = "가격정보 없음";

            if (!priceElementList.isEmpty()) {
                priceString = priceElementList.get(0).getText();
            } else {
                List<WebElement> originlPriceList = child.findElements(By.xpath(".//span[@class='price']"));
                if (!originlPriceList.isEmpty()) {
                    priceString = originlPriceList.get(0).getText();
                }
            }

            Product product = Product.builder()
                    .name(name.getText())
                    .Id(reference.getText())
                    .imageSrc(imageSrc)
                    .price(priceString)
                    .build();
            productList.add(product);

//            log.info("image link = " + imageSrc);
//            log.info("name = " + name.getText());
//            log.info("reference = " + reference.getText());

        }

        return productList;

    }

    public void loadData(HashMap<String, Product> productHashMap, List<Product> productData) {

        for (Product product : productData) {
            if (!productHashMap.containsKey(product.getId())) {
                productHashMap.put(product.getId(), product);
            } else {
                log.error("Load 시 겹치는 ID 존재 확인 필요 상품정보 " + product.toString());
            }
        }

        log.info("현재 적재된 상품개수: " + productHashMap.size());
    }

    public List<Product> findNewProduct(HashMap<String, Product> productHashMap, List<Product> productData) {
        List<Product> newProductList = new ArrayList<>();

        for (Product product : productData) {
            if (!productHashMap.containsKey(product.getId())) {
                System.out.println("새로운 상품 등장" + product);
                newProductList.add(product);
            }
        }

        return newProductList;
    }

}

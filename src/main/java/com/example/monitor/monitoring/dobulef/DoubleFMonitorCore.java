package com.example.monitor.monitoring.dobulef;


import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.discord.DiscordBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static com.example.monitor.discord.DiscordString.DOUBLE_F_DISCOUNT_CHANNEL;
import static com.example.monitor.discord.DiscordString.DOUBLE_F_NEW_PRODUCT_CHANNEL;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;
import static com.example.monitor.monitoring.julian.JulianFindString.CHILD_DIV;

@Slf4j
@Component
@RequiredArgsConstructor
public class DoubleFMonitorCore {

    private final DiscordBot discordBot;

    private final DoubleFBrandHashMap doubleFBrandHashMap;

    @Value("${doublef.user.id}")
    private String userId;

    @Value("${doublef.user.pw}")
    private String userPw;


    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        driver.get(DOUBLE_F_MAIN_PAGE);


        //쿠키허용
        acceptCookie(wait);

        //로그인
        login(driver, wait);

        //데이터 로드
        loadData(driver, wait, womanBrandNameList, "woman");
        loadData(driver, wait, manBrandNameList, "man");

        chromeDriverTool.isLoadData(true);

    }

    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error("Data Load or isRunning OFF");
            return;
        }
        log.info("DOUBLE_F FIND NEW PRODUCT START==");
        findDifferentAndAlarm(chromeDriver, wait, womanBrandNameList, "woman");
        findDifferentAndAlarm(chromeDriver, wait, manBrandNameList, "man");
        log.info("DOUBLE_F FIND NEW PRODUCT FINISH ==");
    }


    public void acceptCookie(WebDriverWait wait) {
        //쿠키 허용
        WebElement cookieElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(DF_COOKIE_ID)));
        cookieElement.click();
    }

    public void login(ChromeDriver driver, WebDriverWait wait) {

        driver.get(DOUBLE_F_MAIN_PAGE);
        //로그인

        WebElement loginElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(DF_ID_ID)));

        loginElement.sendKeys(userId);

        WebElement pwElement = driver.findElement(By.id(DF_PASS_ID));
        pwElement.sendKeys(userPw);

        WebElement button = driver.findElement(By.xpath(DF_LOGIN_BUTTON_XPATH));
        button.click();

        //로그인 이후 5초 정지..
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void findDifferentAndAlarm(ChromeDriver driver, WebDriverWait wait, String[] brandNameList, String type) {
        for (int i = 0; i < brandNameList.length; i++) {

            //페이지 URL 만들기
            String brandName = brandNameList[i];
            String url = makeBrandUrl(brandName, type);

            List<DoubleFProduct> pageProductData = getPageProductData(driver, wait, url, brandName);

            //상품 정보 존재할 경우
            Map<String, DoubleFProduct> eachBrandHashMap = doubleFBrandHashMap.getBrandHashMap(brandName);

            for (DoubleFProduct product : pageProductData) {
                if (!eachBrandHashMap.containsKey(product.getNameId())) {
                    //새로운 재품일 경우
                    log.info("새로운 제품" + product);
                    discordBot.sendNewProductInfo(DOUBLE_F_NEW_PRODUCT_CHANNEL, product, url);
                } else {
                    //포함 되어있고,할인 퍼센테이지가 다를 경우
                    DoubleFProduct beforeProduct = eachBrandHashMap.get(product.getNameId());
                    if (!beforeProduct.getDiscountPercentage().equals(product.getDiscountPercentage())) {
                        log.info("할인율 변경" + beforeProduct.getDiscountPercentage() + " -> " + product.getDiscountPercentage());
                        discordBot.sendDiscountChangeInfo(DOUBLE_F_DISCOUNT_CHANNEL, product, url, beforeProduct.getDiscountPercentage());
                    }
                }
            }

            //검사 후 현재 상태로 기존 데이터 변경
            if (pageProductData.size() > 0) {
                eachBrandHashMap.clear();
                for (DoubleFProduct product : pageProductData) {
                    eachBrandHashMap.put(product.getNameId(), product);
                }
            }

        }

    }


    public void loadData(ChromeDriver driver, WebDriverWait wait, String[] brandNameList, String type) {

        for (int i = 0; i < brandNameList.length; i++) {

            //페이지 URL 만들기
            String brandName = brandNameList[i];
            String url = makeBrandUrl(brandName, type);

            List<DoubleFProduct> pageProductData = getPageProductData(driver, wait, url, brandName);

            //상품 정보 존재할 경우
            Map<String, DoubleFProduct> eachBrandHashMap = doubleFBrandHashMap.getBrandHashMap(brandName);
            for (DoubleFProduct product : pageProductData) {
                eachBrandHashMap.put(product.getNameId(), product);
            }

        }
    }

    private List<DoubleFProduct> getPageProductData(ChromeDriver driver, WebDriverWait wait, String url, String brandName) {

        //페이지로 이동
        driver.get(url);

        //상품 상위 태그
        List<WebElement> productList = new ArrayList<>();
        List<DoubleFProduct> pageProductList = new ArrayList<>();
        try {
            WebElement topDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(TOP_DIV_XPATH)));
            productList = topDiv.findElements(By.xpath(CHILD_DIV));
        } catch (Exception e) {
            log.error("logout Redirection  or error");
            login(driver, wait);
            return pageProductList;
        }

        //상품 정보 로드
        for (WebElement product : productList) {
            String productName = "상품이름 정보 없습니다.";
            String productDiscountPercentage = "0%";
            String productPrice = "";

            // 상품이름 정보
            try {
                WebElement productNameElement = product.findElement(By.xpath(PRODUCT_NAME_XPATH));
                productName = productNameElement.getText();
            } catch (Exception e) {
                log.error("** 확인요망 **" + brandName + "의 상품에 이름이 없습니다. 홈페이지 및 프로그램 확인 바랍니다.");
                //discordError 알림 만들자
            }
            // 상품할인율 정보
            try {
                WebElement discountPercentage = product.findElement(By.xpath(PRODUCT_DISCOUNT_XPATH));
                productDiscountPercentage = discountPercentage.getText();
            } catch (Exception e) {
            }

            // 상품 가격 정보
            try {
                List<WebElement> productPriceElementList = product.findElements(By.xpath(PRODUCT_PRICE_XPATH));
                for (WebElement productPriceElement : productPriceElementList) {
                    productPrice = productPrice + " " + productPriceElement.getText();
                }
                productPrice = productPrice.strip();

            } catch (Exception e) {
                log.info(productName + " 의 가격 정보가 없습니다.");
            }
            DoubleFProduct doubleFProduct = DoubleFProduct.builder()
                    .nameId(productName)
                    .brand(brandName)
                    .price(productPrice)
                    .discountPercentage(productDiscountPercentage)
                    .build();
            pageProductList.add(doubleFProduct);
        }
        return pageProductList;
    }


    private String makeBrandUrl(String brandName, String type) {
        return "https://www.thedoublef.com/bu_en/" + type + "/designers/" + brandName + "/";
    }
}

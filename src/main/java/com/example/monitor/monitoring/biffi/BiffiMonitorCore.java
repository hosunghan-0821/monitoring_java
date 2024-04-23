package com.example.monitor.monitoring.biffi;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.infra.discord.DiscordBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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


import static com.example.monitor.infra.discord.DiscordString.BIFFI_DISCOUNT_CHANNEL;
import static com.example.monitor.infra.discord.DiscordString.BIFFI_NEW_PRODUCT_CHANNEL;
import static com.example.monitor.monitoring.biffi.BiffiFindString.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class BiffiMonitorCore {

    private final DiscordBot discordBot;

    private final BiffiBrandHashMap biffiBrandHashMap;

    @Value("${biffi.user.id}")
    private String userId;

    @Value("${biffi.user.pw}")
    private String userPw;


    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        //로그인
        login(driver, wait);

        //데이터 로드
        loadData(driver, wait, BIFFI_BRAND_NAME_LIST, BIFFI_BRAND_URL_LIST);

        chromeDriverTool.isLoadData(true);
    }

    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {
        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(BIFFI_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }
        log.info(BIFFI_LOG_PREFIX + "BIFFI FIND NEW PRODUCT START==");
        findDifferentAndAlarm(chromeDriver, wait, BIFFI_BRAND_URL_LIST, BIFFI_BRAND_NAME_LIST);

        log.info(BIFFI_LOG_PREFIX + "BIFFI FIND NEW PRODUCT FINISH==");
    }

    private void findDifferentAndAlarm(ChromeDriver chromeDriver, WebDriverWait wait, String[] biffiBrandUrlList, String[] biffiBrandNameList) {

        assert (biffiBrandUrlList.length == biffiBrandNameList.length);

        for (int i = 0; i < biffiBrandUrlList.length; i++) {

            Map<String, BiffiProduct> eachBrandHashMap = biffiBrandHashMap.getBrandHashMap(biffiBrandNameList[i]);
            List<BiffiProduct> biffiProductList = new ArrayList<>();

            try {
                biffiProductList = getPageProductData(chromeDriver, wait, biffiBrandUrlList[i], biffiBrandNameList[i]);
            } catch (Exception e) {
                log.error(BIFFI_LOG_PREFIX + "logout error maybe");
                login(chromeDriver, wait);
                biffiProductList = getPageProductData(chromeDriver, wait, biffiBrandUrlList[i], biffiBrandNameList[i]);
            }

            for (BiffiProduct biffiProduct : biffiProductList) {
                if (!eachBrandHashMap.containsKey(biffiProduct.getId())) {
                    //새로운 제품일 경우
                    log.info(BIFFI_LOG_PREFIX + "새로운 제품" + biffiProduct);
                    //discord bot 알람
                    //원산지 데이터 긁어와야함.
                    getProductOrigin(chromeDriver,wait,biffiProduct);
                    discordBot.sendNewProductInfo(BIFFI_NEW_PRODUCT_CHANNEL, biffiProduct, biffiBrandUrlList[i]);
                } else {
                    BiffiProduct beforeProduct = eachBrandHashMap.get(biffiProduct.getId());
                    if (!beforeProduct.getDiscountPercentage().equals(biffiProduct.getDiscountPercentage())) {
                        log.info(BIFFI_LOG_PREFIX + "할인율 변경" + beforeProduct.getDiscountPercentage() + " -> " + biffiProduct.getDiscountPercentage());
                        //discord bot 알람
                        getProductOrigin(chromeDriver,wait,biffiProduct);
                        discordBot.sendDiscountChangeInfo(BIFFI_DISCOUNT_CHANNEL, biffiProduct, biffiBrandUrlList[i],beforeProduct.getDiscountPercentage());
                    }
                }
            }

            if (biffiProductList.size() > 0) {
                eachBrandHashMap.clear();
                for (BiffiProduct biffiProduct : biffiProductList) {
                    eachBrandHashMap.put(biffiProduct.getId(), biffiProduct);
                }
            } else {
                log.error(BIFFI_LOG_PREFIX + "biffiProduct 데이터 조회 실패 **확인요망**");
            }
        }
    }

    private void getProductOrigin(ChromeDriver driver, WebDriverWait wait, BiffiProduct biffiProduct) {
        driver.get(biffiProduct.getProductUrl());
        String pageSource = driver.getPageSource();

        Document doc = Jsoup.parse(pageSource);

        // CSS 선택자를 사용하여 요소 선택

        Element select = doc.select("div.aks-accordion-row").first();
        Elements elements = select.selectXpath("//div[@class='aks-accordion-item-content']//p");

        if(elements.size() >= 3) {
            biffiProduct.updateMadeBy(elements.get(2).text());
        } else {
            log.error("**page 확인 요망**" + biffiProduct.getProductUrl());
        }


    }

    private void login(ChromeDriver driver, WebDriverWait wait) {
        driver.get(BIFFI_MAIN_URL);

        WebElement loginElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(BIFFI_LOGIN_FORM_ID)));
        loginElement.sendKeys(userId);
        WebElement passwordElement = driver.findElement(By.id(BIFFI_PASSWORD_FORD_ID));
        passwordElement.sendKeys(userPw);

        WebElement loginButton = driver.findElement(By.xpath("//input[@type='submit']"));
        loginButton.click();

        //로그인 이후 5초 정지..
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData(ChromeDriver driver, WebDriverWait wait, String[] brandNameList, String[] brandUrlList) {


        assert (brandNameList.length == brandUrlList.length);

        for (int i = 0; i < brandUrlList.length; i++) {
            List<BiffiProduct> biffiProductList = getPageProductData(driver, wait, brandUrlList[i], brandNameList[i]);
            for (BiffiProduct biffiProduct : biffiProductList) {
                biffiBrandHashMap.getBrandHashMap(brandNameList[i]).put(biffiProduct.getId(), biffiProduct);
            }
        }
    }

    private List<BiffiProduct> getPageProductData(ChromeDriver driver, WebDriverWait wait, String url, String brandName) {

        List<BiffiProduct> biffiProductList = new ArrayList<>();
        driver.get(url);

        List<WebElement> nextLinkList = driver.findElements(By.xpath("//div[@class='col5 last right']//ul[@class='bloccopagine']//a"));
        //find max
        int maxPageNum = 1;
        String tempLink = nextLinkList.get(0).getAttribute("href");
        String nextUrlLink = tempLink.substring(0, tempLink.length() - 1);

        //max page 찾기
        for (WebElement nextLink : nextLinkList) {
            String href = nextLink.getAttribute("href");
            int pageNum = href.charAt(href.length() - 1) - '0';
            if (pageNum > maxPageNum) {
                maxPageNum = pageNum;
            }
        }

        //상품 긁어와서 등록하기
        for (int j = 1; j <= maxPageNum; j++) {
            driver.get(nextUrlLink + j);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("catalogogen")));
            WebElement topDiv = driver.findElement(By.id("catalogogen"));
            List<WebElement> productElements = topDiv.findElements(By.xpath("./div"));

            for (WebElement productElement : productElements) {
                try {
                    String id = productElement.getAttribute("id");

                    WebElement salesPercent = productElement.findElement(By.xpath(".//div[@class='prezzo']//span[@class='percsaldi']"));
                    String discountPercentage = salesPercent.getText().strip();


                    WebElement price = productElement.findElement(By.xpath(".//div[@class='prezzo']//span[@class='saldi2']"));
                    String finalPrice = price.getText();

                    WebElement detailLink = productElement.findElement(By.xpath(".//p[@class='pspec']//a"));
                    String productDetailLink = detailLink.getAttribute("href");

                    WebElement SKU = productElement.findElement(By.xpath(".//div[@class='testofoto']//a//p"));
                    String sku = SKU.getText();

                    WebElement image = productElement.findElement(By.xpath(".//div[@class='cotienifoto']//a//img"));
                    String imageUrl = image.getAttribute("src");

                    BiffiProduct biffiProduct = BiffiProduct.builder()
                            .id(id)
                            .price(finalPrice)
                            .imgUrl(imageUrl)
                            .productUrl(productDetailLink)
                            .sku(sku)
                            .brand(brandName)
                            .discountPercentage(discountPercentage)
                            .build();

                    biffiProductList.add(biffiProduct);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("상품 데이터 조회 실패 URL" + nextUrlLink + j);
                }
            }
        }
        return biffiProductList;
    }

}

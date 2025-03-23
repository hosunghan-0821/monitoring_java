package com.example.monitor.monitoring.zente;

import chrome.ChromeDriverTool;
import com.example.monitor.Util.RandomUtil;
import com.example.monitor.monitoring.global.IMonitorService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.discord.DiscordBot;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.example.monitor.monitoring.zente.ZenteFindString.ZENTE_LOG_PREFIX;
import static module.discord.DiscordString.ZENTE_DISCOUNT_CHANNEL;
import static module.discord.DiscordString.ZENTE_NEW_PRODUCT_CHANNEL;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZenteMonitorCore implements IMonitorService {

    private final DiscordBot discordBot;

    @Getter
    private final ZenteBrandHashData zenteBrandHashData;

    @Override
    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        loadData(driver, wait, ZenteFindString.ZENTE_URL_INFO);
        chromeDriverTool.isLoadData(true);
    }

    @Override
    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        List<ZenteProduct> zenteProductList = new ArrayList<>();
        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(ZENTE_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }
        log.info(ZENTE_LOG_PREFIX + "ZENTE FIND NEW PRODUCT START==");
        findDifferentAndAlarm(driver, wait, ZenteFindString.ZENTE_URL_INFO);

        log.info(ZENTE_LOG_PREFIX + "ZENTE FIND NEW PRODUCT FINISH ==");

    }

    public List<ZenteProduct> findDifferentAndAlarm(ChromeDriver driver, WebDriverWait wait, String[][] zenteUrlInfo) {

        List<ZenteProduct> zenteProducts = new ArrayList<>();
        for (int i = 0; i < zenteUrlInfo.length; i++) {

            String brandName = zenteUrlInfo[i][0];
            String category = zenteUrlInfo[i][1];
            String url = zenteUrlInfo[i][2];
            List<ZenteProduct> pageProductData = new ArrayList<>();
            try {
                pageProductData = getPageProductData(driver, wait, url, brandName, category);

            } catch (Exception e) {
                continue;
            }

            if (pageProductData.size() == 0) {
                continue;
            }
            //상품 정보 존재할 경우
            Map<String, ZenteProduct> eachBrandHashMap = zenteBrandHashData.getBrandHashMap(brandName, category);
            HashSet<String> productKeySet = zenteBrandHashData.getProductKeySet();

            for (ZenteProduct product : pageProductData) {
                if (!eachBrandHashMap.containsKey(product.getId())) {
                    if (!productKeySet.contains(product.getId())) {
                        log.info(ZENTE_LOG_PREFIX + "새로운 제품" + product);
                        getDetailProductInfo(driver, wait, product);

                        discordBot.sendNewProductInfoCommon(
                                ZENTE_NEW_PRODUCT_CHANNEL,
                                product.makeDiscordMessageDescription(),
                                product.getProductLink(),
                                null,
                                Stream.of(product.getSku()).toArray(String[]::new)
                        );

                        productKeySet.add(product.getId());
                        zenteProducts.add(product);
                    } else {
                        log.error(ZENTE_LOG_PREFIX + "상품 중복 " + product);
                    }
                } else {
                    //포함 되어있고,할인 퍼센테이지가 다를 경우
                    ZenteProduct beforeProduct = eachBrandHashMap.get(product.getId());

                    if (!beforeProduct.getPrice().equals(product.getPrice())) {

                        if ((Math.abs(beforeProduct.getDoublePrice() - product.getDoublePrice()) / product.getDoublePrice() * 100) >= 15) {
                            log.info(ZENTE_LOG_PREFIX + "가격 변경" + beforeProduct.getPrice() + " -> " + product.getPrice());
                            getDetailProductInfo(driver, wait, product);
                            //discordBot.sendDiscountChangeInfo(DOUBLE_F_DISCOUNT_CHANNEL, product, url, beforeProduct.getDiscountPercentage());
                            discordBot.sendDiscountChangeInfoCommon(
                                    ZENTE_DISCOUNT_CHANNEL,
                                    product.makeDiscordDiscountMessageDescription(beforeProduct.getPrice()),
                                    product.getProductLink(),
                                    null,
                                    Stream.of(product.getSku()).toArray(String[]::new)
                            );
                            zenteProducts.add(product);
                        } else {
                            log.info("==========================\n가격 변동은 있으나 15% 이하임\nbefore product: " + beforeProduct + "\nnow product: " + product + "\n=============================");
                        }

                    }
                }
            }

            //검사 후 현재 상태로 기존 데이터 변경
            if (pageProductData.size() > 0) {
                eachBrandHashMap.clear();
                for (ZenteProduct product : pageProductData) {
                    eachBrandHashMap.put(product.getId(), product);
                }
            }
        }


        return zenteProducts;
    }

    public void getDetailProductInfo(ChromeDriver driver, WebDriverWait wait, ZenteProduct product) {

        driver.get(product.getProductLink());
        int randomSec = RandomUtil.getRandomSecDefault();
        try {
            Thread.sleep(randomSec * 1000L);
            WebElement detailWebElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='jt-info-detail-goods-item-content']")));
            List<WebElement> elements = detailWebElement.findElements(By.xpath(".//div"));
            if (elements.size() >= 2) {
                String sku = elements.get(1).getText();
                product.setSku(sku);
            }

        } catch (Exception e) {
            log.error(ZENTE_LOG_PREFIX + "DETAIL GET ERROR");
        }
    }

    @Override
    public void login(ChromeDriver driver, WebDriverWait wait) {

        //need not login
    }


    public List<ZenteProduct> getPageProductData(ChromeDriver driver, WebDriverWait wait, String url, String brandName, String category) {


        List<ZenteProduct> zenteProducts = new ArrayList<>();
        int randomSec = RandomUtil.getRandomSecDefault();

        try {
            driver.get(url);
            Thread.sleep(randomSec * 1000L);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='searchedItemDisplay']//ul//li")));


            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(0, document.body.scrollHeight / 2)", "");

            Thread.sleep(randomSec * 1000L);

            List<WebElement> elements = driver.findElements(By.xpath("//div[@id='searchedItemDisplay']//ul//li[@class='jt-goods-list-elem']"));

            for (WebElement element : elements) {

                String siteProductId = element.getAttribute("data-item-seq");

                if (siteProductId == null) {
                    log.error(ZENTE_LOG_PREFIX + "식별자가 없습니다. 문제!!");
                    continue;
                }
                WebElement infoRoot = element.findElement(By.xpath(".//ul[@class='jt-goods-item-info']"));

                WebElement brandElement = infoRoot.findElement(By.xpath(".//li[@class='goods-brand']"));
                WebElement productElement = infoRoot.findElement(By.xpath(".//li[@class='goods-name ellipsis']"));

                WebElement consumerPriceElement = infoRoot.findElement(By.xpath(".//li[@class='price consumer-price']"));

                WebElement priceElement = infoRoot.findElement(By.xpath(".//li[@class='price']"));

                String priceText = priceElement.getText();
                double price = 0;
                try {
                    String tempText = priceText.split(" ")[1].replaceAll(",", "");
                    price = Double.parseDouble(tempText);
                } catch (Exception e) {
                    log.error("stirng to double parse error url : " + url);
                }

                ZenteProduct zenteProduct = ZenteProduct.builder()
                        .brandName(brandElement.getText())
                        .category(category)
                        .price(priceElement.getText())
                        .name(productElement.getText())
                        .salesPrevPrice(priceElement.getText())
                        .id(siteProductId)
                        .productLink("https://jentestore.com/goods/view?no=" + siteProductId)
                        .doublePrice(price)
                        .build();

                zenteProducts.add(zenteProduct);
            }

            Thread.sleep(randomSec * 1000L);

        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("ERROR" + e.getMessage());
        }


        return zenteProducts;
    }

    public void loadData(ChromeDriver driver, WebDriverWait wait, String[][] brandUrlList) {

        int randomSec = RandomUtil.getRandomSecDefault();

        try {
            Thread.sleep(randomSec * 1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }


        for (int i = 0; i < brandUrlList.length; i++) {
            List<ZenteProduct> pageProductData = new ArrayList<>();
            try {
                pageProductData = getPageProductData(driver, wait, brandUrlList[i][2], brandUrlList[i][0], brandUrlList[i][1]);
            } catch (Exception e) {
                continue;
            }
            if (pageProductData.size() == 0) {
                continue;
            }

            Map<String, ZenteProduct> eachBrandHashMap = zenteBrandHashData.getBrandHashMap(brandUrlList[i][0], brandUrlList[i][1]);
            for (ZenteProduct product : pageProductData) {
                eachBrandHashMap.put(product.getId(), product);
            }

        }
    }
}

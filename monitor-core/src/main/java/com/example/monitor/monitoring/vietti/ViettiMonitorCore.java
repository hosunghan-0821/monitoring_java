package com.example.monitor.monitoring.vietti;

import chrome.ChromeDriverTool;
import com.example.monitor.file.ProductFileWriter;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import com.example.monitor.monitoring.global.IMonitorService;
import com.example.monitor.monitoring.style.StyleProduct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.discord.DiscordBot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DISCOUNT_CHANGE;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.NEW_PRODUCT;
import static com.example.monitor.monitoring.style.StyleFindString.STYLE;
import static com.example.monitor.monitoring.style.StyleFindString.STYLE_BRAND_NAME_LIST;
import static com.example.monitor.monitoring.style.StyleFindString.STYLE_BRAND_URL_LIST;
import static com.example.monitor.monitoring.style.StyleFindString.STYLE_LOG_PREFIX;
import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI;
import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI_BRAND_NAME_LIST;
import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI_BRAND_URL_LIST;
import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI_LOG_PREFIX;
import static module.discord.DiscordString.STYLE_NEW_PRODUCT_CHANNEL;
import static module.discord.DiscordString.VIETTI_DISCOUNT_CHANNEL;
import static module.discord.DiscordString.VIETTI_NEW_PRODUCT_CHANNEL;


@Slf4j
@Component
@RequiredArgsConstructor
public class ViettiMonitorCore implements IMonitorService {


    private final DiscordBot discordBot;

    @Getter
    private final ViettiBrandHashData viettiBrandHashData;

    @Value("${vietti.user.id}")
    private String userId;

    @Value("${vietti.user.pw}")
    private String userPw;

    private final ProductFileWriter productFileWriter;

    @Override
    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {
        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        //로그인
        login(driver, wait);

        loadData(driver, wait, VIETTI_BRAND_URL_LIST, VIETTI_BRAND_NAME_LIST);
        chromeDriverTool.isLoadData(true);
    }

    public void loadData(ChromeDriver driver, WebDriverWait wait, String[] brandUrlList, String[] brandNameList) {
        for (int i = 0; i < brandUrlList.length; i++) {

            String brandUrl = brandUrlList[i];
            String brandName = brandNameList[i];
            List<ViettiProduct> viettiProductList = getPageProductData(driver, wait, brandUrl, brandName);

            Map<String, ViettiProduct> brandHashMap = viettiBrandHashData.getBrandHashMap(brandName);

            for (ViettiProduct viettiProduct : viettiProductList) {
                brandHashMap.put(viettiProduct.getId(), viettiProduct);
            }
        }

    }


    @Override
    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {
        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        List<ViettiProduct> viettiProducts = new ArrayList<>();
        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(VIETTI_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }

        log.info(VIETTI_LOG_PREFIX + "VIETTI FIND NEW PRODUCT START==");
        List<ViettiProduct> viettiProductList = findDifferentAndAlarm(chromeDriver, wait, VIETTI_BRAND_URL_LIST, VIETTI_BRAND_NAME_LIST);

        log.info(VIETTI_LOG_PREFIX + "VIETTI FIND NEW PRODUCT FINISH==");
    }


    @Override
    public void login(ChromeDriver driver, WebDriverWait wait) {

        driver.get("https://it-buyer.viettishop.com/en/access");
        WebElement loginElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("mat-input-2")));
        WebElement pwElement = driver.findElement(By.id("mat-input-3"));

        loginElement.sendKeys(userId);
        pwElement.sendKeys(userPw);

        try {
            Thread.sleep(1500);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebElement button = driver.findElement(By.xpath("//button[@class='mat-focus-indicator mat-flat-button mat-button-base login__button']"));
        button.click();

        //로그인 이후 5초 정지..
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ViettiProduct> findDifferentAndAlarm(ChromeDriver chromeDriver, WebDriverWait wait, String[] viettiBrandUrlList, String[] viettiBrandNameList) {
        assert (viettiBrandUrlList.length == viettiBrandNameList.length);

        List<ViettiProduct> findViettiProductList = new ArrayList<>();

        for (int i = 0; i < viettiBrandUrlList.length; i++) {

            Map<String, ViettiProduct> eachBrandHashMap = viettiBrandHashData.getBrandHashMap(viettiBrandNameList[i]);
            List<ViettiProduct> viettiProductList = new ArrayList<>();

            try {
                viettiProductList = getPageProductData(chromeDriver, wait, viettiBrandUrlList[i], viettiBrandNameList[i]);
            } catch (Exception e) {
                log.error(STYLE_LOG_PREFIX + "logout error maybe");
                login(chromeDriver, wait);
                viettiProductList = getPageProductData(chromeDriver, wait, viettiBrandUrlList[i], viettiBrandNameList[i]);
            }

            HashSet<String> productKeySet = viettiBrandHashData.getProductKeySet();
            for (ViettiProduct viettiProduct : viettiProductList) {

                //새 제품인 경우
                if (!eachBrandHashMap.containsKey(viettiProduct.getId())) {
                    if (!productKeySet.contains(viettiProduct.getId())) {
                        //discord bot 알람
                        getProductMoreInfo(chromeDriver, wait, viettiProduct);
                        log.info(VIETTI_LOG_PREFIX + "새로운 제품" + viettiProduct);

                        discordBot.sendNewProductInfoCommon(
                                VIETTI_NEW_PRODUCT_CHANNEL,
                                viettiProduct.makeDiscordMessageDescription(),
                                viettiProduct.getProductLink(),
                                viettiProduct.getImageSrc(),
                                Stream.of(viettiProduct.getSku()).toArray(String[]::new)
                        );
                        findViettiProductList.add(viettiProduct);

                        productFileWriter.writeProductInfo(viettiProduct.changeToProductFileInfo(VIETTI, NEW_PRODUCT));

                        //보낸상품 체크
                        productKeySet.add(viettiProduct.getId());

                    }
                } else {
                    ViettiProduct beforeProduct = eachBrandHashMap.get(viettiProduct.getId());
                    if (!beforeProduct.getDiscountPercentage().equals(viettiProduct.getDiscountPercentage())) {
                        log.info(STYLE_LOG_PREFIX + "할인율 변경" + beforeProduct.getDiscountPercentage() + " -> " + viettiProduct.getDiscountPercentage());
                        //discord bot 알람
                        getProductMoreInfo(chromeDriver, wait, viettiProduct);

                        //TO-DO
                        discordBot.sendDiscountChangeInfoCommon(
                                VIETTI_DISCOUNT_CHANNEL,
                                viettiProduct.makeDiscordDiscountMessageDescription(beforeProduct.getDiscountPercentage()),
                                viettiProduct.getProductLink(),
                                viettiProduct.getImageSrc(),
                                Stream.of(viettiProduct.getSku()).toArray(String[]::new)
                        );

                        findViettiProductList.add(viettiProduct);
                        productFileWriter.writeProductInfo(viettiProduct.changeToProductFileInfo(STYLE, DISCOUNT_CHANGE));
                    }
                }
            }

            if (viettiProductList.size() > 0) {
                eachBrandHashMap.clear();
                for (ViettiProduct viettiProduct : viettiProductList) {
                    eachBrandHashMap.put(viettiProduct.getId(), viettiProduct);
                }
            } else {
                log.error(STYLE_LOG_PREFIX + "styleProduct 데이터 조회 실패 **확인요망**");
            }

        }

        return findViettiProductList;
    }

    public void getProductMoreInfo(ChromeDriver chromeDriver, WebDriverWait wait, ViettiProduct viettiProduct) {
        chromeDriver.get(viettiProduct.getProductLink());

        try {

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='product-technical-datatable__table__row ng-star-inserted']")));

            Thread.sleep(3000);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='loader-wrapper']")));

            List<WebElement> moreInfoList = chromeDriver.findElements(By.xpath("//div[@class='product-technical-datatable__table__row ng-star-inserted']"));

            for (WebElement moreInfo : moreInfoList) {
                String key = moreInfo.findElement(By.xpath(".//div[@class='product-technical-datatable__table__header']")).getText();
                String value = moreInfo.findElement(By.xpath(".//div[@class='product-technical-datatable__table__value']")).getText();

                key = key.toUpperCase().strip();

                if (key.equals("SPU")) {
                    viettiProduct.updateSku(value);
                }
                if (key.equals("MADE IN")) {
                    viettiProduct.updateMadeBy(value);
                }
            }

            WebElement madeByElement = chromeDriver.findElement(By.xpath("//div[@class='product-technical-datatable__table__row last ng-star-inserted']"));

            String key = madeByElement.findElement(By.xpath(".//div[@class='product-technical-datatable__table__header']")).getText();
            String value = madeByElement.findElement(By.xpath(".//div[@class='product-technical-datatable__table__value']")).getText();

            viettiProduct.updateMadeBy(value);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("상세정보 획득 실패 로그확인");
        }


    }


    public List<ViettiProduct> getPageProductData(ChromeDriver driver, WebDriverWait wait, String url, String brandName) {

        driver.get(url);

        List<ViettiProduct> pageProductList = new ArrayList<>();

        int totalPages = 1;
        int productTotalNum = 48;

        //페이지 로드 대기
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='products-grid center ng-star-inserted']")));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='product-counter']")));
            WebElement headerElement = driver.findElement(By.xpath("//div[@class='product-counter']"));
            wait.until(ExpectedConditions.textToBePresentInElement(headerElement, "product"));

            String pageInfo = headerElement.getText();
            String totalProducts = pageInfo.split("of")[1].strip().split(" ")[0];
            totalPages = Integer.parseInt(totalProducts) % productTotalNum == 0 ? Integer.parseInt(totalProducts) / productTotalNum : Integer.parseInt(totalProducts) / productTotalNum + 1;

        } catch (Exception e) {
            e.printStackTrace();
            log.error(VIETTI_LOG_PREFIX + "전체 페이지 정보 없음 제1 페이지만 돕니다.");
        }

        for (int i = 1; i <= totalPages; i++) {

            driver.get(url + "?p=" + i);

            //상품 최외각 div
            WebElement productParentElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='products-grid center ng-star-inserted']")));

            //상품 내각 div
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='product-grid-element ng-star-inserted']")));
            } catch (Exception e) {
                log.error(VIETTI_LOG_PREFIX + "상품정보 없음 url 확인" + url);
            }

            //상품 정보 최워질 때까지 대기
            WebElement priceWaitElement = driver.findElement(By.xpath("//div[@class='price-wrapper__compact__current-unit-price currency-value']"));
            wait.until(ExpectedConditions.textToBePresentInElement(priceWaitElement, "€"));


            List<WebElement> productElements = productParentElement.findElements(By.xpath(".//div[@class='product-grid-element ng-star-inserted']"));


            for (WebElement product : productElements) {

                String productId = "";

                String productName = "상품 이름 정보 오류";
                String productLink = "";


                String productDiscountPercentage = "0%";
                String productOriginPrice = "";
                String productPrice = "";
                String imageSrc = "";
                double productDoublePrice = 0;

                //상품 id
                try {
                    WebElement idElement = product.findElement(By.xpath(".//div[@class='product__code']"));
                    productId = idElement.getText();
                } catch (Exception e) {
                    log.error(VIETTI_LOG_PREFIX + "상품 아이디가 없습니다. \t 상품 누락됩니다. 확인요망");
                    continue;
                }

                //상품 상세정보 , 이름
                try {
                    WebElement nameElement = product.findElement(By.xpath(".//a[@class='product__name']"));
                    productName = nameElement.getText();
                    productLink = nameElement.getAttribute("href");
                } catch (Exception e) {
                    log.error(VIETTI_LOG_PREFIX + "상품 이름과 상품 상세링크가 없습니다. \t. 확인요망");

                }

                //할인율 및 기타 정보
                try {
                    WebElement discountPercentageElement = product.findElement(By.xpath(".//span[@class='price-wrapper__full__discounts__value ng-star-inserted']"));
                    productDiscountPercentage = discountPercentageElement.getText();

                    WebElement priceElement = product.findElement(By.xpath(".//div[@class='price-wrapper__compact__current-unit-price currency-value']"));
                    productPrice = priceElement.getText();
                    WebElement originPriceElement = product.findElement(By.xpath(".//del[@class='price-wrapper__compact__old-unit-price currency-value ng-star-inserted']"));
                    productOriginPrice = originPriceElement.getText();
                } catch (Exception e) {

                    try {
                        WebElement priceElement = product.findElement(By.xpath(".//div[@class='price-wrapper__compact__current-unit-price currency-value']"));
                        productOriginPrice = priceElement.getText();
                        productPrice = priceElement.getText();
                    } catch (Exception ex) {
                        log.error("할인정보 없음 + 가격정보도 없음 url" + url + "productName" + productName);
                        continue;
                    }

                }

                //이미지 링크
                try {
                    WebElement imageElement = product.findElement(By.xpath(".//img[@class='ng-star-inserted']"));
                    imageSrc = imageElement.getAttribute("src");
                } catch (Exception e) {
                    log.error(VIETTI_LOG_PREFIX + "이미지 링크 오류");
                }

                ViettiProduct viettiProduct = ViettiProduct.builder()

                        .id(productId)
                        .name(productName)
                        .price(productPrice)
                        .originPrice(productOriginPrice)
                        .discountPercentage(productDiscountPercentage)
                        .productLink(productLink)
                        .imageSrc(imageSrc)
                        .brandName(brandName)
                        .sku(productId)
                        .build();


                if (productId.isEmpty()) {
                    log.error(VIETTI_LOG_PREFIX+ "상품 정보 획득에러 수정필요 !!!");
                }
                pageProductList.add(viettiProduct);
            }

        }

        return pageProductList;
    }

}

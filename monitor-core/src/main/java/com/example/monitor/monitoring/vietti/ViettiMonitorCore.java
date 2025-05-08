package com.example.monitor.monitoring.vietti;

import chrome.ChromeDriverTool;
import com.example.monitor.file.ProductFileWriter;
import com.example.monitor.monitoring.global.IMonitorService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.discord.DiscordBot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DISCOUNT_CHANGE;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.NEW_PRODUCT;
import static com.example.monitor.monitoring.style.StyleFindString.STYLE;
import static com.example.monitor.monitoring.style.StyleFindString.STYLE_LOG_PREFIX;
import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI;
import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI_BRAND_NAME_LIST;
import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI_BRAND_URL_LIST;
import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI_LOG_PREFIX;
import static module.discord.DiscordString.VIETTI_DISCOUNT_CHANNEL;
import static module.discord.DiscordString.VIETTI_NEW_PRODUCT_CHANNEL;


@Slf4j
@Component
@RequiredArgsConstructor
public class ViettiMonitorCore implements IMonitorService {


    private final ViettiMonitorRetry viettiMonitorRetry;

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
            List<ViettiProduct> viettiProductList = viettiMonitorRetry.getPageProductData(driver, wait, brandUrl, brandName);

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
    public void login(WebDriver driver, WebDriverWait wait) {

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
                viettiProductList = viettiMonitorRetry.getPageProductData(chromeDriver, wait, viettiBrandUrlList[i], viettiBrandNameList[i]);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(STYLE_LOG_PREFIX + "logout error maybe");
                login(chromeDriver, wait);
                viettiProductList = viettiMonitorRetry.getPageProductData(chromeDriver, wait, viettiBrandUrlList[i], viettiBrandNameList[i]);
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



}

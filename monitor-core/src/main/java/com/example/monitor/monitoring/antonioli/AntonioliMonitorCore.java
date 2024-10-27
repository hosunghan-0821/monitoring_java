package com.example.monitor.monitoring.antonioli;

import chrome.ChromeDriverTool;
import com.example.monitor.Util.RandomUtil;
import com.example.monitor.file.ProductFileWriter;
import com.example.monitor.monitoring.biffi.BiffiProduct;
import com.example.monitor.monitoring.dobulef.DoubleFFindString;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import com.example.monitor.monitoring.global.IMonitorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.discord.DiscordBot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.example.monitor.monitoring.antonioli.AntonioliFindString.ANTONIOLI;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.ANTONIOLI_LOG_PREFIX;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.MANS_PREFIX;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.WOMANS_PREFIX;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.manBrandNameList;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.womanBrandNameList;
import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI;
import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI_LOG_PREFIX;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DISCOUNT_CHANGE;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F_LOG_PREFIX;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.NEW_PRODUCT;
import static module.discord.DiscordString.ANOTONIOLI_DISCOUNT_CHANNEL;
import static module.discord.DiscordString.ANOTONIOLI_NEW_PRODUCT_CHANNEL;
import static module.discord.DiscordString.BIFFI_DISCOUNT_CHANNEL;
import static module.discord.DiscordString.DOUBLE_F_NEW_PRODUCT_CHANNEL;


@Slf4j
@Component
@RequiredArgsConstructor
public class AntonioliMonitorCore implements IMonitorService {

    private final ObjectMapper objectMapper;

    private final DiscordBot discordBot;

    @Getter
    private final AntonioliBrandHashData antonioliBrandHashData;

    private final ProductFileWriter productFileWriter;

    @Override
    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();
        login(driver, wait);

        //데이터 로드
        loadData(driver, wait, womanBrandNameList, WOMANS_PREFIX);
        loadData(driver, wait, manBrandNameList, MANS_PREFIX);


        chromeDriverTool.isLoadData(true);

    }


    @Override
    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        List<AntonioliProduct> antonioliProductList = new ArrayList<>();

        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(ANTONIOLI_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }

        log.info(ANTONIOLI_LOG_PREFIX + "ANTONIOLI FIND NEW PRODUCT START==");
        List<AntonioliProduct> womanDifferent = findDifferentAndAlarm(chromeDriver, wait, womanBrandNameList, WOMANS_PREFIX);
        List<AntonioliProduct> manDifferent = findDifferentAndAlarm(chromeDriver, wait, manBrandNameList, MANS_PREFIX);

        log.info(ANTONIOLI_LOG_PREFIX + "ANTONIOLI FIND NEW PRODUCT FINISH ==");

    }

    public List<AntonioliProduct> findDifferentAndAlarm(ChromeDriver driver, WebDriverWait wait, String[] brandNameList, String sexPrefix) {
        List<AntonioliProduct> findAnotonioliProducts = new ArrayList<>();

        for (int i = 0; i < brandNameList.length; i++) {

            //페이지 URL 만들기
            String brandName = brandNameList[i];
            String url = makeBrandUrl(brandName, sexPrefix);
            List<AntonioliProduct> pageProductData = getPageProductDataOrNull(driver, wait, url, brandName);

            if (pageProductData == null) {
                discordBot.sendMessage(ANOTONIOLI_NEW_PRODUCT_CHANNEL, ANTONIOLI_LOG_PREFIX + " 페이지 로그인 오류 있을 수 있으니 확인 부탁드립니다.");
                break;
            }

            //상품 정보 존재할 경우
            Map<String, AntonioliProduct> eachBrandHashMap = antonioliBrandHashData.getBrandHashMap(sexPrefix, brandName);
            HashSet<String> productKeySet = antonioliBrandHashData.getProductKeySet();

            for (AntonioliProduct antonioliProduct : pageProductData) {
                if (!eachBrandHashMap.containsKey(antonioliProduct.getId())) {
                    //새로운 재품일 경우
                    if (!productKeySet.contains(antonioliProduct.getId())) {
                        log.info(ANTONIOLI_LOG_PREFIX + "새로운 제품" + antonioliProduct);

                        getDetailProductInfo(driver, wait, antonioliProduct);

                        discordBot.sendNewProductInfoCommon(
                                ANOTONIOLI_NEW_PRODUCT_CHANNEL,
                                antonioliProduct.makeDiscordMessageDescription(),
                                antonioliProduct.getProductLink(),
                                null,
                                Stream.of(antonioliProduct.getSku()).toArray(String[]::new)
                        );


                        findAnotonioliProducts.add(antonioliProduct);

                        productFileWriter.writeProductInfo(antonioliProduct.changeToProductFileInfo(ANTONIOLI, NEW_PRODUCT));

                        //보낸 상품 체크
                        productKeySet.add(antonioliProduct.getId());
                    } else {
                        log.error(ANTONIOLI_LOG_PREFIX + "상품 중복 " + antonioliProduct);
                    }
                } else {
                    AntonioliProduct beforeProduct = eachBrandHashMap.get(antonioliProduct.getId());
                    if (!beforeProduct.getDiscountPercentage().equals(antonioliProduct.getDiscountPercentage())) {
                        log.info(ANTONIOLI_LOG_PREFIX + "할인율 변경" + beforeProduct.getDiscountPercentage() + " -> " + antonioliProduct.getDiscountPercentage());
                        //discord bot 알람
                        getDetailProductInfo(driver, wait, antonioliProduct);
                        discordBot.sendDiscountChangeInfoCommon(
                                ANOTONIOLI_DISCOUNT_CHANNEL,
                                antonioliProduct.makeDiscordDiscountMessageDescription(beforeProduct.getDiscountPercentage()),
                                antonioliProduct.getProductLink(),
                                null,
                                Stream.of(antonioliProduct.getSku()).toArray(String[]::new)
                        );
                        findAnotonioliProducts.add(antonioliProduct);
                        productFileWriter.writeProductInfo(antonioliProduct.changeToProductFileInfo(ANTONIOLI, DISCOUNT_CHANGE));
                    }
                }
            }


        }
        return findAnotonioliProducts;
    }


    @Override
    public void login(ChromeDriver driver, WebDriverWait wait) {
        driver.get(AntonioliFindString.ANTONIOLI_MAIN_URL);
    }

    public void loadData(ChromeDriver driver, WebDriverWait wait, String[] brandNameList, String sexPrefix) {

        for (int i = 0; i < brandNameList.length; i++) {

            //페이지 URL 만들기
            String brandName = brandNameList[i];
            String url = makeBrandUrl(brandName, sexPrefix);

            List<AntonioliProduct> pageProductData = getPageProductDataOrNull(driver, wait, url, brandName);

            if (pageProductData == null) {
                discordBot.sendMessage(ANOTONIOLI_NEW_PRODUCT_CHANNEL, ANTONIOLI_LOG_PREFIX + " 페이지 로그인 오류 있을 수 있으니 확인 부탁드립니다.");
                break;
            }
            //상품 정보 존재할 경우
            Map<String, AntonioliProduct> eachBrandHashMap = antonioliBrandHashData.getBrandHashMap(sexPrefix, brandName);
            for (AntonioliProduct product : pageProductData) {
                eachBrandHashMap.put(product.getId(), product);
            }

        }

    }

    public List<AntonioliProduct> getPageProductDataOrNull(ChromeDriver driver, WebDriverWait wait, String url, String brandName) {

        // 브랜드 검사할 때 봇탐지 및 부하 줄이기 위해 랜덤 초 생성.
        try {
            int randomSec = RandomUtil.getRandomSec(5, 10);
            log.debug(brandName + "탐색 전 " + randomSec + "초 대기");
            Thread.sleep(randomSec * 1000);
        } catch (Exception e) {
            log.error(ANTONIOLI_LOG_PREFIX + "랜덤 초 실행 에러");
        }


        //페이지로 이동
        driver.get(url);

//        Actions actions = new Actions(driver);

        List<AntonioliProduct> pageProductList = new ArrayList<>();
        List<WebElement> productList = new ArrayList<>();
        int totalPages = 1;
        int productNumPerPage = 60;

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='ProductItem  ']")));
            WebElement pageElement = driver.findElement(By.xpath("//a[@class='Pagination__TotalProducts']"));
            String text = pageElement.getText();
            int productTotalNum = Integer.parseInt(text.split(" ")[2]);
            totalPages = (int) (Math.ceil(((double) productTotalNum) / productNumPerPage));
            log.debug("totalPages = " + totalPages);
        } catch (Exception e) {
            log.error(ANTONIOLI_LOG_PREFIX + "error get page number");
        }

        for (int i = 0; i < totalPages; i++) {
            if (i != 0) {
                driver.get(url + "?page=" + (i + 1));
            }
            try {

                Thread.sleep(1000); //thinkTime
                WebElement mainContainer = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='ProductItem  ']")));
                productList = mainContainer.findElements(By.xpath("//div[@class='ProductItem  ']"));
                log.info("페이지 상품 총 개수 : {}", productList.size());

            } catch (Exception e) {

                // 페이지 문제 없는지 확인하는 곳
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='ProductItem  ProductItem--sold-out']")));
                } catch (Exception e2) {

                    log.error(ANTONIOLI_LOG_PREFIX + "logout Redirection  or FIND PRODUCT ERROR");
                    return null;
                }
                log.debug("Sold Out 으로 인한 에러 -> 다음페이지로 ");
                continue;

            }

            for (WebElement product : productList) {

                String productName = "상품이름 정보 없습니다.";
                String productDiscountPercentage = "0%";
                String productPrice = "";
                String productOriginalPrice = "";
                String productId = "";
                String productLink = "";
                String productSkU = "";
                String productColorCode = "";
                String extraDiscountPercentage = "0%";
                boolean isSale = false;
                String imageUrl = "";

                String productInfo = product.getAttribute("data-product-info");

                //기본정보[Sale] GET &
                try {
//                    actions.moveToElement(product);
//                    actions.perform();

                    AntonioliLegacyProduct antonioliLegacyProduct = objectMapper.readValue(productInfo, AntonioliLegacyProduct.class);
                    productId = antonioliLegacyProduct.getGtmItemId();
                    productName = antonioliLegacyProduct.getGtmItemName();
                    productPrice = String.valueOf(antonioliLegacyProduct.getGtmItemPrice());
                    productOriginalPrice = String.valueOf(antonioliLegacyProduct.getGtmItemPrice() + antonioliLegacyProduct.getGtmItemSaleValue());
                    isSale = antonioliLegacyProduct.getGtmItemSale().equals("Yes");

                    List<WebElement> saleInfos = product.findElements(By.xpath(".//span[@class='sold_out_label-product_item pull-right']//span"));
                    if (saleInfos.size() >= 2) {
                        productDiscountPercentage = saleInfos.get(1).getText();
                    }
                } catch (Exception e) {
                    // TODO [Hosung] 여기서 Discord 알림
                    log.error(ANTONIOLI_LOG_PREFIX + " Product Info Error page = " + url);
                    e.printStackTrace();
                    continue;
                }
                //이미지 Link 정보 일단 보류
//                try {
//
//                    WebElement imageElement = product.findElement(By.xpath(".//img"));
//                    String srcset = imageElement.getAttribute("srcset");
//                    String imageSrc = srcset.strip().split(",")[0];
//                    imageUrl = "https://" + imageSrc.split(" ")[0];
//                    log.info(imageUrl);
//                } catch (Exception e) {
//                    log.error(ANTONIOLI_LOG_PREFIX + "Image Info Error page = " + url + "productID: " + productId);
//                    e.printStackTrace();
//                }


                //상품 Detail Link
                try {

                    WebElement element = product.findElement(By.xpath(".//div[@class='ProductItem__Wrapper']//a"));
                    productLink = element.getAttribute("href");


                } catch (Exception e) {
                    log.error(ANTONIOLI_LOG_PREFIX + "Detail Link Error " + url + "productID: " + productId);
                    e.printStackTrace();

                }


                AntonioliProduct antonioliProduct = AntonioliProduct.builder()
                        .id(productId)
                        .price(productOriginalPrice + " / " + productPrice)
                        .name(productName)
                        .discountPercentage(productDiscountPercentage)
                        .productLink(productLink)
                        .brandName(brandName)
                        .sku(productId)
//                        .imgUrl(imageUrl)
                        .build();

                log.debug(antonioliProduct.toString());
                pageProductList.add(antonioliProduct);
            }

        }
        return pageProductList;
    }

    public void getDetailProductInfo(ChromeDriver driver, WebDriverWait wait, AntonioliProduct product) {

        boolean isGetData = false;
        driver.get(product.getProductLink());
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='Product__Wrapper']")));
            WebElement detailElement = driver.findElement(By.xpath("//div[@class='tab']//label[text()='Details']"));
            detailElement.click();

            List<WebElement> detailElements = detailElement.findElements(By.xpath("..//div[@class='tab-content']//ul//li"));
            for (WebElement webElement : detailElements) {
                log.debug(webElement.getText());
                String text = webElement.getText();
                isGetData = true;
                if (text.contains("Made in")) {
                    String madeBy = text.split(":")[1];
                    product.updateMadeBy(madeBy);
                    return;
                }
            }
            if (isGetData) {
                product.updateMadeBy("원산지 찾지 못함 페이지 확인");
                log.error(ANTONIOLI_LOG_PREFIX + "원산지 찾지 못함 페이지 확인 / check productlink = " + product.getProductLink());
            }
        } catch (Exception e) {
            log.error(ANTONIOLI_LOG_PREFIX + "get product detail error /  check productlink = " + product.getProductLink());
        }

    }

    public String makeBrandUrl(String brandName, String sexPrefix) {

        return "https://stores.antonioli.eu/collections/designer-" + brandName + "/" + sexPrefix;
    }

}

package com.example.monitor.monitoring.julian;

import chrome.ChromeDriverTool;
import com.example.monitor.file.ProductFileWriter;
import com.example.monitor.infra.converter.controller.IConverterFacade;
import com.example.monitor.monitoring.gebnegozi.GebenegoziProduct;
import module.database.dto.Boutique;
import module.discord.DiscordBot;
import com.example.monitor.monitoring.global.IMonitorService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.GEBENE_LOG_PREFIX;
import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.GNB;
import static com.example.monitor.monitoring.julian.JulianSaleInfoString.FALL_WINTER_2024_2025;
import static module.discord.DiscordString.ALL_CATEGORIES_CHANNEL;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.NEW_PRODUCT;
import static com.example.monitor.monitoring.julian.JulianFindString.*;
import static module.discord.DiscordString.JULIAN_LEMAIRE;

@Slf4j
@Component
@RequiredArgsConstructor
public class JulianMonitorCore implements IMonitorService {


    private final DiscordBot discordBot;

    private final ProductFileWriter productFileWriter;

    @Getter
    private final JulianBrandHashData julianBrandHashData;

    private final IConverterFacade iConverterFacade;
    @Value("${julian.user.id}")
    private String userId;

    @Value("${julian.user.pw}")
    private String userPw;


    public void changeUrl(ChromeDriver driver, String url) {
        driver.get(url);
    }

    @Override
    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {

        try {
            ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
            WebDriverWait wait = chromeDriverTool.getWebDriverWait();
            HashMap<String, JulianProduct> brandHashMap = this.getJulianBrandHashData().getBrandHashMap(ALL_CATEGORIES);

            login(chromeDriver, wait);

            for (int i = 1; i < 5; i++) {
                String url = getUrl(ALL_CATEGORIES_URL, i);
                //페이지 이동
                changeUrl(chromeDriver, url);

                //하위 데이터
                List<WebElement> productDataDivs = getInnerProductDivs(wait);

                //상품 하위 데이터 조회
                List<JulianProduct> productData = getProductData(productDataDivs, url);

                //정보가져오기
                loadData(brandHashMap, productData);
            }

            try {
                for (int i = 1; i < 3; i++) {
                    String url = getUrl(LEMAIRE_BAGS_URL, i);
                    //페이지 이동
                    changeUrl(chromeDriver, url);

                    //하위 데이터
                    List<WebElement> productDataDivs = getInnerProductDivs(wait);

                    //상품 하위 데이터 조회
                    List<JulianProduct> productData = getProductData(productDataDivs, url);

                    //정보가져오기
                    loadData(brandHashMap, productData);
                }
            } catch (Exception e) {
                log.info(JULIAN_LOG_PREFIX + "== LEMARIE NO BAGS LOAD DATA FINISH ==");
            }

            //로드체크
            chromeDriverTool.isLoadData(true);
            log.info(JULIAN_LOG_PREFIX + "== ALL CATEGORIES LOAD DATA FINISH ==");
        } catch (Exception e) {
            log.error(JULIAN_LOG_PREFIX + "All Category Data Load Error");
        }
    }

    @Override
    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {


        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        HashMap<String, JulianProduct> dataHashMap = julianBrandHashData.getBrandHashMap(ALL_CATEGORIES);
        HashSet<String> dataKeySet = julianBrandHashData.getProductKeySet();

        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(JULIAN_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }
        log.info(JULIAN_LOG_PREFIX + "START: " + ALL_CATEGORIES + " FIND NEW PRODUCT ");
        List<JulianProduct> findJulianProductList = new ArrayList<>();

        try {
            for (int i = 1; i < 5; i++) {
                String url = getUrl(ALL_CATEGORIES_URL, i);

                //페이지 이동
                changeUrl(chromeDriver, url);

                //하위 데이터
                List<WebElement> productDataDivs = getInnerProductDivs(wait);

                //상품 하위 데이터 조회
                List<JulianProduct> julianProductData = getProductData(productDataDivs, url);


                //Send Auto Order Server
                try {
                    sendAutoOrderServer(julianProductData);
                } catch (Exception e) {
                    log.error(GEBENE_LOG_PREFIX + "자동주문 버그 MSG: " + e.getMessage());
                }

                //데이터 누적 HashMap 수정을 위해서
                findJulianProductList.addAll(julianProductData);

                //정보가져오기
                List<JulianProduct> newJulianProductList = findNewProduct(dataHashMap, julianProductData);


                if (julianProductData.size() != 48) {
                    log.info(JULIAN_LOG_PREFIX + "한 페이지에 size 개수 변동 확인요망! 현재사이즈 = " + julianProductData.size());
                }
                if (!newJulianProductList.isEmpty()) {

                    for (JulianProduct julianProduct : newJulianProductList) {

                        //새 상품 set에 있다면, 알람x 보내면 안됨.
                        if (dataKeySet.contains(julianProduct.getSku())) {
                            log.info(JULIAN_LOG_PREFIX + "이전에 알람 보냈던 제품 PASS 상품ID " + julianProduct.getSku());
                            continue;
                        }

                        if (!Arrays.stream(JULIAN_TARGET_BRAND_NAME_LIST).toList().contains(julianProduct.getBrandName())) {
                            log.info(JULIAN_LOG_PREFIX + "해당하지 않는 시즌의 브랜드 제품 PASS" + julianProduct.getSeason() + "\t" + julianProduct.getSku());
                            continue;
                        }

                        //새 상품 set에 없다면, 알람 보내고, 보낸걸 기록
                        dataKeySet.add(julianProduct.getSku());

                        getProductMoreInfo(chromeDriver, wait, julianProduct);

                        //discordBot.sendNewProductInfo(ALL_CATEGORIES_CHANNEL, julianProduct);
                        discordBot.sendNewProductInfoCommon(
                                ALL_CATEGORIES_CHANNEL,
                                julianProduct.makeDiscordMessageDescription(),
                                julianProduct.getProductLink(),
                                julianProduct.getImageUrl(),
                                Stream.of(julianProduct.getSku()).toArray(String[]::new)
                        );


                        productFileWriter.writeProductInfo(julianProduct.changeToProductFileInfo(JULIAN + " / " + ALL_CATEGORIES, NEW_PRODUCT));
                        log.info(JULIAN_LOG_PREFIX + "New Product = " + julianProduct);
                    }
                }
            }


            try {
                for (int i = 1; i < 3; i++) {
                    String url = getUrl(LEMAIRE_BAGS_URL, i);
                    //페이지 이동
                    changeUrl(chromeDriver, url);

                    //하위 데이터
                    List<WebElement> productDataDivs = getInnerProductDivs(wait);

                    //상품 하위 데이터 조회
                    List<JulianProduct> julianProductData = getProductData(productDataDivs, url);

                    //Send Auto Order Server
                    try {
                        sendAutoOrderServer(julianProductData);
                    } catch (Exception e) {
                        log.error(GEBENE_LOG_PREFIX + "자동주문 버그 MSG: " + e.getMessage());
                    }

                    //데이터 누적 HashMap 수정을 위해서
                    findJulianProductList.addAll(julianProductData);

                    //정보가져오기
                    List<JulianProduct> newJulianProductList = findNewProduct(dataHashMap, julianProductData);


                    if (julianProductData.size() != 48) {
                        log.info(JULIAN_LOG_PREFIX + "한 페이지에 size 개수 변동 확인요망! 현재사이즈 = " + julianProductData.size());
                    }
                    if (!newJulianProductList.isEmpty()) {

                        for (JulianProduct julianProduct : newJulianProductList) {

                            //새 상품 set에 있다면, 알람x 보내면 안됨.
                            if (dataKeySet.contains(julianProduct.getSku())) {
                                log.info(JULIAN_LOG_PREFIX + "이전에 알람 보냈던 제품 PASS 상품ID " + julianProduct.getSku());
                                continue;
                            }

                            if (!Arrays.stream(JULIAN_TARGET_BRAND_NAME_LIST).toList().contains(julianProduct.getBrandName())) {
                                log.info(JULIAN_LOG_PREFIX + "해당하지 않는 시즌의 브랜드 제품 PASS" + julianProduct.getSeason() + "\t" + julianProduct.getSku());
                                continue;
                            }

                            //새 상품 set에 없다면, 알람 보내고, 보낸걸 기록
                            dataKeySet.add(julianProduct.getSku());

                            getProductMoreInfo(chromeDriver, wait, julianProduct);

                            //discordBot.sendNewProductInfo(ALL_CATEGORIES_CHANNEL, julianProduct);
                            discordBot.sendNewProductInfoCommon(
                                    JULIAN_LEMAIRE,
                                    julianProduct.makeDiscordMessageDescription(),
                                    julianProduct.getProductLink(),
                                    julianProduct.getImageUrl(),
                                    Stream.of(julianProduct.getSku()).toArray(String[]::new)
                            );


                            productFileWriter.writeProductInfo(julianProduct.changeToProductFileInfo(JULIAN + " / " + LEMAIRE_BAGS_URL, NEW_PRODUCT));
                            log.info(JULIAN_LOG_PREFIX + "New Product = " + julianProduct);
                        }
                    }
                }
            } catch (Exception e) {
                log.error(JULIAN_LOG_PREFIX + "NO LEMAIRE BAGS");
            }

            // 이후에 HashMap 재 정립
            dataHashMap.clear();
            loadData(dataHashMap, findJulianProductList);

        } catch (NoSuchWindowException e) {

            log.error(JULIAN_LOG_PREFIX + "Chrome Driver Down!!");
            return;
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(JULIAN_LOG_PREFIX + "자동 로그아웃");
            // 모니터링 다시 시작
            login(chromeDriver, wait);
        }

        log.info(JULIAN_LOG_PREFIX + "END:  FIND NEW PRODUCT FINISH");

    }


    @Override
    public void login(ChromeDriver driver, WebDriverWait wait) {
        assert (driver != null);

        try {
            driver.get("https://b2bfashion.online/");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id(ID_FORM)));
            WebElement id = driver.findElement(By.id(ID_FORM));
            id.sendKeys(userId);

            WebElement password = driver.findElement(By.id(PASS_FORM));
            password.sendKeys(userPw);

            WebElement loginButton = driver.findElement(By.id(SUBMIT_FORM));
            loginButton.click();

            //로그인 후 멈춤
            Thread.sleep(5000);
        } catch (Exception e) {
            log.error(JULIAN_LOG_PREFIX + "로그인 에러");
            e.printStackTrace();
        }

    }


    public List<WebElement> getInnerProductDivs(WebDriverWait wait) {
        WebElement topDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(PRODUCT_TOP_DIV)));
        return topDiv.findElements(By.xpath(CHILD_DIV));
    }

    public List<JulianProduct> getProductData(List<WebElement> childDivs, String findUrl) {

        List<JulianProduct> julianProductList = new ArrayList<>();

        for (WebElement child : childDivs) {
            WebElement image = child.findElement(By.xpath(PRODUCT_IMAGE));
            WebElement name = child.findElement(By.xpath(PRODUCT_NAME));
            WebElement reference = child.findElement(By.xpath(PRODUCT_SKU));
            WebElement season = child.findElement(By.xpath(".//div[@class='fea-min-prod']"));

            String imageSrc = image.getAttribute("src");

            WebElement priceElement = child.findElement(By.xpath(".//div[@class='product-description']//p"));
            String priceString = "가격정보 없음";
            priceString = priceElement.getText().trim().split(" ")[1];

            JulianProduct julianProduct = JulianProduct.builder()
                    .name(name.getText())
                    .brandName(name.getText())
                    .sku(reference.getText())
                    .imageUrl(imageSrc)
                    .price(priceString)
                    .productLink(findUrl)
                    .originPrice(priceString)
                    .season(season.getText().toUpperCase())
                    .boutique(Boutique.JULIAN.getName())
                    .build();
            julianProductList.add(julianProduct);

        }

        return julianProductList;

    }

    public void loadData(HashMap<String, JulianProduct> productHashMap, List<JulianProduct> julianProductData) {

        for (JulianProduct julianProduct : julianProductData) {
            if (!productHashMap.containsKey(julianProduct.getSku())) {
                productHashMap.put(julianProduct.getSku(), julianProduct);
            } else {
                log.error(JULIAN_LOG_PREFIX + "Load 시 겹치는 ID 존재 확인 필요 상품정보 " + julianProduct.toString());
            }
        }
    }

    public List<JulianProduct> findNewProduct(HashMap<String, JulianProduct> productHashMap, List<JulianProduct> julianProductData) {
        List<JulianProduct> newJulianProductList = new ArrayList<>();

        for (JulianProduct julianProduct : julianProductData) {
            if (!productHashMap.containsKey(julianProduct.getSku())) {

                newJulianProductList.add(julianProduct);
            }
        }

        return newJulianProductList;
    }


    public void getProductMoreInfo(WebDriver driver, WebDriverWait wait, JulianProduct julianProduct) {

        if (!julianProduct.getProductLink().equals(driver.getCurrentUrl())) {
            driver.get(julianProduct.getProductLink());
        }

        try {
            getInnerProductDivs(wait);
            WebElement element = driver.findElement(By.xpath("//div[@class='produt_reference' and contains(text(),'" + julianProduct.getSku() + "')]/.."));
            //정보가져오기
            WebElement detailLink = element.findElement(By.xpath(".//a[@class='button-action quick-view']"));
            //해당상품으로 이동
            Actions actions = new Actions(driver);
            actions.moveToElement(detailLink);
            actions.click().perform();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@class='name']")));
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='close']//span")));
            List<WebElement> productDataList = driver.findElements(By.xpath("//li[@class='name']"));
            for (WebElement productDataElement : productDataList) {
                String text = productDataElement.getText();
                text = text.toLowerCase(Locale.ENGLISH);
                if (text.contains("made in")) {
                    String madeBy = text.split(":")[1].strip();
                    julianProduct.setMadeBy(madeBy);
                } else if (text.contains("gender")) {
                    String gender = text.split(":")[1].strip().toUpperCase();
                    julianProduct.setGender(gender);
                } else if (text.contains("season")) {
                    String season = text.split(":")[1].strip().toUpperCase();
                    julianProduct.setSeason(season);
                } else if (text.contains("type")) {
                    String category = text.split(":")[1].strip().toUpperCase();
                    julianProduct.setCategory(category);
                }
            }
            setProductPriceInfo(julianProduct);

            driver.findElement(By.xpath("//button[@class='close']//span")).click();
        } catch (Exception e) {
            log.error("more info 오류 sku = " + julianProduct.getSku());
            //대응하는 코드 짜야 TO-DO
            setProductPriceInfoWhenFail(julianProduct);
        }
    }

    public void setProductPriceInfoWhenFail(JulianProduct julianProduct) {
        HashMap<String, JulianSaleInfo> julianSaleInfoHashMap = julianBrandHashData.getJulianSaleInfoHashMap();
        String findKeyPrefix = julianProduct.getBrandName() + "_" + julianProduct.getSeason();
        StringBuilder sb = new StringBuilder();
        StringBuilder defaultBuilder = new StringBuilder();

        List<InnerSortingContainer> specificContainer = new ArrayList<>();
        List<InnerSortingContainer> defaultContainer = new ArrayList<>();

        try {
            for (var entry : julianSaleInfoHashMap.entrySet()) {
                String key = entry.getKey();
                if (key.contains(findKeyPrefix)) {
                    String[] split = entry.getKey().split("_");
                    if (!split[3].equals("UNISEX")) {
                        JulianSaleInfo saleInfo = entry.getValue();
                        double wholeSaleAfter = Double.parseDouble(julianProduct.getOriginPrice()) * (saleInfo.getSalesPercent() + 100) / 100;
                        //sb.append(split[3] + " " + saleInfo.getCategory() + saleInfo.getSalesPercent() + "%" + " = " + wholeSaleAfter +"\n");
                        InnerSortingContainer innerSortingContainer = new InnerSortingContainer(split[3] + " " + saleInfo.getCategory(), split[3] + " " + saleInfo.getCategory() + " " + saleInfo.getSalesPercent() + "%" + " = " + wholeSaleAfter + "\n");
                        specificContainer.add(innerSortingContainer);
                    }

                }

                if (key.contains("OTHER BRANDS_" + julianProduct.getSeason())) {
                    String[] split = entry.getKey().split("_");
                    if (!split[3].equals("UNISEX")) {
                        JulianSaleInfo saleInfo = entry.getValue();
                        double wholeSaleAfter = Double.parseDouble(julianProduct.getOriginPrice()) * (saleInfo.getSalesPercent() + 100) / 100;
                        //defaultBuilder.append(split[3] + " " + saleInfo.getCategory()+" " + saleInfo.getSalesPercent() + "%" + " = " + wholeSaleAfter + "\n");
                        InnerSortingContainer innerSortingContainer = new InnerSortingContainer(split[3] + " " + saleInfo.getCategory(), split[3] + " " + saleInfo.getCategory() + " " + saleInfo.getSalesPercent() + "%" + " = " + wholeSaleAfter + "\n");
                        defaultContainer.add(innerSortingContainer);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("more info 대응 실패");
        }
        if (specificContainer.isEmpty()) {
            defaultBuilder.insert(0, "OTHER BRADNS \n");
            defaultContainer.sort((o1, o2) -> o1.key.compareTo(o2.key));
            for (var data : defaultContainer) {
                defaultBuilder.append(data.resultString);
            }
            julianProduct.setKeyInfo(defaultBuilder.toString());
        } else {
            //key set
            specificContainer.sort((o1, o2) -> o1.key.compareTo(o2.key));
            for (var data : specificContainer) {
                sb.append(data.resultString);
            }
            julianProduct.setKeyInfo(sb.toString());
        }
    }

    public void setProductPriceInfo(JulianProduct julianProduct) {

        String key = julianBrandHashData.makeSalesInfoKey(julianProduct.getName(), julianProduct.getSeason(), julianProduct.getCategory(), julianProduct.getGender());
        String defaultKey = julianBrandHashData.makeSalesInfoKey("OTHER BRANDS", julianProduct.getSeason(), julianProduct.getCategory(), julianProduct.getGender());
        JulianSaleInfo defaultSalesInfoOrNull = julianBrandHashData.getJulianSaleInfoHashMap().getOrDefault(defaultKey, null);
        JulianSaleInfo julianSaleInfoOrNull = julianBrandHashData.getJulianSaleInfoHashMap().getOrDefault(key, defaultSalesInfoOrNull);

        String wholeSaleOrigin = "0.0";
        String wholeSale = "0.0";
        int wholeSalePercent = 0;

        if (julianSaleInfoOrNull != null) {
            wholeSaleOrigin = julianProduct.getPrice().trim().replaceAll(",", "");
            wholeSalePercent = julianSaleInfoOrNull.getSalesPercent();
            double wholeSaleAfter = Double.parseDouble(wholeSaleOrigin) * (wholeSalePercent + 100) / 100;
            wholeSale = String.valueOf(wholeSaleAfter);
        }

        if (wholeSaleOrigin.equals("0.0")) {
            wholeSaleOrigin = julianProduct.getOriginPrice();
        }

        julianProduct.setMorePriceInfo(wholeSaleOrigin, wholeSale, wholeSalePercent, julianSaleInfoOrNull != null ? julianSaleInfoOrNull.toString() : null);
    }

    public String getUrl(String pageUrl, int i) {
        String findUrl;
        if (i == 1) {
            findUrl = pageUrl;
        } else {
            findUrl = pageUrl + "?page=" + i;
        }
        return findUrl;
    }


    private void sendAutoOrderServer(List<JulianProduct> julianProductData) {

        Set<String> alreadySent = new HashSet<>();  // 한번만 생성해서 루프 전체에서 활용
        int batchSize = 10;

        for (int j = 0; j < julianProductData.size(); j += batchSize) {
            int end = Math.min(j + batchSize, julianProductData.size());
            List<JulianProduct> batch = julianProductData.subList(j, end);

            // 중복 걸러내고, 이미 보낸 건은 건너뛰기
            List<JulianProduct> toSend = batch.stream()
                    .filter(p -> {
                        String sku = p.getSku();
                        if (alreadySent.contains(sku)) {
                            return false;
                        } else {
                            alreadySent.add(sku);
                            return true;
                        }
                    })
                    .toList();

            if (!toSend.isEmpty()) {
                iConverterFacade.sendToAutoOrderServerBulk(toSend);
            }
        }
    }


    private static class InnerSortingContainer {
        String key;
        String resultString;

        public InnerSortingContainer(String key, String resultString) {
            this.key = key;
            this.resultString = resultString;
        }
    }


}

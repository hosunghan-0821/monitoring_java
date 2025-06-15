package com.example.monitor.monitoring.gebnegozi;

import chrome.ChromeDriverTool;
import com.example.monitor.file.ProductFileWriter;
import com.example.monitor.infra.converter.controller.IConverterFacade;
import module.database.repository.ProductRepository;
import module.discord.DiscordBot;

import com.example.monitor.monitoring.global.IMonitorService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import s3.service.S3UploaderService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.example.monitor.monitoring.eic.EicFindString.EIC_LOG_PREFIX;
import static module.discord.DiscordString.GEBENE_NEW_PRODUCT_CHANNEL;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;
import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.*;
import static module.discord.DiscordString.GNB_STONE_ISLAND_NEW_PRODUCT_CHANNEL;

@Slf4j
@Component
@RequiredArgsConstructor
public class GebenegoziMonitorCore implements IMonitorService {


    private final DiscordBot discordBot;

    @Getter
    private final GebenegoziBrandHashData gebenegoziBrandHashData;

    @Value("${gebenegozi.user.id}")
    private String userId;

    @Value("${gebenegozi.user.pw}")
    private String userPw;


    private final IConverterFacade iConverterFacade;

    private final ProductFileWriter productFileWriter;

    private final S3UploaderService s3UploaderService;

    private final RestTemplate restTemplate;

    private final ProductRepository productRepository;


    @Override
    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();


        login(driver, wait);

        loadData(driver, wait, GEBE_URL_LIST);

        chromeDriverTool.isLoadData(true);
    }


    public void runLoadLogicStoneIsland(ChromeDriverTool chromeDriverTool) {
        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        try {
            login(driver, wait);
        } catch (Exception e) {
            log.error(EIC_LOG_PREFIX + "Discount Change login Error");
        }
        loadStoneIsland(driver, wait, STONE_ISLAND_URL_LIST);

        chromeDriverTool.isLoadData(true);
    }

    private void loadStoneIsland(ChromeDriver driver, WebDriverWait wait, String[][] urlList) {
        //데이터 정보조회
        for (String[] data : urlList) {
            String brand = data[0];
            String url = data[2];
            String category = data[1];
            String sex = data[3];

            if (!brand.equals("STONE ISLAND")) {
                continue;
            }

            Map<String, GebenegoziProduct> stoneIslandHashMap = gebenegoziBrandHashData.getGnbStoneIslandMap().get(url);
            HashSet<String> stoneIslandKeySet = gebenegoziBrandHashData.getStoneIslandKeySet();


            List<GebenegoziProduct> pageProductDataList = new ArrayList<>();
            int count = 0;
            do {
                pageProductDataList = getPageProductDataOrNull(driver, wait, url, category, sex, brand);
                if (count > 2) {
                    log.error(GEBENE_LOG_PREFIX + "GNB Load Data 3번 이상 돌아도, 제대로 된 상품이 안나옴. URL : {}", url);
                    break;
                }
                count++;
            }
            while (pageProductDataList == null || pageProductDataList.isEmpty());


            if (pageProductDataList != null && !pageProductDataList.isEmpty()) {

                for (var product : pageProductDataList) {
                    if (stoneIslandHashMap.containsKey(getGebeneProductKey(product))) {
                        log.info(GEBENE_LOG_PREFIX + "id 중복 " + product);
                        continue;
                    }
                    stoneIslandHashMap.put(getGebeneProductKey(product), product);
                    stoneIslandKeySet.add(getGebeneProductKey(product));
                }

            }

        }
    }

    @Override
    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();


        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(GEBENE_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }
        log.info(GEBENE_LOG_PREFIX + "GEBENE FIND NEW PRODUCT START");

        List<GebenegoziProduct> gebenegoziProductList = findDifferentAndAlarm(chromeDriver, wait, GEBE_URL_LIST);

        log.info(GEBENE_LOG_PREFIX + "GEBENE FIND NEW PRODUCT FINISH");
    }

    public void runFindStoneIslandProductLogic(ChromeDriverTool chromeDriverTool) {
        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();


        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(GEBENE_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }
        log.info(GEBENE_LOG_PREFIX + "GEBENE-STONEISLAND FIND NEW PRODUCT START");

        List<GebenegoziProduct> gebenegoziProductList = findDifferentAndAlarmSpecificBrand(chromeDriver, wait, STONE_ISLAND_URL_LIST, "STONE ISLAND");

        log.info(GEBENE_LOG_PREFIX + "GEBENE-STONEISLAND FIND NEW PRODUCT FINISH");
    }

    public List<GebenegoziProduct> findDifferentAndAlarmSpecificBrand(ChromeDriver driver, WebDriverWait wait, String[][] brandDataList, String specific) {
        List<GebenegoziProduct> findGebeneProductList = new ArrayList<>();
        HashSet<String> stoneIslandKeySet = gebenegoziBrandHashData.getStoneIslandKeySet();


        for (int i = 0; i < brandDataList.length; i++) {
            String brand = brandDataList[i][0];
            String url = brandDataList[i][2];
            String category = brandDataList[i][1];
            String sex = brandDataList[i][3];

            if (!specific.equals(brand)) {
                continue;
            }
            Map<String, GebenegoziProduct> gnbStoneIslandMap = gebenegoziBrandHashData.getGnbStoneIslandMap().get(url);


            List<GebenegoziProduct> pageProductDataList = getPageProductDataOrNull(driver, wait, url, category, sex, brand);

            if (pageProductDataList == null) {
                continue;
            }

            //자동주문  Business Logic
            //검증 없이 전부 API 호출

            try {
                Set<String> alreadySent = new HashSet<>();  // 한번만 생성해서 루프 전체에서 활용
                int batchSize = 10;

                for (int j = 0; j < pageProductDataList.size(); j += batchSize) {
                    int end = Math.min(j + batchSize, pageProductDataList.size());
                    List<GebenegoziProduct> batch = pageProductDataList.subList(j, end);

                    // 중복 걸러내고, 이미 보낸 건은 건너뛰기
                    List<GebenegoziProduct> toSend = batch.stream()
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
            } catch (Exception e) {
                log.error(GEBENE_LOG_PREFIX + "자동주문 버그 MSG: " + e.getMessage());
            }


            //신상품 확인 Business Logic
            for (GebenegoziProduct product : pageProductDataList) {
                if (!gnbStoneIslandMap.containsKey(getGebeneProductKey(product))) {
                    if (!stoneIslandKeySet.contains(getGebeneProductKey(product))) {
                        log.info(GEBENE_LOG_PREFIX + "새로운 제품" + product);

                        //이미지 다운로드 내 S3 대입 상태확인
                        if (s3UploaderService.isAllowedUpload()) {
                            String cookie = driver.manage().getCookieNamed("JSESSIONID").getValue();
                            File file = downloadImageOrNull(product.getImageSrc(), cookie);

                            if (file != null) {
                                String fileName = getGebeneProductKey(product) + ".jpg";
                                String uploadUrl = s3UploaderService.uploadImageOrNull(file, fileName);
                                product.updateImageUrl(uploadUrl);
                            }
                        } else {
                            product.updateImageUrl(null);
                        }


                        discordBot.sendNewProductInfoCommon(
                                GNB_STONE_ISLAND_NEW_PRODUCT_CHANNEL,
                                product.makeDiscordMessageDescription(),
                                product.getProductLink(),
                                product.getImageSrc(),
                                Stream.of(product.getSku()).toArray(String[]::new)
                        );

                        findGebeneProductList.add(product);
                        stoneIslandKeySet.add(getGebeneProductKey(product));


                        productFileWriter.writeProductInfo(product.changeToProductFileInfo(GNB, NEW_PRODUCT));

                    } else {
                        log.error(GEBENE_LOG_PREFIX + "상품 중복 " + product);
                    }

                }
            }

            if (pageProductDataList.size() > 0) {
                gnbStoneIslandMap.clear();
                for (var product : pageProductDataList) {
                    if (gnbStoneIslandMap.containsKey(getGebeneProductKey(product))) {
                        continue;
                    }
                    stoneIslandKeySet.add(getGebeneProductKey(product));
                    gnbStoneIslandMap.put(getGebeneProductKey(product), product);
                }
            }

        }
        return findGebeneProductList;
    }

    public List<GebenegoziProduct> findDifferentAndAlarm(ChromeDriver driver, WebDriverWait wait, String[][] brandDataList) {
        List<GebenegoziProduct> findGebeneProductList = new ArrayList<>();

        HashSet<String> productKeySet = gebenegoziBrandHashData.getProductKeySet();

        for (int i = 0; i < brandDataList.length; i++) {
            String brand = brandDataList[i][0];
            String url = brandDataList[i][2];
            String category = brandDataList[i][1];
            String sex = brandDataList[i][3];

            Map<String, GebenegoziProduct> eachBrandHashMap = gebenegoziBrandHashData.getBrandHashMap(url);

            List<GebenegoziProduct> pageProductDataList = getPageProductDataOrNull(driver, wait, url, category, sex, brand);

            if (pageProductDataList == null) {
                continue;
            }

            //자동주문  Business Logic
            //검증 없이 전부 API 호출

            try {
                Set<String> alreadySent = new HashSet<>();  // 한번만 생성해서 루프 전체에서 활용
                int batchSize = 10;

                for (int j = 0; j < pageProductDataList.size(); j += batchSize) {
                    int end = Math.min(j + batchSize, pageProductDataList.size());
                    List<GebenegoziProduct> batch = pageProductDataList.subList(j, end);

                    // 중복 걸러내고, 이미 보낸 건은 건너뛰기
                    List<GebenegoziProduct> toSend = batch.stream()
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
            } catch (Exception e) {
                log.error(GEBENE_LOG_PREFIX + "자동주문 버그 MSG: " + e.getMessage());
            }


            //신상품 확인 Business Logic
            for (GebenegoziProduct product : pageProductDataList) {
                if (!eachBrandHashMap.containsKey(getGebeneProductKey(product))) {
                    if (!productKeySet.contains(getGebeneProductKey(product))) {
                        log.info(GEBENE_LOG_PREFIX + "새로운 제품" + product);

                        //이미지 다운로드 내 S3 대입 상태확인
                        if (s3UploaderService.isAllowedUpload()) {
                            String cookie = driver.manage().getCookieNamed("JSESSIONID").getValue();
                            File file = downloadImageOrNull(product.getImageSrc(), cookie);

                            if (file != null) {
                                String fileName = getGebeneProductKey(product) + ".jpg";
                                String uploadUrl = s3UploaderService.uploadImageOrNull(file, fileName);
                                product.updateImageUrl(uploadUrl);
                            }
                        } else {
                            product.updateImageUrl(null);
                        }


                        discordBot.sendNewProductInfoCommon(
                                GEBENE_NEW_PRODUCT_CHANNEL,
                                product.makeDiscordMessageDescription(),
                                product.getProductLink(),
                                product.getImageSrc(),
                                Stream.of(product.getSku()).toArray(String[]::new)
                        );

                        findGebeneProductList.add(product);
                        productKeySet.add(getGebeneProductKey(product));


                        productFileWriter.writeProductInfo(product.changeToProductFileInfo(GNB, NEW_PRODUCT));

                    } else {
                        log.error(GEBENE_LOG_PREFIX + "상품 중복 " + product);
                    }

                }
            }

            if (pageProductDataList.size() > 0) {
                eachBrandHashMap.clear();
                for (var product : pageProductDataList) {
                    if (eachBrandHashMap.containsKey(getGebeneProductKey(product))) {
                        continue;
                    }
                    productKeySet.add(getGebeneProductKey(product));
                    eachBrandHashMap.put(getGebeneProductKey(product), product);
                }
            }

        }
        return findGebeneProductList;
    }


    @Override
    public void login(ChromeDriver driver, WebDriverWait wait) {
        //로그인페이지 로그인
        driver.get(GEBENE_MAIN_URL);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("login error");
        }


        WebElement loginInput = driver.findElement(By.id("username"));
        loginInput.sendKeys(userId);
        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys(userPw);
        WebElement submitButton = driver.findElement(By.id("doLogin"));

        submitButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("containerLineeModal")));

    }

    public void loadData(ChromeDriver driver, WebDriverWait wait, String[][] urlList) {
        //데이터 정보조회
        for (String[] data : urlList) {
            String brand = data[0];
            String url = data[2];
            String category = data[1];
            String sex = data[3];


            Map<String, GebenegoziProduct> eachBrandHashMap = gebenegoziBrandHashData.getBrandHashMap(url);
            HashSet<String> productKeySet = gebenegoziBrandHashData.getProductKeySet();

            List<GebenegoziProduct> pageProductDataList = new ArrayList<>();

            int count = 0;
            do {
                pageProductDataList = getPageProductDataOrNull(driver, wait, url, category, sex, brand);
                if (count > 2) {
                    log.error(GEBENE_LOG_PREFIX + "GNB Load Data 3번 이상 돌아도, 제대로 된 상품이 안나옴. URL : {}", url);
                    break;
                }
                count++;
            }
            while (pageProductDataList == null || pageProductDataList.isEmpty());


            if (pageProductDataList != null && !pageProductDataList.isEmpty()) {

                for (var product : pageProductDataList) {
                    if (eachBrandHashMap.containsKey(getGebeneProductKey(product))) {
                        log.info(GEBENE_LOG_PREFIX + "id 중복 " + product);
                        continue;
                    }
                    eachBrandHashMap.put(getGebeneProductKey(product), product);
                    productKeySet.add(getGebeneProductKey(product));
                }

            }

        }

    }

    public List<GebenegoziProduct> getPageProductDataOrNull(ChromeDriver driver, WebDriverWait wait, String url, String category, String sex, String searchBrand) {

        List<GebenegoziProduct> pageProductList = new ArrayList<>();
        String pattern = "\\S";
        Pattern p = Pattern.compile(pattern);

        //해당 브랜드 사전 정보 캐치
        driver.get(url);

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='card-body']")));
        } catch (Exception e) {
            log.error(GEBENE_LOG_PREFIX + "logout Redirection  or FIND PRODUCT ERROR");
            login(driver, wait);
            return null;
        }


        boolean isValidPage = false;
        isValidPage = isValidPage(driver, wait, p, url);
        if (!isValidPage) {
            log.error("**확인요망** 페이지 오류 확인 url" + url);
            return null;
        }

        //finalPage 찾기.
        WebElement pageElement = driver.findElement(By.xpath("//a[@class='page-link']"));
        int finalPage = Integer.parseInt(pageElement.getText().split("of")[1].strip());


        int brandRetryCount = 0;
        for (int j = 1; j <= finalPage; j++) {
            driver.get(url);

            isValidPage = false;
            isValidPage = isValidPage(driver, wait, p, url);

            if (!isValidPage) {
                log.error("**확인요망** 페이지 오류 확인 url" + url);
                return null;
            }

            List<WebElement> elements = driver.findElements(By.xpath("//div[@class='shopping-cart mb-3']"));

            List<GebenegoziProduct> pageUrlProduct = new ArrayList<>();
            boolean isValidBrand = true;


            for (WebElement productElement : elements) {

                try {

                    //상품정보
                    WebElement infoElement = productElement.findElement(By.xpath(".//div[@class='row title font-italic text-capitalize artPrezzi']"));
                    List<WebElement> dataList = infoElement.findElements(By.xpath(".//div[@class='col-5']"));
                    List<WebElement> madeByList = infoElement.findElements(By.xpath(".//div[@class='col-3']"));
                    WebElement priceInfo = infoElement.findElement(By.xpath(".//div[@class='col-3']//span"));
                    List<WebElement> wholeSaleInfoList = infoElement.findElements(By.xpath(".//div[@class='col-3']"));

                    String id = null;
                    String wholeSaleOrigin = "0.0";
                    String wholeSale = "0.0";
                    int wholeSalePercent = 0;
                    String brand = dataList.get(0).getText();
                    String sku = dataList.get(1).getText();
                    String season = dataList.get(2).getText();
                    String finalPrice = priceInfo.getText();
                    String madeBy = madeByList.get(2).getText();
                    double doubleFinalPrice = 0;
                    boolean isColored = false;


                    try {
                        WebElement idInfo = productElement.findElement(By.xpath(".//div[@class='artCod']"));
                        id = idInfo.getAttribute("id");
                    } catch (Exception e) {
                        log.error(GEBENE_LOG_PREFIX + "id 검색 오류 url=" + url);
                        continue;
                    }


                    //page 버그 수정..
                    pageElement = driver.findElement(By.xpath("//a[@class='page-link']"));
                    int tempPage = Integer.parseInt(pageElement.getText().split("of")[1].strip());

                    if (tempPage > finalPage) {
                        finalPage = tempPage;
                        log.info(GEBENE_LOG_PREFIX + " final page 오류로 인해 변경 로그 확인");
                    }
                    try {

                        for (int i = 0; i < wholeSaleInfoList.size(); i++) {
                            if (wholeSaleInfoList.get(i).getText().toLowerCase().contains("whole")) {
                                wholeSaleOrigin = wholeSaleInfoList.get(i).getText().split("€")[1].strip();
                                String key = gebenegoziBrandHashData.makeSalesInfoKey(brand, season.toUpperCase(), category, sex);
                                GebenegoziSaleInfo gebenegoziSaleInfoOrNull = gebenegoziBrandHashData.getGebenegoziSaleMap().getOrDefault(key, null);
                                double wholeSaleAfter = 0;
                                if (gebenegoziSaleInfoOrNull != null) {
                                    isColored = gebenegoziSaleInfoOrNull.isColored();
                                    wholeSalePercent = gebenegoziSaleInfoOrNull.getSalesPercent();
                                    wholeSaleAfter = Double.parseDouble(wholeSaleOrigin) * (wholeSalePercent + 100) / 100;
                                    wholeSale = String.valueOf(wholeSaleAfter);
                                }

                                doubleFinalPrice = Double.parseDouble(wholeSale) != 0 ? Double.parseDouble(wholeSale) : Double.parseDouble(wholeSaleOrigin);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(GEBENE_LOG_PREFIX + "whole Sale 검색 오류 url = " + url);
                    }


                    //이미지 정보
                    String imageSrc = null;
//                    try {
//                        imageSrc = findImageSrc(driver, wait, productElement);
//                    } catch (Exception e) {
//                        log.error(GEBENE_LOG_PREFIX + "이미지 경로 찾기 실패 sku" + sku + " url" + url);
//                    }


                    GebenegoziProduct product = GebenegoziProduct.builder()
                            .brandName(brand)
                            .sku(sku)
                            .season(season)
                            .madeBy(madeBy)
                            .price(finalPrice)
                            .productLink(url)
                            .imageSrc(imageSrc)
                            .category(category)
                            .salePercent(wholeSalePercent)
                            .finalPrice(wholeSale)
                            .originPrice(wholeSaleOrigin)
                            .id(id)
                            .isColored(isColored)
                            .monitoringSite(GNB)
                            .boutique(GNB)
                            .doublePrice(doubleFinalPrice)
                            .build();

                    //TODO 고민해보자
//                    gebenegoziBrandHashData.getProductKeySet().add(getGebeneProductKey(product));
                    if (brand == null || !brand.equals(searchBrand)) {
                        log.error("{} 브랜드 일치하지 않음 URL : {}, SEARCH BRAND : {}, URL PRODUCT BRAND : {}", GEBENE_LOG_PREFIX, url, searchBrand, brand);
                        isValidBrand = false;
                        brandRetryCount++;
                        if (brandRetryCount > 3) {
                            discordBot.sendMessage(GEBENE_NEW_PRODUCT_CHANNEL,String.format("%s 브랜드 일치하지 않음 URL : %s, SEARCH BRAND : %s, URL PRODUCT BRAND : %s", GEBENE_LOG_PREFIX, url, searchBrand, brand));
                            return new ArrayList<>();
                        }
                        Thread.sleep(500);
                        break;
                    }
                    pageUrlProduct.add(product);


                    //log.info("id = {},\t sku = {}\t \t brand = {}\t  season = {}\t finalPrice ={}\t madeBy = {}\t product link = {}", id, sku, brand, season, finalPrice, madeBy, url);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("상품 정보 조회 오류 url =" + url);
                }

            }


            if (!isValidBrand) {
                log.error("브랜드 일치하지 않아서 재탐색 합니다. url : {}", url);
                j--;
                continue;
            }

            pageProductList.addAll(pageUrlProduct);

            //url 변경
            url = url.replace("n=" + j, "n=" + (j + 1));
        }

        return pageProductList;
    }


    private boolean isValidPage(ChromeDriver driver, WebDriverWait wait, Pattern p, String url) {
        boolean isValidPage = false;
        for (int k = 0; k < 5; k++) {
            try {
                wait.until(ExpectedConditions.textMatches(By.xpath("//div[@class='row title font-italic text-capitalize artPrezzi']//div[@class='col-5']"), p));
                isValidPage = true;
                break;
            } catch (Exception e) {
                //페이지 오류땜에..
                driver.get(url);
                if (k == 2) {
                    break;
                }
            }
        }
        return isValidPage;
    }

    private String findImageSrc(ChromeDriver driver, WebDriverWait wait, WebElement productElement) {

        WebElement imageElement = productElement.findElement(By.xpath(".//img[@class='zoom lozad']"));
        Actions actions = new Actions(driver);
        actions.moveToElement(productElement);
        actions.perform();

        wait.until(ExpectedConditions.attributeToBeNotEmpty(imageElement, "src"));
        imageElement = productElement.findElement(By.xpath(".//img[@class='zoom lozad']"));
        String imageSrc = imageElement.getAttribute("src");


        return imageSrc;
    }

    public File downloadImageOrNull(String imageSrcUrl, String cookie) {

        assert (imageSrcUrl != null);
        assert (cookie != null);

        HttpHeaders headers = new HttpHeaders();
        headers.add(COOKIE, SESSION_COOKIE_KEY + "=" + cookie);

        // 이미지를 바이트 배열로 다운로드
        String fileName = getFileNameByTimeFormat();
        File file = new File(fileName);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(imageSrcUrl, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
            byte[] imageData = response.getBody();
            assert (imageData != null);
            Path path = file.toPath();
            // 이미지 데이터를 파일로 저장
            Files.write(path, imageData);
        } catch (Exception e) {

            log.error(GEBENE_LOG_PREFIX + "이미지 다운로드 실패 : url =  " + imageSrcUrl + " cookie = " + cookie + "message : " + e.getMessage());
            return null;
        }

        return file;
        //내 서버에 이미지 올립니다.


    }


    public static String getFileNameByTimeFormat() {
        // 현재 날짜와 시간 가져오기
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");
        return currentDateTime.format(formatter) + ".jpg";
    }

    private String[] getUrlList() {
        List<String> collect = Arrays.stream(GEBE_URL_LIST).map(v -> v[1]).toList();
        return collect.toArray(new String[collect.size()]);
    }

    private String getGebeneProductKey(GebenegoziProduct product) {
        return product.getId() + product.getSku();
    }

}

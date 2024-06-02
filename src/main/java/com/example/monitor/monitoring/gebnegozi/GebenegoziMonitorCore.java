package com.example.monitor.monitoring.gebnegozi;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.file.ProductFileWriter;
import com.example.monitor.infra.converter.controller.IConverterFacade;
import com.example.monitor.infra.discord.DiscordBot;
import com.example.monitor.infra.s3.S3UploaderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

import static com.example.monitor.infra.discord.DiscordString.GEBENE_NEW_PRODUCT_CHANNEL;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;
import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class GebenegoziMonitorCore {


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


    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();


        login(driver, wait);

        loadData(driver, wait, GEBE_URL_LIST);

        chromeDriverTool.isLoadData(true);
    }


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

        //검색 모듈로 전달은 나중에..
//        if (!gebenegoziProductList.isEmpty()) {
//            List<ConvertProduct> convertProductList = doubleFProductList.stream()
//                    .map(v -> v.changeToConvertProduct(DOUBLE_F))
//                    .collect(Collectors.toList());
//            iConverterFacade.convertProduct(convertProductList);
//            iConverterFacade.sendToSearchServer(convertProductList);
//        }

    }

    public List<GebenegoziProduct> findDifferentAndAlarm(ChromeDriver driver, WebDriverWait wait, String[][] brandDataList) {
        List<GebenegoziProduct> findGebeneProductList = new ArrayList<>();

        HashSet<String> productKeySet = gebenegoziBrandHashData.getProductKeySet();

        for (int i = 0; i < brandDataList.length; i++) {
            String url = brandDataList[i][2];
            String category = brandDataList[i][1];
            String sex = brandDataList[i][3];

            Map<String, GebenegoziProduct> eachBrandHashMap = gebenegoziBrandHashData.getBrandHashMap(url);

            List<GebenegoziProduct> pageProductDataList = getPageProductDataOrNull(driver, wait, url, category, sex);

            if (pageProductDataList == null) {
                continue;
            }


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

                        discordBot.sendNewProductInfo(GEBENE_NEW_PRODUCT_CHANNEL, product);
                        findGebeneProductList.add(product);
                        productKeySet.add(getGebeneProductKey(product));

                        productFileWriter.writeProductInfo(product.changeToProductFileInfo(GEBE, NEW_PRODUCT));

                    } else {
                        log.error(GEBENE_LOG_PREFIX + "상품 중복 " + product);
                    }

                }
            }

            if (pageProductDataList.size() > 0) {
                eachBrandHashMap.clear();
                for (var product : pageProductDataList) {
                    if (eachBrandHashMap.containsKey(getGebeneProductKey(product))) {
                        // log.info(GEBENE_LOG_PREFIX + "id 중복 " + product);
                        continue;
                    }
                    productKeySet.add(getGebeneProductKey(product));
                    eachBrandHashMap.put(getGebeneProductKey(product), product);
                }
            }

        }
        return findGebeneProductList;
    }


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

            List<GebenegoziProduct> pageProductDataList = getPageProductDataOrNull(driver, wait, url, category, sex);

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

    public List<GebenegoziProduct> getPageProductDataOrNull(ChromeDriver driver, WebDriverWait wait, String url, String category, String sex) {

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

        for (int j = 1; j <= finalPage; j++) {
            driver.get(url);

            isValidPage = false;
            isValidPage = isValidPage(driver, wait, p, url);
            if (!isValidPage) {
                log.error("**확인요망** 페이지 오류 확인 url" + url);
                return null;
            }

            List<WebElement> elements = driver.findElements(By.xpath("//div[@class='shopping-cart mb-3']"));

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
                                String key = gebenegoziBrandHashData.makeSalesInfoKey(brand, season.toUpperCase(), category, "man");
                                GebenegoziSaleInfo gebenegoziSaleInfoOrNull = gebenegoziBrandHashData.getGebenegoziSaleMap().getOrDefault(key, null);

                                if (gebenegoziSaleInfoOrNull != null) {
                                    isColored = gebenegoziSaleInfoOrNull.isColored();
                                    wholeSalePercent = gebenegoziSaleInfoOrNull.getSalesPercent();
                                    double wholeSaleAfter = Double.parseDouble(wholeSaleOrigin) * (wholeSalePercent + 100) / 100;
                                    wholeSale = String.valueOf(wholeSaleAfter);
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(GEBENE_LOG_PREFIX + "whole Sale 검색 오류 url = " + url);
                    }


                    //이미지 정보
                    String imageSrc = null;
                    try {
                        imageSrc = findImageSrc(driver, wait, productElement);
                    } catch (Exception e) {
                        log.error(GEBENE_LOG_PREFIX + "이미지 경로 찾기 실패 sku" + sku + " url" + url);
                    }

                    GebenegoziProduct product = GebenegoziProduct.builder()
                            .brandName(brand)
                            .sku(sku)
                            .season(season)
                            .madeBy(madeBy)
                            .price(finalPrice)
                            .productLink(url)
                            .imageSrc(imageSrc)
                            .category(category)
                            .wholeSalePercent(wholeSalePercent)
                            .wholeSale(wholeSale)
                            .wholeSaleOrigin(wholeSaleOrigin)
                            .id(id)
                            .isColored(isColored)
                            .build();

                    pageProductList.add(product);
                    //log.info("id = {},\t sku = {}\t \t brand = {}\t  season = {}\t finalPrice ={}\t madeBy = {}\t product link = {}", id, sku, brand, season, finalPrice, madeBy, url);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("상품 정보 조회 오류 url =" + url);
                }

            }
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
                if (k == 4) {
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
            e.printStackTrace();
            log.error(GEBENE_LOG_PREFIX + "이미지 다운로드 실패");
            return null;
        }

        return file;
        //내 서버에 이미지 올립니다.


    }

    @NotNull
    public static String getFileNameByTimeFormat() {
        // 현재 날짜와 시간 가져오기
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss.SSS");
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

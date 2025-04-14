package com.example.monitor.monitoring.eic;


import chrome.ChromeDriverTool;
import com.example.monitor.monitoring.global.IMonitorService;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DOUBLE_F_LOG_PREFIX;
import static com.example.monitor.monitoring.eic.EicFindString.EIC_LOG_PREFIX;
import static module.discord.DiscordString.EIC_DISCOUNT_CHANNEL;
import static module.discord.DiscordString.EIC_NEW_PRODUCT_CHANNEL;

@Slf4j
@Component
@RequiredArgsConstructor
public class EicMonitorCore implements IMonitorService {

    private final DiscordBot discordBot;


    @Value("${eic.user.id}")
    private String userId;

    @Value("${eic.user.pw}")
    private String userPw;

    @Getter
    private final EicBrandHashData eicBrandHashData;

    @Override
    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        login(driver, wait);
        loadData(driver, wait, EicFindString.brandNameList);
        chromeDriverTool.isLoadData(true);
    }

    @Override
    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();


        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(EIC_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }

        log.info(EIC_LOG_PREFIX + "EIC FIND NEW PRODUCT START==");
        findDifferentAndAlarm(chromeDriver, wait, EicFindString.brandNameList);

        log.info(EIC_LOG_PREFIX + "EIC FIND NEW PRODUCT FINISH ==");

    }

    public List<EicProduct> findDifferentAndAlarm(ChromeDriver driver, WebDriverWait wait, String[] brandNameList) {

        List<EicProduct> findNewProduct = new ArrayList<>();
        for (int i = 0; i < brandNameList.length; i++) {
            //페이지 URL 만들기
            String brandName = brandNameList[i];
            String url = makeBrandUrl(brandName);

            List<EicProduct> pageProductData = getPageProductData(driver, wait, url, brandName);

            //상품 정보 존재할 경우
            Map<String, EicProduct> eachBrandHashMap = eicBrandHashData.getBrandHashMap(brandName);
            HashSet<String> productKeySet = eicBrandHashData.getProductKeySet();

            for (EicProduct product : pageProductData) {

                if (!eachBrandHashMap.containsKey(getProductKey(product))) {

                    if (!productKeySet.contains(getProductKey(product))) {
                        log.info(EIC_LOG_PREFIX + "새로운 제품" + product);
                        getDetailProductInfo(driver, wait, product);

                        discordBot.sendNewProductInfoCommon(
                                EIC_NEW_PRODUCT_CHANNEL,
                                product.makeDiscordMessageDescription(),
                                product.getProductLink(),
                                product.getImageSrc(),
                                Stream.of(product.getSku(), product.getColorCode()).toArray(String[]::new)
                        );

                        findNewProduct.add(product);
                        //보낸 상품 체크
                        productKeySet.add(getProductKey(product));
                    } else {
                        log.error(EIC_LOG_PREFIX + "상품 중복 " + product);
                    }
                } else {
                    //포함 되어있고,할인 퍼센테이지가 다를 경우
                    EicProduct beforeProduct = eachBrandHashMap.get(getProductKey(product));

                    if (!beforeProduct.getDiscountPercentage().equals(product.getDiscountPercentage())) {
                        log.info(EIC_LOG_PREFIX + "할인율 변경" + beforeProduct.getDiscountPercentage() + " -> " + product.getDiscountPercentage() + "상품내역 : " + product);
                        getDetailProductInfo(driver, wait, product);

                        discordBot.sendDiscountChangeInfoCommon(
                                EIC_DISCOUNT_CHANNEL,
                                product.makeDiscordDiscountMessageDescription(beforeProduct.getDiscountPercentage()),
                                product.getProductLink(),
                                product.getImageSrc(),
                                Stream.of(product.getSku(), product.getColorCode()).toArray(String[]::new)
                        );


                        findNewProduct.add(product);
                    }
                }

            }

            //검사 후 현재 상태로 기존 데이터 변경
            if (pageProductData.size() > 0) {
                eachBrandHashMap.clear();
                for (EicProduct product : pageProductData) {
                    eachBrandHashMap.put(getProductKey(product), product);
                }
            }
        }

        return findNewProduct;
    }

    public void getDetailProductInfo(ChromeDriver driver, WebDriverWait wait, EicProduct product) {
        driver.get(product.getProductLink());
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='infoProdDet']//span[@class='price']")));

        try {
            WebElement colorCodeElement = driver.findElement(By.xpath("//table[@class='tableWS']//td"));
            String colorCode = colorCodeElement.getText();
            colorCode = colorCode.split(":")[1].trim();
            product.setColorCode(colorCode);
        } catch (Exception e) {
            log.error(EIC_LOG_PREFIX + " Color Code ERROR url = " + driver.getCurrentUrl());
        }


    }

    @Override
    public void login(ChromeDriver driver, WebDriverWait wait) {



        driver.get("https://www.giglio.com/eng/my-account/logon.html");


        //accept cookie
        {
            try{
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='sc-jPkiSJ eVcJuY']")));
                Thread.sleep(1000);
                WebElement element = driver.findElement(By.xpath("//button[@class='sc-dcKlJK iQJAOG sc-fjUQFl hycjKk']"));
                element.click();
                Thread.sleep(3000);
            }catch (Exception e) {
                log.error(EIC_LOG_PREFIX+ " cookie accept error");
            }
        }

        WebElement idElement = driver.findElement(By.xpath("//form[@id='login-form']//input[@class='form__input ' and @name='email']"));
        WebElement pwElement = driver.findElement(By.xpath("//form[@id='login-form']//input[@class='form__input ' and @name='password']"));

        idElement.sendKeys(userId);
        pwElement.sendKeys(userPw);

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebElement loginButton = driver.findElement(By.xpath("//button[@class='button button--primary' and contains(text(), ' Log in and start shopping')]"));
        loginButton.click();

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadData(ChromeDriver driver, WebDriverWait wait, String[] eicBrandNameList) {

        for (int i = 0; i < eicBrandNameList.length; i++) {
            String brandName = eicBrandNameList[i];
            String url = makeBrandUrl(brandName);
            List<EicProduct> pageProductData = getPageProductData(driver, wait, url, brandName);


            //상품 정보 존재할 경우
            Map<String, EicProduct> eachBrandHashMap = eicBrandHashData.getBrandHashMap(brandName);
            for (EicProduct product : pageProductData) {
                eachBrandHashMap.putIfAbsent(getProductKey(product), product);
            }
        }


    }


    public List<EicProduct> getPageProductData(ChromeDriver driver, WebDriverWait wait, String url, String brandName) {

        //페이지로 이동
        driver.get(url);

        List<EicProduct> eicProductList = new ArrayList<>();
        Map<String, EicProduct> eicProductMap = new HashMap<>();

        for (int i = 1; i < 50; i++) {
            driver.get(url + "/pag-" + i);
            if (i != 1 && driver.getCurrentUrl().equals(url)) {
                log.info(driver.getCurrentUrl() + " and Final Page = " + (i - 1));
                if (i >= 40) {
                    discordBot.sendMessage(EIC_NEW_PRODUCT_CHANNEL, "url page 가 40쪽이 넘었습니다. 유효한지 확인해주세요 " + url);
                }
                break;
            }

            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//article[@class='boxArt']")));
            } catch (Exception e) {
                log.error(EIC_LOG_PREFIX + "logout Redirection  or FIND PRODUCT ERROR");
                try {
                    login(driver, wait);
                } catch (Exception e2) {
                    log.error(EIC_LOG_PREFIX + "loginError -> find product");
                    driver.get(url + "/pag-" + i);
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//article[@class='boxArt']")));
                }
            }


            List<WebElement> elements = driver.findElements(By.xpath("//article[@class='boxArt']"));


            String salesPercent = "";
            String sku = "";
            String productID = "";
            String variant = "";
            String imageSrc = "";
            String price = "";
            String productName = "";
            String productLink = "";

            for (WebElement product : elements) {

                try {
                    salesPercent = product.findElement(By.xpath(".//small[@class='priceBox']//q")).getText();
                } catch (Exception e) {
                    log.debug("sale 정보 없음");
                }

                try {
                    sku = product.findElement(By.xpath(".//meta[@itemprop='sku']")).getAttribute("content");
                    productID = product.findElement(By.xpath(".//meta[@itemprop='productID']")).getAttribute("content");
                    variant = product.findElement(By.xpath(".//meta[@itemprop='variant']")).getAttribute("content");
                } catch (Exception e) {
                    log.error(EIC_LOG_PREFIX + "** 확인요망 ** url =" + url + "/pag-" + i + " 상품중 상품ID 가 없습니다. 누락됩니다.");
                    continue;
                }

                try {
                    price = product.findElement(By.xpath(".//small[@class='priceBox']")).getText();
                    String[] split = price.split("€");
                    price = split[split.length - 1];
                } catch (Exception e) {
                    log.debug("가격 정보 없음");
                }

                try {
                    imageSrc = product.findElement(By.xpath(".//img")).getAttribute("src");
                    if ("https://img.giglio.com/images/prodPage/noImage.jpg".equals(imageSrc)) {
                        log.debug(EIC_LOG_PREFIX + "이미지없어서 넘어갑니다. url =" + driver.getCurrentUrl() + " AND sku = " + sku);
                        continue;
                    }
                } catch (Exception e) {
                    log.debug("이미지 정보 없음");
                }

                try {
                    WebElement productDetail = product.findElement(By.xpath(".//h3//a"));
                    productName = productDetail.getText();
                    productLink = productDetail.getAttribute("href");
                } catch (Exception e) {
                    log.info("상품정보 없음 + url=" + url + "/pag-" + i);
                }

                if (eicProductMap.containsKey(sku + productID + productLink + imageSrc)) {
                    log.debug("상품 중복 입니다. url = " + driver.getCurrentUrl() + " And  sku + product +ID +productLink() = " + (sku + " " + productID + " " + productLink));
                    continue;
                }


                EicProduct eicProduct = EicProduct.builder()
                        .id(productID)
                        .price(price)
                        .sku(sku)
                        .discountPercentage(salesPercent)
                        .name(productName)
                        .productLink(productLink)
                        .imageSrc(imageSrc)
                        .colorCode(variant)
                        .brandName(brandName)
                        .build();

                //log.info(eicProduct.toString());
                eicProductMap.put(sku + productID + productLink + imageSrc, eicProduct);
                eicProductList.add(eicProduct);
            }

            if (elements.size() < 48) {
                log.info(driver.getCurrentUrl() + " and Final Page = " + (i - 1));
                break;
            }
        }

        return eicProductList;
    }

    public String makeBrandUrl(String brandName) {

        return "https://eic.giglio.com/eng/" + brandName + "/";

    }

    private String getProductKey(EicProduct product) {
        return product.getSku() + product.getId() + product.getProductLink() + product.getImageSrc();
    }
}

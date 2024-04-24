package com.example.monitor.monitoring.dobulef;


import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.infra.discord.DiscordBot;
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


import static com.example.monitor.infra.discord.DiscordString.DOUBLE_F_DISCOUNT_CHANNEL;
import static com.example.monitor.infra.discord.DiscordString.DOUBLE_F_NEW_PRODUCT_CHANNEL;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;

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
        loadData(driver, wait, womanBrandNameList, WOMANS_PREFIX);
        loadData(driver, wait, manBrandNameList, MANS_PREFIX);

        chromeDriverTool.isLoadData(true);

    }

    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(DOUBLE_F_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }
        log.info(DOUBLE_F_LOG_PREFIX + "DOUBLE_F FIND NEW PRODUCT START==");
        findDifferentAndAlarm(chromeDriver, wait, womanBrandNameList, WOMANS_PREFIX);
        findDifferentAndAlarm(chromeDriver, wait, manBrandNameList, MANS_PREFIX);
        log.info(DOUBLE_F_LOG_PREFIX + "DOUBLE_F FIND NEW PRODUCT FINISH ==");
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

    private void findDifferentAndAlarm(ChromeDriver driver, WebDriverWait wait, String[] brandNameList, String sexPrefix) {
        for (int i = 0; i < brandNameList.length; i++) {

            //페이지 URL 만들기
            String brandName = brandNameList[i];
            String url = makeBrandUrl(brandName, sexPrefix);

            List<DoubleFProduct> pageProductData = getPageProductData(driver, wait, url, brandName);

            //상품 정보 존재할 경우
            Map<String, DoubleFProduct> eachBrandHashMap = doubleFBrandHashMap.getBrandHashMap(sexPrefix, brandName);

            for (DoubleFProduct product : pageProductData) {
                if (!eachBrandHashMap.containsKey(product.getId())) {
                    //새로운 재품일 경우
                    log.info(DOUBLE_F_LOG_PREFIX + "새로운 제품" + product);
                    getDetailProductInfo(driver, wait, product);
                    discordBot.sendNewProductInfo(DOUBLE_F_NEW_PRODUCT_CHANNEL, product, url);
                } else {
                    //포함 되어있고,할인 퍼센테이지가 다를 경우
                    DoubleFProduct beforeProduct = eachBrandHashMap.get(product.getId());
                    if (!beforeProduct.getDiscountPercentage().equals(product.getDiscountPercentage())) {
                        log.info(DOUBLE_F_LOG_PREFIX + "할인율 변경" + beforeProduct.getDiscountPercentage() + " -> " + product.getDiscountPercentage());
                        getDetailProductInfo(driver, wait, product);
                        discordBot.sendDiscountChangeInfo(DOUBLE_F_DISCOUNT_CHANNEL, product, url, beforeProduct.getDiscountPercentage());
                    }
                }
            }

            //검사 후 현재 상태로 기존 데이터 변경
            if (pageProductData.size() > 0) {
                eachBrandHashMap.clear();
                for (DoubleFProduct product : pageProductData) {
                    eachBrandHashMap.put(product.getId(), product);
                }
            }

        }

    }


    public void loadData(ChromeDriver driver, WebDriverWait wait, String[] brandNameList, String sexPrefix) {

        for (int i = 0; i < brandNameList.length; i++) {

            //페이지 URL 만들기
            String brandName = brandNameList[i];
            String url = makeBrandUrl(brandName, sexPrefix);

            List<DoubleFProduct> pageProductData = getPageProductData(driver, wait, url, brandName);

            //상품 정보 존재할 경우
            Map<String, DoubleFProduct> eachBrandHashMap = doubleFBrandHashMap.getBrandHashMap(sexPrefix, brandName);
            for (DoubleFProduct product : pageProductData) {
                eachBrandHashMap.put(product.getId(), product);
            }

        }
    }

    public List<DoubleFProduct> getPageProductData(ChromeDriver driver, WebDriverWait wait, String url, String brandName) {

        //페이지로 이동
        driver.get(url);

        //상품 상위 태그
        List<WebElement> productList = new ArrayList<>();
        List<DoubleFProduct> pageProductList = new ArrayList<>();
        try {
            WebElement topDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(TOP_DIV_XPATH)));
            productList = topDiv.findElements(By.xpath(CHILD_PRODUCT_DIV));
        } catch (Exception e) {
            log.error(DOUBLE_F_LOG_PREFIX + "logout Redirection  or FIND PRODUCT ERROR");
            login(driver, wait);
            return pageProductList;
        }


        //상품 정보 로드
        for (WebElement product : productList) {
            String productName = "상품이름 정보 없습니다.";
            String productDiscountPercentage = "0%";
            String productPrice = "";
            String productId = "";
            String productLink = "";
            String productSkU = "";
            String productColorCode = "";

            //상품 id
            try {
                WebElement productIdElement = product.findElement(By.xpath(".//div[@class='price-box price-final_price']"));
                productId = productIdElement.getAttribute("data-price-box");
            } catch (Exception e) {
                log.error(DOUBLE_F_LOG_PREFIX + "** 확인요망 ** url =" + url + " 상품중 상품ID 가 없습니다. 누락됩니다.");
                log.error(DOUBLE_F_LOG_PREFIX + "상세정보 총 상품개수 " + productList.size() + "에러 index" + productList.indexOf(product));
                continue;
            }
            // 상품이름 정보
            try {
                WebElement productNameElement = product.findElement(By.xpath(PRODUCT_NAME_XPATH));
                productName = productNameElement.getText();
            } catch (Exception e) {
                log.error(DOUBLE_F_LOG_PREFIX + "** 확인요망 **" + brandName + "의 상품에 이름이 없습니다. 홈페이지 및 프로그램 확인 바랍니다.");

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
                log.info(DOUBLE_F_LOG_PREFIX + productName + " 의 가격 정보가 없습니다.");
            }
            // 상품 링크 정보
            try {
                WebElement webElement = product.findElement(By.xpath(".//h4[@class='product-card__name truncate ... font-light text-xs tracking-1-08 leading-snug mb-5px']//a"));
                productLink = webElement.getAttribute("href");
                String tempLink = productLink;
                // https://www.thedoublef.com/bu_en/light-blue-denim-over-shirt-acne-cb0070co-o-acne-228/

                tempLink = tempLink.replaceAll("/", "");

                String[] splitData = tempLink.split("-");
                if (splitData.length >= 4) {
                    productSkU = splitData[splitData.length - 4];
                    productColorCode = splitData[splitData.length - 1];
                }


            } catch (Exception e) {
                log.info(DOUBLE_F_LOG_PREFIX + productName + " 의 링크 정보가 없습니다.");
            }

            DoubleFProduct doubleFProduct = DoubleFProduct.builder()
                    .id(productId)
                    .name(productName)
                    .brand(brandName)
                    .price(productPrice)
                    .discountPercentage(productDiscountPercentage)
                    .productLink(productLink)
                    .sku(productSkU)
                    .colorCode(productColorCode)
                    .build();


            pageProductList.add(doubleFProduct);
        }
        return pageProductList;
    }


    public void getDetailProductInfo(ChromeDriver driver, WebDriverWait wait, DoubleFProduct product) {

        boolean isGetData = false;

        driver.get(product.getProductLink());
        WebElement styleDetailsToggle = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='title pre-arrow-right text-md font-medium py-5-5']//span[text()='Composition and care']")));
        driver.executeScript("arguments[0].click();", styleDetailsToggle);
        //실패하면 최소 2번 해당 상품 데이터 조회 시도.
        try {
            for (int j = 1; j <= 2; j++) {
                Thread.sleep(1000);

                WebElement element = driver.findElement(By.xpath("//div[@class='content overflow-hidden border-b transition-all max-h-0 duration-500 text-sm leading-relaxed']"));
                List<WebElement> productDataList = element.findElements(By.xpath("./div"));

                for (WebElement productData : productDataList) {

                    if (!productData.getText().isEmpty()) {
                        if (productData.getText().contains("Made in")) {
                            String[] split = productData.getText().split(" ");
                            if (split.length >= 3) {
                                String madeBy = split[2];
                                product.updateMadeBy(madeBy);
                            }
                        }
                        isGetData = true;
                    } else {
                        log.info(DOUBLE_F_LOG_PREFIX + "**확인요망** get Data 원산지 error ");
                    }
                }
                if (isGetData) {
                    break;
                } else {
                    log.error(DOUBLE_F_LOG_PREFIX + " 데이터 상세 조회 실패 재시도 ");
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            log.error(DOUBLE_F_LOG_PREFIX + " 상세조회 에러 : " + "Product : + " + product.toString());
            e.printStackTrace();
        }
        if (!isGetData) {
            log.error(DOUBLE_F_LOG_PREFIX + "Product : + " + product.toString() + " 상세조회 에러");
        }

    }


    private String makeBrandUrl(String brandName, String sexPrefix) {
        return "https://www.thedoublef.com/bu_en/" + sexPrefix + "/designers/" + brandName + "/";
    }
}

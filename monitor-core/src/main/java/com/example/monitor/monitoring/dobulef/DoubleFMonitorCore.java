package com.example.monitor.monitoring.dobulef;


import chrome.ChromeDriverTool;
import com.example.monitor.file.ProductFileWriter;
import com.example.monitor.infra.converter.controller.IConverterFacade;
import com.example.monitor.infra.converter.dto.ConvertProduct;
import module.discord.DiscordBot;
import com.example.monitor.monitoring.global.IMonitorService;
import lombok.Getter;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static module.discord.DiscordString.DOUBLE_F_DISCOUNT_CHANNEL;
import static module.discord.DiscordString.DOUBLE_F_NEW_PRODUCT_CHANNEL;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DoubleFMonitorCore implements IMonitorService {


    private final DiscordBot discordBot;

    @Getter
    private final DoubleFBrandHashData doubleFBrandHashData;

    @Value("${doublef.user.id}")
    private String userId;

    @Value("${doublef.user.pw}")
    private String userPw;

    private final IConverterFacade iConverterFacade;

    private final ProductFileWriter productFileWriter;

    @Override
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

    @Override
    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        List<DoubleFProduct> doubleFProductList = new ArrayList<>();
        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(DOUBLE_F_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }
        log.info(DOUBLE_F_LOG_PREFIX + "DOUBLE_F FIND NEW PRODUCT START==");
        List<DoubleFProduct> womanDifferent = findDifferentAndAlarm(chromeDriver, wait, womanBrandNameList, WOMANS_PREFIX);
        List<DoubleFProduct> manDifferent = findDifferentAndAlarm(chromeDriver, wait, manBrandNameList, MANS_PREFIX);

        doubleFProductList.addAll(womanDifferent);
        doubleFProductList.addAll(manDifferent);
        log.info(DOUBLE_F_LOG_PREFIX + "DOUBLE_F FIND NEW PRODUCT FINISH ==");

        if (!doubleFProductList.isEmpty()) {
            List<ConvertProduct> convertProductList = doubleFProductList.stream()
                    .map(v -> v.changeToConvertProduct(DOUBLE_F))
                    .collect(Collectors.toList());
            iConverterFacade.convertProduct(convertProductList);
            iConverterFacade.sendToSearchServer(convertProductList);
        }

    }


    public void acceptCookie(WebDriverWait wait) {
        //쿠키 허용
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebElement cookieElement = wait.until(ExpectedConditions.presenceOfElementLocated(DoubleFLocator.DF_COOKIE.byId()));

        cookieElement.click();

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void login(ChromeDriver driver, WebDriverWait wait) {
        driver.get(DOUBLE_F_MAIN_PAGE);
        //로그인

        WebElement loginElement = wait.until(ExpectedConditions.presenceOfElementLocated(DoubleFLocator.DF_ID.byId()));

        loginElement.sendKeys(userId);

        WebElement pwElement = driver.findElement(DoubleFLocator.DF_PASS.byId());
        pwElement.sendKeys(userPw);

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }


        WebElement button = driver.findElement(By.xpath(DF_LOGIN_BUTTON_XPATH));
        button.click();

        //로그인 이후 5초 정지..
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadData(ChromeDriver driver, WebDriverWait wait, String[] brandNameList, String sexPrefix) {

        for (int i = 0; i < brandNameList.length; i++) {

            //페이지 URL 만들기
            String brandName = brandNameList[i];
            String url = makeBrandUrl(brandName, sexPrefix);

            List<DoubleFProduct> pageProductData = getPageProductData(driver, wait, url, brandName);

            //상품 정보 존재할 경우
            Map<String, DoubleFProduct> eachBrandHashMap = doubleFBrandHashData.getBrandHashMap(sexPrefix, brandName);
            for (DoubleFProduct product : pageProductData) {
                eachBrandHashMap.put(getDoubleFProductKey(product), product);
            }

        }
    }

    public List<DoubleFProduct> getPageProductData(ChromeDriver driver, WebDriverWait wait, String url, String brandName) {

        //페이지로 이동
        driver.get(url);

        //상품 상위 태그
        List<WebElement> productList = new ArrayList<>();
        List<DoubleFProduct> pageProductList = new ArrayList<>();

        int totalPages = 1;
        int productTotalNum = 48;
        try {

            WebElement pageElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='pb-3 text-4xs uppercase leading-normal tracking-widest']")));
            String text = pageElement.getText();
            int productNumPerPage = Integer.parseInt(text.split(" ")[1]);
            productTotalNum = Integer.parseInt(text.split(" ")[3]);
            totalPages = (int) (Math.ceil(((double) productTotalNum) / productNumPerPage));
        } catch (Exception e) {
            log.error(DOUBLE_F_LOG_PREFIX + "error get page number");
        }

        for (int i = 0; i < totalPages; i++) {

            if (i != 0) {
                driver.get(url + "?p=" + (i + 1));
            }
            try {
                WebElement topDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(TOP_DIV_XPATH)));
                productList = topDiv.findElements(By.xpath(CHILD_PRODUCT_DIV));
            } catch (Exception e) {
                log.error(DOUBLE_F_LOG_PREFIX + "logout Redirection  or FIND PRODUCT ERROR");
                try {
                    login(driver, wait);
                } catch (Exception e2) {
                    log.error(DOUBLE_F_LOG_PREFIX + "logout Redirection ERROR");
                }

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
                String extraDiscountPercentage = "0%";
                double productDoublePrice = 0;
                boolean extraSales = false;

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
                    //log.error(DOUBLE_F_LOG_PREFIX + "** 확인요망 **" + productDiscountPercentage + "의 상품에 할인율 없습니다. 홈페이지 및 프로그램 확인 바랍니다.");
                }

                //Extra 상품 할인 정보
                try {
                    WebElement extraDiscount = product.findElement(By.xpath(".//div[@class='product-card__extra-label text-center inline-block font-medium text-primary text-4xs tracking-0-12 uppercase border border-primary px-5px my-1 py-2px']"));
                    extraDiscountPercentage = extraDiscount.getText();
                    extraDiscountPercentage = extraDiscountPercentage.split(" ")[1];
                    extraSales = true;
                } catch (Exception e) {

                }

                // 상품 가격 정보
                try {
                    List<WebElement> productPriceElementList = product.findElements(By.xpath(PRODUCT_PRICE_XPATH));
                    for (WebElement productPriceElement : productPriceElementList) {
                        productPrice = productPrice + " " + productPriceElement.getText();
                    }
                    productPrice = productPrice.strip();
                    productDoublePrice = changePriceToDouble(productPrice);
                    if (extraSales) {
                        double extraSalesDouble = Double.parseDouble(extraDiscountPercentage.split("%")[0]);
                        productDoublePrice = productDoublePrice * (100 - extraSalesDouble) / 100;
                    }
                } catch (Exception e) {
                    log.info(DOUBLE_F_LOG_PREFIX + productName + " 의 가격 정보가 없습니다.");
                }
                // 상품 링크 정보
                try {
                    WebElement webElement = product.findElement(By.xpath(".//h2[@class='product-card__name truncate ... font-light text-xs tracking-1-08 leading-snug mb-5px']//a"));
                    productLink = webElement.getAttribute("href");
                    String tempLink = productLink;
                    // https://www.thedoublef.com/bu_en/light-blue-denim-over-shirt-acne-cb0070co-o-acne-228/

                    tempLink = tempLink.replaceAll("/", "");

                    String[] splitData = tempLink.split("-");
                    if (splitData.length >= 5) {
                        productSkU = splitData[splitData.length - 4];
                        if (productSkU.length() <= 3) {
                            productSkU = splitData[splitData.length - 5] + splitData[splitData.length - 4];
                        }
                        productColorCode = splitData[splitData.length - 1];
                    }

                } catch (Exception e) {
                    log.info(DOUBLE_F_LOG_PREFIX + productName + " 의 링크 정보가 없습니다.");
                }

                DoubleFProduct doubleFProduct = DoubleFProduct.builder()

                        .id(productId)
                        .name(productName)
                        .brandSex(getSexPrefix(url))
                        .brandName(brandName)
                        .price(productPrice)
                        .discountPercentage(productDiscountPercentage)
                        .productLink(productLink)
                        .sku(productSkU)
                        .colorCode(productColorCode)
                        .doublePrice(productDoublePrice)
                        .extraSalesPercentage(extraDiscountPercentage)
                        .build();


                pageProductList.add(doubleFProduct);
            }

        }

        assert (productTotalNum == pageProductList.size());

        return pageProductList;
    }

    public List<DoubleFProduct> findDifferentAndAlarm(ChromeDriver driver, WebDriverWait wait, String[] brandNameList, String sexPrefix) {
        List<DoubleFProduct> findDoubleFProduct = new ArrayList<>();

        for (int i = 0; i < brandNameList.length; i++) {

            //페이지 URL 만들기
            String brandName = brandNameList[i];
            String url = makeBrandUrl(brandName, sexPrefix);

            List<DoubleFProduct> pageProductData = getPageProductData(driver, wait, url, brandName);

            //상품 정보 존재할 경우
            Map<String, DoubleFProduct> eachBrandHashMap = doubleFBrandHashData.getBrandHashMap(sexPrefix, brandName);
            HashSet<String> productKeySet = doubleFBrandHashData.getProductKeySet();

            for (DoubleFProduct product : pageProductData) {
                if (!eachBrandHashMap.containsKey(getDoubleFProductKey(product))) {
                    //새로운 재품일 경우
                    if (!productKeySet.contains(getDoubleFProductKey(product))) {
                        log.info(DOUBLE_F_LOG_PREFIX + "새로운 제품" + product);
                        getDetailProductInfo(driver, wait, product);
                        discordBot.sendNewProductInfoCommon(
                                DOUBLE_F_NEW_PRODUCT_CHANNEL,
                                product.makeDiscordMessageDescription(),
                                product.getProductLink(),
                                null,
                                Stream.of(product.getSku(), product.getColorCode()).toArray(String[]::new)
                        );


                        //discordBot.sendNewProductInfo(DOUBLE_F_NEW_PRODUCT_CHANNEL, product, url);

                        product.updateDetectedCause(NEW_PRODUCT);
                        findDoubleFProduct.add(product);

                        productFileWriter.writeProductInfo(product.changeToProductFileInfo(DOUBLE_F, NEW_PRODUCT));

                        //보낸 상품 체크
                        productKeySet.add(getDoubleFProductKey(product));
                    } else {
                        log.error(DOUBLE_F_LOG_PREFIX + "상품 중복 " + product);
                    }

                } else {

                    //포함 되어있고,할인 퍼센테이지가 다를 경우
                    DoubleFProduct beforeProduct = eachBrandHashMap.get(getDoubleFProductKey(product));

                    if (!beforeProduct.getExtraSalesPercentage().equals(product.getExtraSalesPercentage())) {
                        log.info(DOUBLE_F_LOG_PREFIX + "Extra 할인율 변경" + beforeProduct.getExtraSalesPercentage() + " -> " + product.getExtraSalesPercentage());
                        getDetailProductInfo(driver, wait, product);
                        discordBot.sendDiscountChangeInfoCommon(
                                DOUBLE_F_DISCOUNT_CHANNEL,
                                product.makeDiscordDiscountExtraMessageDescription(beforeProduct.getExtraSalesPercentage()),
                                product.getProductLink(),
                                null,
                                Stream.of(product.getSku(), product.getColorCode()).toArray(String[]::new)
                        );
                        product.updateDetectedCause(DISCOUNT_CHANGE);
                        productFileWriter.writeProductInfo(product.changeToProductFileInfo(DOUBLE_F, DISCOUNT_CHANGE));

                        findDoubleFProduct.add(product);
                        continue;
                    }


                    if (!beforeProduct.getDiscountPercentage().equals(product.getDiscountPercentage())) {
                        log.info(DOUBLE_F_LOG_PREFIX + "할인율 변경" + beforeProduct.getDiscountPercentage() + " -> " + product.getDiscountPercentage());
                        getDetailProductInfo(driver, wait, product);
                        //discordBot.sendDiscountChangeInfo(DOUBLE_F_DISCOUNT_CHANNEL, product, url, beforeProduct.getDiscountPercentage());
                        discordBot.sendDiscountChangeInfoCommon(
                                DOUBLE_F_DISCOUNT_CHANNEL,
                                product.makeDiscordDiscountMessageDescription(beforeProduct.getDiscountPercentage()),
                                product.getProductLink(),
                                null,
                                Stream.of(product.getSku(), product.getColorCode()).toArray(String[]::new)
                        );
                        product.updateDetectedCause(DISCOUNT_CHANGE);
                        productFileWriter.writeProductInfo(product.changeToProductFileInfo(DOUBLE_F, DISCOUNT_CHANGE));

                        findDoubleFProduct.add(product);
                    }
                }
            }

            //검사 후 현재 상태로 기존 데이터 변경
            if (pageProductData.size() > 0) {
                eachBrandHashMap.clear();
                for (DoubleFProduct product : pageProductData) {
                    eachBrandHashMap.put(getDoubleFProductKey(product), product);
                }
            }
        }
        return findDoubleFProduct;
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


    public String makeBrandUrl(String brandName, String sexPrefix) {
        return "https://www.thedoublef.com/bu_en/" + sexPrefix + "/designers/" + brandName + "/";
    }

    private double changePriceToDouble(String price) {
        String[] split = price.split(" ");
        if (split.length == 2) {

            return Double.parseDouble(split[1].replace("€", "").replace(",", "").strip());
        } else {
            return Double.parseDouble(split[0].replace("€", "").replace(",", "").strip());
        }
    }

    private String getDoubleFProductKey(DoubleFProduct product) {
        return product.getId();
    }

    private String getSexPrefix(String url) {
        if (url.contains("man")) {
            return "man";
        } else {
            return "woman";
        }
    }


}

package com.example.monitor.monitoring.style;

import chrome.ChromeDriverTool;
import com.example.monitor.file.ProductFileWriter;
import com.example.monitor.infra.discord.DiscordBot;
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

import static com.example.monitor.infra.discord.DiscordString.STYLE_DISCOUNT_CHANNEL;
import static com.example.monitor.infra.discord.DiscordString.STYLE_NEW_PRODUCT_CHANNEL;
import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI_BRAND_NAME_LIST;
import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI_BRAND_URL_LIST;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.DISCOUNT_CHANGE;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.NEW_PRODUCT;
import static com.example.monitor.monitoring.style.StyleFindString.STYLE;
import static com.example.monitor.monitoring.style.StyleFindString.STYLE_LOG_PREFIX;
import static com.example.monitor.monitoring.style.StyleFindString.brandNameList;
import static com.example.monitor.monitoring.style.StyleFindString.brandUrlList;

@Slf4j
@Component
@RequiredArgsConstructor
public class StyleMonitorCore implements IMonitorService {

    private final ProductFileWriter productFileWriter;

    private final DiscordBot discordBot;

    @Value("${style.user.id}")
    private String userId;

    @Value("${style.user.pw}")
    private String userPw;


    @Getter
    private final StyleBrandHashData styleBrandHashData;

    @Override
    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {


        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        //로그인
        login(driver, wait);

        //데이터 로드
        loadData(driver, wait, brandUrlList, brandNameList);

        chromeDriverTool.isLoadData(true);

    }

    @Override
    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {
        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        List<StyleProduct> styleProductList = new ArrayList<>();
        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(STYLE_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }

        log.info(STYLE_LOG_PREFIX + "STYLE FIND NEW PRODUCT START==");
        List<StyleProduct> styleFindList = findDifferentAndAlarm(chromeDriver, wait, BIFFI_BRAND_URL_LIST, BIFFI_BRAND_NAME_LIST);

        log.info(STYLE_LOG_PREFIX + "STYLE FIND NEW PRODUCT FINISH==");

    }


    @Override
    public void login(ChromeDriver driver, WebDriverWait wait) {

        driver.get("https://www.styleisnow.com/business/customer/account/login/referer/aHR0cHM6Ly93d3cuc3R5bGVpc25vdy5jb20vYnVzaW5lc3MvY3VzdG9tZXIvYWNjb3VudC9pbmRleC8~/");

        WebElement loginElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("email")));

        loginElement.sendKeys(userId);

        WebElement pwElement = driver.findElement(By.id("pass"));
        pwElement.sendKeys(userPw);

        try {
            Thread.sleep(1500);
        } catch (Exception e) {
            log.error("로그인 에러");
            e.printStackTrace();
        }

        WebElement button = driver.findElement(By.xpath("//Button[@class='btn btn-primary']"));
        button.click();

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            log.error("로그인 에러");
            e.printStackTrace();
        }
    }

    public void loadData(ChromeDriver driver, WebDriverWait wait, String[] brandUrlList, String[] brandNameList) {


        for (int i = 0; i < brandNameList.length; i++) {

            String brandName = brandNameList[i];
            List<StyleProduct> styleProductList = getPageProductData(driver, wait, brandUrlList[i], brandNameList[i]);
            //log.info("brandName = " + brandName + "\t total size = " + styleProductList.size());
            Map<String, StyleProduct> brandHashMap = styleBrandHashData.getBrandHashMap(brandName);

            for (StyleProduct styleProduct : styleProductList) {
                brandHashMap.put(styleProduct.getId(), styleProduct);
            }

        }
    }

    public List<StyleProduct> getPageProductData(ChromeDriver driver, WebDriverWait wait, String url, String brandName) {

        //페이지로 이동
        driver.get(url);
        int totalPage = 1;
        //페이지 개수 새기
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@class='uppercase text-sm']//span[@class='toolbar-number']")));
            List<WebElement> elements = driver.findElements(By.xpath("//p[@class='uppercase text-sm']//span[@class='toolbar-number']"));

            int sizePerPage = Integer.parseInt(elements.get(0).getText());
            int totalSize = Integer.parseInt(elements.get(1).getText());
            totalPage = totalSize / sizePerPage + 1;

        } catch (Exception e) {
            log.info("total page 1개 url 체크확인 = " + url);
        }

        //상품 데이터 페이지별 Get
        List<StyleProduct> styleProductList = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            if (i != 1) {
                driver.get(url + "?p=" + i);
            }
            WebElement productContainerElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("custom-product-grid")));
            List<WebElement> products = productContainerElement.findElements(By.xpath(".//div[@class='w-full cursor-pointer']"));
            //상품 체크
            for (WebElement product : products) {

                String name = "";
                String price = "";
                String discountPercentage = "0%";
                String imageSrc = "";
                String id = "";
                String detailLink = "";
                double finalPrice = 0;

                try {
                    WebElement nameElement = product.findElement(By.xpath(".//span[@class='no-2grid-mobile hidden md:inline']"));
                    name = nameElement.getText();

                    //Get ID,DetailLink
                    {
                        String onclick = product.getAttribute("onclick");
                        String tempId = onclick.split("=")[1].trim();
                        int startIndex = tempId.indexOf("'");
                        int lastIndex = tempId.lastIndexOf("'");
                        id = tempId.substring(startIndex + 1, lastIndex);
                        detailLink = id;
                    }

                    //Get price, discountPercentage, imageSrc
                    {
                        product.findElement(By.xpath(".//span[@class='no-2grid-mobile hidden md:inline']"));
                        List<WebElement> priceElements = product.findElements(By.xpath(".//div[@class='price-box price-final_price text-right ']//span"));

                        if (priceElements.size() > 1) {
                            discountPercentage = priceElements.get(1).getText();
                            price = priceElements.get(2).getText().replaceAll(",", "");

                        } else {
                            price = priceElements.get(0).getText().replaceAll(",", "");

                        }

                        double originPrice = Double.parseDouble(price.split("€")[1]);
                        finalPrice = (originPrice * 0.95);

                        WebElement imageElement = product.findElement(By.xpath(".//img[@class='group-hover:hidden max-listing-img']"));
                        imageSrc = imageElement.getAttribute("src");
                    }


                } catch (Exception e) {
                    log.error("상품 정보 획득 에러 확인필요");
                    e.printStackTrace();
                }

                StyleProduct styleProduct = StyleProduct.builder()
                        .id(id)
                        .brandName(brandName)
                        .name(name)
                        .discountPercentage(discountPercentage)
                        .price(price)
                        .doublePrice(finalPrice)
                        .productLink(detailLink)
                        .imageUrl(imageSrc)
                        .monitoringSite("Style")
                        .build();

                styleProductList.add(styleProduct);
            }
        }

        return styleProductList;
    }

    public List<StyleProduct> findDifferentAndAlarm(ChromeDriver chromeDriver, WebDriverWait wait, String[] styleBrandUrlList, String[] styleBrandNameList) {

        assert (styleBrandUrlList.length == styleBrandNameList.length);

        List<StyleProduct> findStyleProductList = new ArrayList<>();

        for (int i = 0; i < styleBrandUrlList.length; i++) {

            Map<String, StyleProduct> eachBrandHashMap = styleBrandHashData.getBrandHashMap(styleBrandNameList[i]);
            List<StyleProduct> styleProductList = new ArrayList<>();

            try {
                styleProductList = getPageProductData(chromeDriver, wait, styleBrandUrlList[i], styleBrandNameList[i]);
            } catch (Exception e) {
                log.error(STYLE_LOG_PREFIX + "logout error maybe");
                login(chromeDriver, wait);
                styleProductList = getPageProductData(chromeDriver, wait, styleBrandUrlList[i], styleBrandNameList[i]);
            }

            HashSet<String> productKeySet = styleBrandHashData.getProductKeySet();
            for (StyleProduct styleProduct : styleProductList) {

                //새 제품인 경우
                if (!eachBrandHashMap.containsKey(styleProduct.getId())) {
                    if (!productKeySet.contains(styleProduct.getId())) {
                        //discord bot 알람
                        getProductMoreInfo(chromeDriver, wait, styleProduct);
                        log.info(STYLE_LOG_PREFIX + "새로운 제품" + styleProduct);

                        discordBot.sendNewProductInfo(STYLE_NEW_PRODUCT_CHANNEL, styleProduct);
                        findStyleProductList.add(styleProduct);

                        productFileWriter.writeProductInfo(styleProduct.changeToProductFileInfo(STYLE, NEW_PRODUCT));
                        //보낸상품 체크
                        productKeySet.add(styleProduct.getId());

                    }
                } else {
                    StyleProduct beforeProduct = eachBrandHashMap.get(styleProduct.getId());
                    if (!beforeProduct.getDiscountPercentage().equals(styleProduct.getDiscountPercentage())) {
                        log.info(STYLE_LOG_PREFIX + "할인율 변경" + beforeProduct.getDiscountPercentage() + " -> " + styleProduct.getDiscountPercentage());
                        //discord bot 알람
                        getProductMoreInfo(chromeDriver, wait, styleProduct);
                        discordBot.sendDiscountChangeInfo(STYLE_DISCOUNT_CHANNEL, styleProduct, beforeProduct.getDiscountPercentage());
                        findStyleProductList.add(styleProduct);
                        productFileWriter.writeProductInfo(styleProduct.changeToProductFileInfo(STYLE, DISCOUNT_CHANGE));
                    }
                }
            }

            if (styleProductList.size() > 0) {
                eachBrandHashMap.clear();
                for (StyleProduct styleProduct : styleProductList) {
                    eachBrandHashMap.put(styleProduct.getId(), styleProduct);
                }
            } else {
                log.error(STYLE_LOG_PREFIX + "styleProduct 데이터 조회 실패 **확인요망**");
            }

        }

        return findStyleProductList;
    }

    public void getProductMoreInfo(ChromeDriver driver, WebDriverWait wait, StyleProduct styleProduct) {

        if (!styleProduct.getProductLink().equals(driver.getCurrentUrl())) {
            driver.get(styleProduct.getProductLink());
        }

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[@class='text-black']")));
        List<WebElement> detailElementList = driver.findElements(By.xpath("//tr[@class='text-black']"));

        for (WebElement webElement : detailElementList) {
            String text = webElement.findElement(By.xpath(".//th")).getText().toLowerCase();
            if (text.contains("made in")) {
                String madeBy = webElement.findElement(By.xpath(".//td")).getText();
                styleProduct.setMadeBy(madeBy);
            } else if (text.contains("supplier code")) {
                String sku = webElement.findElement(By.xpath(".//td")).getText();
                styleProduct.setSku(sku);
            } else if (text.contains("season")) {
                String season = webElement.findElement(By.xpath(".//td")).getText();
                styleProduct.setSeason(season);
            }
        }

    }

}
package com.example.monitor.monitoring.antonioli;

import chrome.ChromeDriverTool;
import com.example.monitor.Util.RandomUtil;
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
import java.util.List;
import java.util.Map;

import static com.example.monitor.monitoring.antonioli.AntonioliFindString.ANTONIOLI_LOG_PREFIX;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.MANS_PREFIX;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.WOMANS_PREFIX;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.manBrandNameList;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.womanBrandNameList;


@Slf4j
@Component
@RequiredArgsConstructor
public class AntonioliMonitorCore implements IMonitorService {

    private final ObjectMapper objectMapper;

    private final DiscordBot discordBot;

    @Getter
    private final AntonioliBrandHashData antonioliBrandHashData;

    @Override
    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();
        login(driver, wait);

        //데이터 로드
        loadData(driver, wait, manBrandNameList, MANS_PREFIX);
        loadData(driver, wait, womanBrandNameList, WOMANS_PREFIX);

        chromeDriverTool.isLoadData(true);

    }


    @Override
    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {

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

            //상품 정보 존재할 경우
            Map<String, AntonioliProduct> eachBrandHashMap = antonioliBrandHashData.getBrandHashMap(sexPrefix, brandName);
            for (AntonioliProduct product : pageProductData) {
                eachBrandHashMap.put(product.getId(), product);
            }

        }

    }

    public List<AntonioliProduct> getPageProductDataOrNull(ChromeDriver driver, WebDriverWait wait, String url, String brandName) {

        try{
            int randomSec = RandomUtil.getRandomSec(5, 10);
            Thread.sleep(randomSec * 1000);
        }catch (Exception e){
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
            log.info("totalPages = " + totalPages);
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

                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='ProductItem  ProductItem--sold-out']")));
                } catch (Exception e2) {

                    log.error(ANTONIOLI_LOG_PREFIX + "logout Redirection  or FIND PRODUCT ERROR");
                    return null;
                }
                log.info("Sold Out 으로 인한 에러 -> 다음페이지로 ");
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
//                        .imgUrl(imageUrl)
                        .build();

                log.info(antonioliProduct.toString());
                pageProductList.add(antonioliProduct);
            }

        }
        return pageProductList;
    }

    private String makeBrandUrl(String brandName, String sexPrefix) {

        return "https://stores.antonioli.eu/collections/designer-" + brandName + "/" + sexPrefix;
    }

}

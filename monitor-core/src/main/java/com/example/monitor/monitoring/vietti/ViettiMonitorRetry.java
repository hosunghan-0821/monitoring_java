package com.example.monitor.monitoring.vietti;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.example.monitor.monitoring.vietti.ViettiFindString.VIETTI_LOG_PREFIX;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViettiMonitorRetry {


    @Retryable(retryFor = {TimeoutException.class}, backoff = @Backoff(delay = 1000))
    public List<ViettiProduct> getPageProductData(ChromeDriver driver, WebDriverWait wait, String url, String brandName) {

        driver.get(url);

        List<ViettiProduct> pageProductList = new ArrayList<>();

        int totalPages = 1;
        int productTotalNum = 48;

        //페이지 로드 대기
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='products-grid center ng-star-inserted']")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='product-counter']")));
        WebElement headerElement = driver.findElement(By.xpath("//div[@class='product-counter']"));
        wait.until(ExpectedConditions.textToBePresentInElement(headerElement, "of"));

        String pageInfo = headerElement.getText();
        String totalProducts = pageInfo.split("of")[1].strip().split(" ")[0];
        totalPages = Integer.parseInt(totalProducts) % productTotalNum == 0 ? Integer.parseInt(totalProducts) / productTotalNum : Integer.parseInt(totalProducts) / productTotalNum + 1;


        for (int i = 1; i <= totalPages; i++) {

            driver.get(url + "?p=" + i);

            //상품 최외각 div
            WebElement productParentElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='products-grid center ng-star-inserted']")));

            //상품 내각 div
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='product-grid-element ng-star-inserted']")));
            } catch (Exception e) {
                log.error(VIETTI_LOG_PREFIX + "상품정보 없음 url 확인" + url);
            }

            //상품 정보 최워질 때까지 대기
            WebElement priceWaitElement = driver.findElement(By.xpath("//div[@class='price-wrapper__compact__current-unit-price currency-value']"));
            wait.until(ExpectedConditions.textToBePresentInElement(priceWaitElement, "€"));


            List<WebElement> productElements = productParentElement.findElements(By.xpath(".//div[@class='product-grid-element ng-star-inserted']"));


            for (WebElement product : productElements) {

                String productId = "";

                String productName = "상품 이름 정보 오류";
                String productLink = "";


                String productDiscountPercentage = "0%";
                String productOriginPrice = "";
                String productPrice = "";
                String imageSrc = "";
                double productDoublePrice = 0;
                String sku = "";

                //상품 id
                try {
                    WebElement idElement = product.findElement(By.xpath(".//div[@class='product__code ng-star-inserted']"));
                    productId = idElement.getText();
                    sku = idElement.getText();
                } catch (Exception e) {
                    log.error(VIETTI_LOG_PREFIX + "상품 아이디가 없습니다. \t 상품 누락됩니다. 확인요망");
                    continue;
                }

                //상품 상세정보 , 이름
                try {
                    WebElement nameElement = product.findElement(By.xpath(".//a[@class='product__name']"));
                    productName = nameElement.getText();
                    productLink = nameElement.getAttribute("href");
                    productId = productId + ":::" + productLink;
                } catch (Exception e) {
                    log.error(VIETTI_LOG_PREFIX + "상품 이름과 상품 상세링크가 없습니다. \t. 확인요망");

                }

                //할인율 및 기타 정보
                try {
                    WebElement discountPercentageElement = product.findElement(By.xpath(".//span[@class='price-wrapper__full__discounts__value ng-star-inserted']"));
                    productDiscountPercentage = discountPercentageElement.getText();

                    WebElement priceElement = product.findElement(By.xpath(".//div[@class='price-wrapper__compact__current-unit-price currency-value']"));
                    productPrice = priceElement.getText();
                    WebElement originPriceElement = product.findElement(By.xpath(".//del[@class='price-wrapper__compact__old-unit-price currency-value ng-star-inserted']"));
                    productOriginPrice = originPriceElement.getText();
                } catch (Exception e) {

                    try {
                        WebElement priceElement = product.findElement(By.xpath(".//div[@class='price-wrapper__compact__current-unit-price currency-value']"));
                        productOriginPrice = priceElement.getText();
                        productPrice = priceElement.getText();
                    } catch (Exception ex) {
                        log.error("할인정보 없음 + 가격정보도 없음 url" + url + "productName" + productName);
                        continue;
                    }

                }

                //이미지 링크
                try {
                    WebElement imageElement = product.findElement(By.xpath(".//img[@class='ng-star-inserted']"));
                    imageSrc = imageElement.getAttribute("src");

                    productId = productId + ":::" + imageSrc;
                } catch (Exception e) {
                    log.error(VIETTI_LOG_PREFIX + "이미지 링크 오류");
                }


                ViettiProduct viettiProduct = ViettiProduct.builder()

                        .id(productId).name(productName).price(productPrice).originPrice(productOriginPrice).discountPercentage(productDiscountPercentage).productLink(productLink).imageSrc(imageSrc).brandName(brandName).sku(sku).build();


                if (productId.isEmpty()) {
                    log.error(VIETTI_LOG_PREFIX + "상품 정보 획득에러 수정필요 !!!");
                }

                pageProductList.add(viettiProduct);
            }

        }

        return pageProductList;
    }

    @Recover
    public List<ViettiProduct> recoverTimeout(TimeoutException ex, ChromeDriver driver, WebDriverWait wait, String url, String brandName) {
        log.error("{} Timeout Exception Recover Empty Array url : {}", VIETTI_LOG_PREFIX, url);
        return new ArrayList<>();
    }
}

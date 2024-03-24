package com.example.monitor;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.chrome.ChromeDriverToolFactory;
import com.example.monitor.monitoring.julian.JulianMonitorCore;
import com.example.monitor.monitoring.dobulef.DoubleFFindString;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;
import static com.example.monitor.monitoring.julian.JulianFindString.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomApplicationRunner implements ApplicationRunner {

    private final ChromeDriverToolFactory chromeDriverToolFactory;

    private final JulianMonitorCore julianMonitorCore;

    @Override
    public void run(ApplicationArguments args) throws Exception {


        chromeDriverToolFactory.makeChromeDriverTool("test");
        ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool("test");
        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();
        chromeDriver.get("https://www.thedoublef.com/bu_en/customer/account/login/referer/aHR0cHM6Ly93d3cudGhlZG91YmxlZi5jb20vYnVfZW4v/");

        //쿠키 허용
        WebElement cookieElement = chromeDriver.findElement(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
        cookieElement.click();


        //로그인
        WebElement loginElement = chromeDriver.findElement(By.id(DF_ID));
        loginElement.sendKeys("dopeesince2022@gmail.com");

        WebElement pwElement = chromeDriver.findElement(By.id(DF_PASS));
        pwElement.sendKeys("DOPEESHOE99!");

        WebElement button = chromeDriver.findElement(By.xpath(DF_LOGIN_BUTTON_XPATH));
        button.click();

        //로그인 이후 5초 정지..
        Thread.sleep(5000);
        //메뉴탭 누르고 원하는 곳으로 이동
        String[] manBrandNameList = DoubleFFindString.manBrandNameList;
        String[] womanBrandNameList = DoubleFFindString.womanBrandNameList;
        for (int i = 0; i < manBrandNameList.length; i++) {

            //페이지 이동
            String brandName = manBrandNameList[i];
            String urlString = "https://www.thedoublef.com/bu_en/man/designers/" + brandName + "/";
            chromeDriver.get(urlString);

            //상품 상위 태그
            List<WebElement> productList = new ArrayList<>();
            try {
                WebElement topDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='grid grid-cols-4 gap-5 md:gap-4 md:gap-y-16 md:grid-cols-12 lg:gap-5-5 opacity-100 transition-opacity duration-500 items-container items-stretch']")));
                productList = topDiv.findElements(By.xpath(CHILD_DIV));
            } catch (Exception e) {
                log.error("** 확인요망 ** 상품 정보 못찾음 URL 이나 기타 다른 요인들.. etc");
            }

            //상품 정보 로드
            for (WebElement product : productList) {
                String productName = "상품이름 정보 없습니다.";
                String productDiscountPercentage = "0%";
                String productPrice = "";

                // 상품이름 정보
                try {
                    WebElement productNameElement = product.findElement(By.xpath(".//h4[@class='product-card__name truncate ... font-light text-xs tracking-1-08 leading-snug mb-5px']"));
                    productName = productNameElement.getText();
                } catch (Exception e) {
                    log.error("** 확인요망 **" + brandName + "의 상품에 이름이 없습니다. 홈페이지 및 프로그램 확인 바랍니다.");
                    //discordError 알림 만들자
                }
                // 상품할인율 정보
                try {
                    WebElement discountPercentage = product.findElement(By.xpath(".//div[@class='product-card__discount absolute z-5 top-0 left-0 font-medium tracking-0-12 leading-8 text-white text-4xs h-5-5 px-5px bg-primary lg:tracking-0-15 lg:leading-10 lg:text-2xs lg:h-6 lg:px-10px']//span"));
                    productDiscountPercentage = discountPercentage.getText();
                } catch (Exception e) {
                    log.info(productName + " 의 할인정보가 없습니다.");
                }

                // 상품 가격 정보
                try {
                    List<WebElement> productPriceElementList = product.findElements(By.xpath(".//span[@class='price']"));
                    for (WebElement productPriceElement : productPriceElementList) {
                        productPrice = productPrice + " " + productPriceElement.getText();
                    }
                    productPrice = productPrice.strip();

                } catch (Exception e) {
                    log.info(productName + " 의 가격 정보가 없습니다.");
                }

                DoubleFProduct doubleFProduct = DoubleFProduct.builder()
                        .nameId(productName)
                        .brand(brandName)
                        .price(productPrice)
                        .discountPercentage(productDiscountPercentage)
                        .build();

                System.out.println(doubleFProduct.toString());
            }

        }
        //chromeDriver.get("https://www.thedoublef.com/bu_en/man/designers/balenciaga/");


//
//        chromeDriverToolFactory.makeChromeDriverTool(ALL_CATEGORIES);
//        chromeDriverToolFactory.makeChromeDriverTool(PROMO);
        //
//        log.info("Load Data From Site");
//        try {
//            //로그인
//            ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(ALL_CATEGORIES);
//            ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
//            WebDriverWait wait = chromeDriverTool.getWebDriverWait();
//            monitorCore.login(chromeDriver);
//
//            for (int i = 1; i < 3; i++) {
//                //페이지 이동
//                monitorCore.changeUrl(chromeDriver, ALL_CATEGORIES_URL + "?page=" + i);
//
//                //하위 데이터
//                List<WebElement> productDataDivs = monitorCore.getInnerProductDivs(wait);
//
//                //상품 하위 데이터 조회
//                List<Product> productData = monitorCore.getProductData(productDataDivs);
//
//                //정보가져오기
//                monitorCore.loadData(chromeDriverTool.getDataHashMap(),productData);
//
//            }
//            //로드체크
//            chromeDriverTool.isLoadData(true);
//            log.info("ALl CATEGORIES HASH MAP" + chromeDriverTool.getDataHashMap().size());
//        } catch (Exception e) {
//            log.error("All Category Data Load Error");
//            e.printStackTrace();
//        }
//
//        try {
//            //로그인
//            ChromeDriverTool chromeDriverTool = chromeDriverToolFactory.getChromeDriverTool(PROMO);
//            ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
//            WebDriverWait wait = chromeDriverTool.getWebDriverWait();
//            monitorCore.login(chromeDriver);
//
//            for (int i = 1; i < 3; i++) {
//                //페이지 이동
//                monitorCore.changeUrl(chromeDriver, PROMO_URL + "?page=" + i);
//
//                //하위 데이터
//                List<WebElement> productDataDivs = monitorCore.getInnerProductDivs(wait);
//
//                //상품 하위 데이터 조회
//                List<Product> productData = monitorCore.getProductData(productDataDivs);
//
//                //정보가져오기
//                monitorCore.loadData(chromeDriverTool.getDataHashMap(),productData);
//
//            }
//            //Load 확인
//            chromeDriverTool.isLoadData(true);
//            log.info("PROMO HASH MAP"+chromeDriverTool.getDataHashMap().size());
//        } catch (Exception e) {
//            log.error("PROMO Data Load Error");
//            e.printStackTrace();
//        }
//

    }
}

package com.example.monitor.monitoring.theclub;

import chrome.ChromeDriverTool;
import com.example.monitor.file.ProductFileWriter;
import module.discord.DiscordBot;
import com.example.monitor.monitoring.global.IMonitorService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static module.discord.DiscordString.ALL_CATEGORIES_CHANNEL;
import static com.example.monitor.monitoring.theclub.TheClubFindString.*;
import static module.discord.DiscordString.THE_CLUB_DISCOUNT_CHANGE_CHANNEL;
import static module.discord.DiscordString.THE_CLUB_NEW_PRODUCT_CHANNEL;

@Slf4j
@Component
@RequiredArgsConstructor
public class TheClubMonitorCore implements IMonitorService {

    private final ProductFileWriter productFileWriter;
    private final DiscordBot discordBot;

    @Value("${theclub.user.id}")
    private String userId;

    @Value("${theclub.user.pw}")
    private String userPw;

    @Getter
    private final TheClubBrandHashData theClubBrandHashData;

    @Override
    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {
        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        try {
            // 로그인
            login(driver, wait);

            // 데이터 로드
            loadData(driver, wait, THE_CLUB_BRAND_URL_LIST, THE_CLUB_BRAND_NAME_LIST);

            chromeDriverTool.isLoadData(true);
            log.info(THE_CLUB_LOG_PREFIX + "== THE_CLUB LOAD DATA FINISH ==");
        } catch (Exception e) {
            log.error(THE_CLUB_LOG_PREFIX + "Data Load Error: " + e.getMessage());
        }
    }

    @Override
    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {
        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        if (!chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(THE_CLUB_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }

        log.info(THE_CLUB_LOG_PREFIX + "THE_CLUB FIND NEW PRODUCT START==");
        List<TheClubProduct> theClubProductList = findDifferentAndAlarm(chromeDriver, wait, THE_CLUB_BRAND_URL_LIST, THE_CLUB_BRAND_NAME_LIST);
        log.info(THE_CLUB_LOG_PREFIX + "THE_CLUB FIND NEW PRODUCT FINISH==");
    }

    @Override
    public void login(ChromeDriver driver, WebDriverWait wait) {
        try {
            driver.get(THE_CLUB_MAIN_PAGE);

            WebElement loginElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(TC_ID_ID)));
            loginElement.sendKeys(userId);

            WebElement pwElement = driver.findElement(By.id(TC_PASS_ID));
            pwElement.sendKeys(userPw);

            Thread.sleep(1500);

            WebElement button = driver.findElement(By.xpath(TC_LOGIN_BUTTON_XPATH));
            button.click();

            Thread.sleep(3000);
            log.info(THE_CLUB_LOG_PREFIX + "로그인 완료");
        } catch (Exception e) {
            log.error(THE_CLUB_LOG_PREFIX + "로그인 에러: " + e.getMessage());
        }
    }

    public void loadData(ChromeDriver driver, WebDriverWait wait, String[] brandUrlList, String[] brandNameList) {
        for (int i = 0; i < brandNameList.length; i++) {
            String brandName = brandNameList[i];
            List<TheClubProduct> theClubProductList = getPageProductData(driver, wait, brandUrlList[i], brandNameList[i]);

            Map<String, TheClubProduct> brandHashMap = theClubBrandHashData.getBrandHashMap(brandName);

            for (TheClubProduct theClubProduct : theClubProductList) {
                if (brandHashMap.containsKey(theClubProduct.getId())) {
                    log.error(THE_CLUB_LOG_PREFIX + "key error 버그 발생");
                }
                brandHashMap.put(theClubProduct.getId(), theClubProduct);
            }
        }
    }

    public List<TheClubProduct> getPageProductData(ChromeDriver driver, WebDriverWait wait, String url, String brandName) {
        List<TheClubProduct> productList = new ArrayList<>();

        // 페이지 1부터 시작해서 더 이상 상품이 없을 때까지 반복
        for (int page = 1; page <= 50; page++) { // 최대 10페이지까지 제한
            try {
                String pageUrl = url + "?page=" + page;
                driver.get(pageUrl);

                // 상품 목록 대기
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(TOP_DIV_XPATH)));

                // 상품 요소들 가져오기
                List<WebElement> productElements = driver.findElements(By.xpath(CHILD_PRODUCT_DIV));

                //마지막까지 데이터 로드
                if (!productElements.isEmpty()) {
                    ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
                    Thread.sleep(1000);
                }

                //전체 개수 확인
                productElements = driver.findElements(By.xpath(CHILD_PRODUCT_DIV));
                log.info(THE_CLUB_LOG_PREFIX + "페이지 " + page + " - " + brandName + ": " + productElements.size() + "개 상품 발견");

                for (WebElement productElement : productElements) {
                    try {
                        WebElement productNameElement = productElement.findElement(By.xpath(PRODUCT_NAME_XPATH));
                        WebElement productIdElement = productElement.findElement(By.xpath(PRODUCT_ID_XPATH));
                        String productName = productNameElement.getText();
                        String id = productIdElement.getAttribute("id");
                        String discountInfo = productElement.findElement(By.xpath(PRODUCT_DISCOUNT_XPATH)).getText();
                        String price = productElement.findElement(By.xpath(PRODUCT_PRICE_XPATH)).getText();
                        String imageUrl = productElement.findElement(By.xpath(PRODUCT_IMAGE_XPATH)).getAttribute("src");
                        String productLink = productIdElement.getAttribute("href");

                        // 가격을 double로 변환 (€315,58 EUR → 315.58)
                        double doublePrice = 0.0;
                        {
                            if (price != null && !price.isEmpty()) {
                                try {
                                    // 숫자와 콤마, 점만 남기고 모든 문자 제거
                                    String numericPrice = price.replaceAll("[^0-9,.]", "");
                                    // 유럽식 표기법 (콤마가 소수점) 처리
                                    if (numericPrice.contains(",") && numericPrice.contains(".")) {
                                        // 예: 1.315,58 → 1315.58
                                        numericPrice = numericPrice.replace(".", "").replace(",", ".");
                                    } else if (numericPrice.contains(",") && !numericPrice.contains(".")) {
                                        // 예: 315,58 → 315.58
                                        numericPrice = numericPrice.replace(",", ".");
                                    }
                                    doublePrice = Double.parseDouble(numericPrice);
                                } catch (NumberFormatException e) {
                                    log.debug(THE_CLUB_LOG_PREFIX + "가격 변환 실패: " + price);
                                    doublePrice = 0.0;
                                }
                            }
                        }


                        TheClubProduct product = TheClubProduct.builder()
                                .name(productName)
                                .brandName(brandName)
                                .price(price)
                                .doublePrice(doublePrice)
                                .salePercent(discountInfo)
                                .imageUrl(imageUrl)
                                .id(id)
                                .productLink(productLink)
                                .build();

                        productList.add(product);
                        log.info(product.toString());
                    } catch (Exception e) {
                        log.debug(THE_CLUB_LOG_PREFIX + "상품 데이터 파싱 실패: " + e.getMessage());
                    }
                }

            } catch (Exception e) {
                break; // 에러 발생시 페이지네이션 중단
            }
        }

        log.info(THE_CLUB_LOG_PREFIX + brandName + " 총 " + productList.size() + "개 상품 로드 완료");
        return productList;
    }

    public List<TheClubProduct> findDifferentAndAlarm(ChromeDriver chromeDriver, WebDriverWait wait, String[] brandUrlList, String[] brandNameList) {
        List<TheClubProduct> newProductList = new ArrayList<>();
        HashSet<String> productKeySet = theClubBrandHashData.getProductKeySet();

        for (int i = 0; i < brandNameList.length; i++) {
            String brandName = brandNameList[i];
            Map<String, TheClubProduct> brandHashMap = theClubBrandHashData.getBrandHashMap(brandName);

            List<TheClubProduct> currentProducts = getPageProductData(chromeDriver, wait, brandUrlList[i], brandName);

            for (TheClubProduct product : currentProducts) {
                if (!brandHashMap.containsKey(product.getId()) && !productKeySet.contains(product.getSku())) {
                    // 새로운 상품 발견
                    newProductList.add(product);
                    productKeySet.add(product.getSku());

                    // 상품 상세 정보 수집 (SKU, 컬러코드)
                    getProductMoreInfo(chromeDriver, wait, product);

                    // 디스코드 알림 전송
                    discordBot.sendNewProductInfoCommon(
                            THE_CLUB_NEW_PRODUCT_CHANNEL,
                            product.makeDiscordMessageDescription(),
                            product.getProductLink(),
                            product.getImageUrl(),
                            Stream.of(product.getSku(), product.getColorCode()).toArray(String[]::new)
                    );

                    // 파일 기록
                    productFileWriter.writeProductInfo(product.changeToProductFileInfo(THE_CLUB + " / " + brandName, NEW_PRODUCT));
                    log.info(THE_CLUB_LOG_PREFIX + "새 상품 발견: " + product);
                } else if (brandHashMap.containsKey(product.getId())) {
                    // 기존 상품의 할인율 변경 확인
                    TheClubProduct existingProduct = brandHashMap.get(product.getId());
                    String oldDiscount = existingProduct.getSalePercent();
                    String newDiscount = product.getSalePercent();

                    // 할인율이 변경되었는지 확인 (null 체크 포함)
                    boolean discountChanged = false;
                    if (oldDiscount == null && newDiscount != null) {
                        discountChanged = true;
                    } else if (oldDiscount != null && !oldDiscount.equals(newDiscount)) {
                        discountChanged = true;
                    }

                    if (discountChanged) {
                        log.info(THE_CLUB_LOG_PREFIX + "할인율 변경 감지 - " + product.getName() +
                                " 이전: " + oldDiscount + " → 현재: " + newDiscount);

                        // 상품 상세 정보 수집 (SKU, 컬러코드)
                        getProductMoreInfo(chromeDriver, wait, product);


                        // 할인 변경 디스코드 알림
                        discordBot.sendDiscountChangeInfoCommon(
                                THE_CLUB_DISCOUNT_CHANGE_CHANNEL,
                                product.makeDiscordDiscountMessageDescription(oldDiscount != null ? oldDiscount : "0%"),
                                product.getProductLink(),
                                product.getImageUrl(),
                                Stream.of(product.getSku(), product.getColorCode()).toArray(String[]::new)
                        );

                        // 할인 변경 파일 기록
                        productFileWriter.writeProductInfo(product.changeToProductFileInfo(THE_CLUB + " / " + brandName, DISCOUNT_CHANGE));
                    }
                }
            }

            // 기존 데이터 업데이트
            brandHashMap.clear();
            for (TheClubProduct product : currentProducts) {
                brandHashMap.put(product.getId(), product);
            }
        }

        return newProductList;
    }

    public void getProductMoreInfo(ChromeDriver driver, WebDriverWait wait, TheClubProduct theClubProduct) {
        try {
            // 상품 상세 페이지 링크 가져오기 (a 태그의 href)
            String productDetailUrl = theClubProduct.getProductLink();

            // 현재 페이지가 아니면 상세 페이지로 이동
            if (!productDetailUrl.equals(driver.getCurrentUrl())) {
                driver.get(productDetailUrl);
            }

            // 페이지 로딩 대기
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='product__info-wrapper grid__item scroll-trigger animate--slide-in']")));
            Thread.sleep(1000);

            List<WebElement> elements = driver.findElements(By.xpath("//ul[@class='product__text inline-richtext special_text']//li"));


            //SKU 찾기
            try {
                if (elements.size() >= 2) {

                    String skuText = elements.get(1).getText();

                    if (skuText != null && skuText.contains(":")) {
                        String[] skuParts = skuText.split(":");
                        if (skuParts.length > 1) {
                            String sku = skuParts[1].trim();
                            if (!sku.isEmpty()) {
                                theClubProduct.setSku(sku);
                            }
                        }
                    } else {
                        String sku = skuText;
                        theClubProduct.setSku(sku);
                    }
                }
            } catch (Exception e) {
                log.debug(THE_CLUB_LOG_PREFIX + "SKU 정보를 찾을 수 없습니다 - " + theClubProduct.getProductLink());
            }


            //칼러코드 찾기
            try {
                if (elements.size() >= 2) {
                    String colorCode = elements.get(2).getText().split(":")[1];
                    theClubProduct.setColorCode(colorCode);
                }

            } catch (Exception e) {
                log.debug(THE_CLUB_LOG_PREFIX + "컬러코드 정보를 찾을 수 없습니다 - " + theClubProduct.getProductLink());
            }

            // 이름 찾기
            try {
                WebElement element = driver.findElement(By.xpath("//div[@class='product__title']"));
                theClubProduct.setName(element.getText());
                ;
            } catch (Exception e) {
                log.debug(THE_CLUB_LOG_PREFIX + "이름 정보를 찾을 수 없습니다 - " + theClubProduct.getProductLink());
            }


        } catch (Exception e) {
            log.error(THE_CLUB_LOG_PREFIX + "상품 상세 정보 조회 실패 - " + theClubProduct.getProductLink() + ": " + e.getMessage());
        }

    }
}
package com.example.monitor.monitoring.julian;

import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.discord.DiscordBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
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

import static com.example.monitor.monitoring.julian.JulianFindString.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JulianMonitorCore {


    private final DiscordBot discordBot;
    @Value("${julian.user.id}")
    private String userId;

    @Value("${julian.user.pw}")
    private String userPw;


    public void runFindProductLogic(ChromeDriverTool chromeDriverTool, String pageUrl, String category,String discordChannelName) {
        ChromeDriver chromeDriver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();
        HashMap<String, JulianProduct> dataHashMap = chromeDriverTool.getDataHashMap();
        HashSet<String> dataKeySet = chromeDriverTool.getProductKeySet();
        if ( !chromeDriverTool.isLoadData() || !chromeDriverTool.isRunning()) {
            log.error(JULIAN_LOG_PREFIX + "Data Load or isRunning OFF");
            return;
        }
        log.info(JULIAN_LOG_PREFIX+ "START: " + category + " FIND NEW PRODUCT ");
        List<JulianProduct> findJulianProductList = new ArrayList<>();

        try {
            for (int i = 1; i < 3; i++) {
                //페이지 이동
                changeUrl(chromeDriver, pageUrl + "?page=" + i);

                //하위 데이터
                List<WebElement> productDataDivs = getInnerProductDivs(wait);

                //상품 하위 데이터 조회
                List<JulianProduct> julianProductData = getProductData(productDataDivs);

                //데이터 누적 HashMap 수정을 위해서
                findJulianProductList.addAll(julianProductData);

                //정보가져오기
                List<JulianProduct> newJulianProductList = findNewProduct(dataHashMap, julianProductData);

                if (julianProductData.size() != 48) {
                    log.info(JULIAN_LOG_PREFIX + "한 페이지에 size 개수 변동 확인요망! 현재사이즈 = " + newJulianProductList.size());
                }
                if (!newJulianProductList.isEmpty()) {

                    for (JulianProduct julianProduct : newJulianProductList) {

                        //새 상품 set에 있다면, 알람x 보내면 안됨.
                        if(dataKeySet.contains(julianProduct.getId())){
                            log.info(JULIAN_LOG_PREFIX  + "이전에 알람 보냈던 제품 PASS 상품ID " +julianProduct.getId());
                            continue;
                        }

                        //새 상품 set에 없다면, 알람 보내고, 보낸걸 기록
                        dataKeySet.add(julianProduct.getId());

                        julianProduct.setCategory(category);
                        discordBot.sendNewProductInfo(discordChannelName, julianProduct);
                        log.info(JULIAN_LOG_PREFIX + "New Product = " + julianProduct);
                    }
                }
            }
            // 이후에 HashMap 재 정립
            dataHashMap.clear();
            loadData(dataHashMap, findJulianProductList);

        } catch (NoSuchWindowException e) {
            log.error(JULIAN_LOG_PREFIX + "Chrome Driver Down!!");
            return;
        } catch (Exception e) {
            log.error(JULIAN_LOG_PREFIX + "자동 로그아웃");
            // 모니터링 다시 시작
            login(chromeDriver);
        }

        log.info(JULIAN_LOG_PREFIX + "END:  FIND NEW PRODUCT FINISH");
    }

    public void changeUrl(ChromeDriver driver, String url) {
        driver.get(url);
    }

    public void login(ChromeDriver driver) {
        assert (driver != null);


        driver.get("https://b2bfashion.online/");
        WebElement id = driver.findElement(By.id(ID_FORM));
        id.sendKeys(userId);

        WebElement password = driver.findElement(By.id(PASS_FORM));
        password.sendKeys(userPw);

        WebElement loginButton = driver.findElement(By.id(SUBMIT_FORM));
        loginButton.click();
    }


    public List<WebElement> getInnerProductDivs(WebDriverWait wait) {
        WebElement topDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(PRODUCT_TOP_DIV)));
        return topDiv.findElements(By.xpath(CHILD_DIV));
    }

    public List<JulianProduct> getProductData(List<WebElement> childDivs) {

        List<JulianProduct> julianProductList = new ArrayList<>();

        for (WebElement child : childDivs) {
            WebElement image = child.findElement(By.xpath(PRODUCT_IMAGE));
            WebElement name = child.findElement(By.xpath(PRODUCT_NAME));
            WebElement reference = child.findElement(By.xpath(PRODUCT_ID));

            String imageSrc = image.getAttribute("src");
            List<WebElement> priceElementList = child.findElements(By.xpath(".//p[@class='price']"));
            String priceString = "가격정보 없음";

            if (!priceElementList.isEmpty()) {
                priceString = priceElementList.get(0).getText();
            } else {
                List<WebElement> originlPriceList = child.findElements(By.xpath(".//span[@class='price']"));
                if (!originlPriceList.isEmpty()) {
                    priceString = originlPriceList.get(0).getText();
                }
            }

            JulianProduct julianProduct = JulianProduct.builder()
                    .name(name.getText())
                    .Id(reference.getText())
                    .imageSrc(imageSrc)
                    .price(priceString)
                    .build();
            julianProductList.add(julianProduct);

//            log.info("image link = " + imageSrc);
//            log.info("name = " + name.getText());
//            log.info("reference = " + reference.getText());

        }

        return julianProductList;

    }

    public void loadData(HashMap<String, JulianProduct> productHashMap, List<JulianProduct> julianProductData) {

        for (JulianProduct julianProduct : julianProductData) {
            if (!productHashMap.containsKey(julianProduct.getId())) {
                productHashMap.put(julianProduct.getId(), julianProduct);
            } else {
                log.error(JULIAN_LOG_PREFIX + "Load 시 겹치는 ID 존재 확인 필요 상품정보 " + julianProduct.toString());
            }
        }
    }

    public List<JulianProduct> findNewProduct(HashMap<String, JulianProduct> productHashMap, List<JulianProduct> julianProductData) {
        List<JulianProduct> newJulianProductList = new ArrayList<>();

        for (JulianProduct julianProduct : julianProductData) {
            if (!productHashMap.containsKey(julianProduct.getId())) {
                log.info(JULIAN_LOG_PREFIX + "새로운 상품 등장" + julianProduct);
                newJulianProductList.add(julianProduct);
            }
        }

        return newJulianProductList;
    }

}

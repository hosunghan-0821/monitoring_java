package com.example.monitor.monitoring.zente;

import chrome.ChromeDriverTool;
import com.example.monitor.Util.RandomUtil;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import com.example.monitor.monitoring.global.IMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.discord.DiscordBot;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZenteMonitorCore implements IMonitorService {

    private final DiscordBot discordBot;

    @Override
    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        loadData(driver, wait, ZenteFindString.brandUrlList);
    }

    @Override
    public void runFindProductLogic(ChromeDriverTool chromeDriverTool) {

    }

    @Override
    public void login(WebDriver driver, WebDriverWait wait) {

        //need not login
    }

    public void loadData(ChromeDriver driver, WebDriverWait wait, String[] brandUrlList) {

        int randomSec = RandomUtil.getRandomSec(10, 30);

        try {
            Thread.sleep(randomSec * 1000L);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < brandUrlList.length; i++) {

            try {
                driver.get(brandUrlList[i]);
                Thread.sleep(randomSec * 1000L);


                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='searchedItemDisplay']//ul//li")));

                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.scrollBy(0, document.body.scrollHeight / 2)", "");

                Thread.sleep(randomSec * 1000);

                List<WebElement> elements = driver.findElements(By.xpath("//div[@id='searchedItemDisplay']//ul//li[@class='jt-goods-list-elem']"));
                System.out.println(elements.size());

                for (WebElement element : elements) {

                    WebElement infoRoot = element.findElement(By.xpath(".//ul[@class='jt-goods-item-info']"));

                    WebElement brandElement = infoRoot.findElement(By.xpath(".//li[@class='goods-brand']"));
                    WebElement productElement = infoRoot.findElement(By.xpath(".//li[@class='goods-name ellipsis']"));

                    WebElement consumerPriceElement = infoRoot.findElement(By.xpath(".//li[@class='price consumer-price']"));

                    WebElement priceElement = infoRoot.findElement(By.xpath(".//li[@class='price']"));

                    ZenteProduct zenteProduct = ZenteProduct.builder()
                            .brandName(brandElement.getText())
                            .price(priceElement.getText())
                            .name(productElement.getText())
                            .salesPrevPrice(consumerPriceElement.getText())
                            .build();
                    System.out.println(zenteProduct.toString());
                }

                Thread.sleep(randomSec * 1000L);

            } catch (InterruptedException e) {
                e.printStackTrace();
                log.error("ERROR" + e.getMessage());
            }
        }
    }
}

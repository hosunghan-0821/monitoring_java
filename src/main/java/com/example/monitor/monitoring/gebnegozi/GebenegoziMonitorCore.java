package com.example.monitor.monitoring.gebnegozi;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.monitor.chrome.ChromeDriverTool;
import com.example.monitor.file.ProductFileWriter;
import com.example.monitor.infra.converter.controller.IConverterFacade;
import com.example.monitor.infra.discord.DiscordBot;
import com.example.monitor.infra.s3.S3UploaderService;
import com.example.monitor.monitoring.dobulef.DoubleFBrandHashData;
import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;
import static com.example.monitor.monitoring.gebnegozi.GebenegoziProdcutFindString.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class GebenegoziMonitorCore {


    private final DiscordBot discordBot;

    @Getter
    private final GebenegoziBrandHashData gebenegoziBrandHashData;

    @Value("${gebenegozi.user.id}")
    private String userId;

    @Value("${gebenegozi.user.pw}")
    private String userPw;


    private final IConverterFacade iConverterFacade;

    private final ProductFileWriter productFileWriter;

    private final S3UploaderService s3UploaderService;

    private final RestTemplate restTemplate;


    public void runLoadLogic(ChromeDriverTool chromeDriverTool) {

        ChromeDriver driver = chromeDriverTool.getChromeDriver();
        WebDriverWait wait = chromeDriverTool.getWebDriverWait();

        login(driver, wait);
        loadData(driver, wait, GebenegoziProdcutFindString.GEBE_URL_LIST);

        chromeDriverTool.isLoadData(true);
    }


    public void login(ChromeDriver driver, WebDriverWait wait) {
        //로그인페이지 로그인
        driver.get("http://93.46.41.5:1995/login");

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("login error");
        }


        WebElement loginInput = driver.findElement(By.id("username"));
        loginInput.sendKeys(userId);
        WebElement passwordInput = driver.findElement(By.id("password"));
        passwordInput.sendKeys(userPw);
        WebElement submitButton = driver.findElement(By.id("doLogin"));

        submitButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("containerLineeModal")));

    }

    public void loadData(ChromeDriver driver, WebDriverWait wait, String[] urlList) {
        //데이터 정보조회

        for (int i = 0; i < urlList.length; i++) {
            String url = urlList[i];
            driver.get(url);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='card-body']")));
            String pattern = "\\S"; // 공백이 아닌 문자에 대한 패턴
            Pattern p = Pattern.compile(pattern);
            try {
                wait.until(ExpectedConditions.textMatches(By.xpath("//div[@class='row title font-italic text-capitalize artPrezzi']//div[@class='col-5']"), p));
            } catch (Exception e) {
                log.error("**확인요망** 상품없음 url = " + url);
                continue;
            }

            WebElement pageElement = driver.findElement(By.xpath("//a[@class='page-link']"));
            System.out.println(pageElement.getText());
            int finalPage = pageElement.getText().charAt(pageElement.getText().length() - 1) - '0';
            System.out.println("막페이지 : " + finalPage);

            for (int j = 1; j <= finalPage; j++) {
                driver.get(url);

                wait.until(ExpectedConditions.textMatches(By.xpath("//div[@class='row title font-italic text-capitalize artPrezzi']//div[@class='col-5']"), p));

                List<WebElement> elements = driver.findElements(By.xpath("//div[@class='shopping-cart mb-3']"));


                for (WebElement productElement : elements) {

                    //이미지정보
                    try {
                        //String imageSrc = findImageSrc(driver, wait, productElement);
//                        String cookie = driver.manage().getCookieNamed("JSESSIONID").getValue();
//                        downloadImage(imageSrc, cookie);

                        //상품정보
                        WebElement infoElement = productElement.findElement(By.xpath(".//div[@class='row title font-italic text-capitalize artPrezzi']"));
                        List<WebElement> dataList = infoElement.findElements(By.xpath(".//div[@class='col-5']"));
                        List<WebElement> madeByList = infoElement.findElements(By.xpath(".//div[@class='col-3']"));
                        WebElement priceInfo = infoElement.findElement(By.xpath(".//div[@class='col-3']//span"));

                        String brand = dataList.get(0).getText();
                        String sku = dataList.get(1).getText();
                        String season = dataList.get(2).getText();
                        String finalPrice = priceInfo.getText();
                        String madeBy = madeByList.get(1).getText();

                        //TO-DO Product 만들고 저장 및 Discord 발송
                        log.info("{},\t{},\t{},\t{},\t{},\t{}", brand, sku, season, finalPrice, madeBy);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("상품 정보 조회 오류 url =" + url);
                        continue;
                    }

                }
                //url 변경
                url = url.replace("n=" + j, "n=" + (j + 1));
            }
        }

    }

    private String findImageSrc(ChromeDriver driver, WebDriverWait wait, WebElement productElement) {

        WebElement imageElement = productElement.findElement(By.xpath(".//img[@class='zoom lozad']"));
        Actions actions = new Actions(driver);
        actions.moveToElement(productElement);
        actions.perform();

        wait.until(ExpectedConditions.attributeToBeNotEmpty(imageElement, "src"));
        imageElement = productElement.findElement(By.xpath(".//img[@class='zoom lozad']"));
        String imageSrc = imageElement.getAttribute("src");
        log.info(imageSrc);


        return imageSrc;
    }

    public File downloadImageOrNull(String imageSrcUrl, String cookie) {

        assert (imageSrcUrl != null);
        assert (cookie != null);

        HttpHeaders headers = new HttpHeaders();
        headers.add(COOKIE, SESSION_COOKIE_KEY + "=" + cookie);

        // 이미지를 바이트 배열로 다운로드
        String fileName = getFileNameByTimeFormat();
        File file = new File(fileName);

        try {
            ResponseEntity<byte[]> response = restTemplate.exchange(imageSrcUrl, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);
            byte[] imageData = response.getBody();
            assert (imageData != null);
            Path path = file.toPath();
            // 이미지 데이터를 파일로 저장
            Files.write(path, imageData);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(GEBENE_LOG_PREFIX + "이미지 다운로드 실패");
            return null;
        }

        return file;
        //내 서버에 이미지 올립니다.
//        s3UploaderService.uploadImage(file, fileName);

    }

    @NotNull
    private static String getFileNameByTimeFormat() {
        // 현재 날짜와 시간 가져오기
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        return currentDateTime.format(formatter) + ".jpg";
    }


}

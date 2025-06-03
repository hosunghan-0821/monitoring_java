package com.example.monitor.unit.doublef;


import com.example.monitor.monitoring.dobulef.DoubleFLocator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class DoubleFLocatorValidTest {

    private static final String RESOURCES_PREFIX = "src/test/resources/doublef/";

    @Test
    @DisplayName("Login 페이지 Locator 확인 [ID / PASSWORD / COOKIE]")
    void DOUBLE_F_로그인페이지_Locator_유효성검사() throws Exception {
        // 1) 파일 읽어서 JSoup Document 만들기
        Path file = Path.of(RESOURCES_PREFIX + "login-snapshot.html");
        String html = Files.readString(file);
        Document doc = Jsoup.parse(html, "UTF-8");

        /* ─────────────────────────────
           2) 원하는 요소/값 검증 예시
           ───────────────────────────── */

        // (1) 로그인 및 패스워드 존재하는지 확인
        Element email = doc.getElementById(DoubleFLocator.DF_ID.selector());
        assertThat(email).isNotNull();

        Element pass = doc.getElementById(DoubleFLocator.DF_PASS.selector());
        assertThat(pass).isNotNull();

        Element cookie = doc.getElementById(DoubleFLocator.DF_COOKIE.selector());
        assertThat(cookie).isNotNull();

    }

    @Test
    @DisplayName("상품 페이지 Locator 확인 [SKU LINK / TOP,CHILD CONTAINER]")
    void DOUBLE_F_상품_Locator_유효성검사() throws Exception {
        // 1) 파일 읽어서 JSoup Document 만들기
        Path file = Path.of(RESOURCES_PREFIX + "product-snapshot.html");
        String html = Files.readString(file);
        Document doc = Jsoup.parse(html, "UTF-8");

        /* ─────────────────────────────
           2) 원하는 요소/값 검증 예시
           ───────────────────────────── */

        // (1) 상품 상단 Locator
        Elements elements = doc.selectXpath(DoubleFLocator.DF_PRODUCT_TOP_CONTAINER.selector());
        assertThat(elements).isNotNull();
        assertThat(elements).isNotEmpty();

        Elements childProducts = doc.selectXpath(DoubleFLocator.DF_PRODUCT_CHILD_CONTAINER.selector());
        assertThat(childProducts).isNotNull();
        assertThat(childProducts).isNotEmpty();

        Elements productLinks = doc.selectXpath(DoubleFLocator.DF_PRODUCT_LINK.selector());
        assertThat(productLinks).isNotNull();
        assertThat(productLinks).isNotEmpty();

        for (Element productLink : productLinks) {
            Elements hrefElements = productLink.getElementsByAttribute("href");
            assertThat(hrefElements).isNotEmpty();
            String url = hrefElements.get(0).attr("href");
            assertThat(url.startsWith("https://www.thedoublef.com/")).isEqualTo(true);
        }

        Elements discountInfos = doc.selectXpath(DoubleFLocator.DF_DISCOUNT_INFO.selector());
        assertThat(discountInfos).isNotNull();
        assertThat(discountInfos).isNotEmpty();

    }


}

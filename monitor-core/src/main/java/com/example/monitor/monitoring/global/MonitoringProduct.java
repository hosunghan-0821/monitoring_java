package com.example.monitor.monitoring.global;

import com.example.monitor.file.ProductFileInfo;
import com.example.monitor.infra.converter.dto.ConvertProduct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@SuperBuilder
@Getter
@ToString
public class MonitoringProduct {

    protected String id;
    protected String name;
    protected String brandName; // converting Rule 사용
    protected String price;
    protected String productLink;
    protected String sku;
    protected String colorCode;
    protected String madeBy;
    @Setter
    protected String monitoringSite;  // converting Rule 사용
    protected double doublePrice;


    protected static final String DISCORD_NEW_PRODUCT_MESSAGE_FORMAT =
            "상품 이름 : %s \n " +
            "상품 시즌 : %s \n " +
            "상품 품번 : %s \n" +
            "상품 브랜드 : %s \n" +
            "상품 카테고리 : %s \n" +
            "원산지: %s \n\n"+
            "최종 가격 : %s \n" +
            "할인율 : %s \n" +
            "할인전 가격 : %s \n\n" +
            "엑셀 색 유무 %s \n\n " +
            "기타 정보: %s\n\n";

    protected static final String DISCORD_NEW_PRODUCT_MESSAGE_FORMAT_DOUBLE_F =
            "상품 이름 : %s \n " +
                    "상품 시즌 : %s \n " +
                    "상품 품번 : %s \n" +
                    "상품 브랜드 : %s \n" +
                    "상품 카테고리 : %s \n" +
                    "원산지: %s \n\n"+
                    "최종 가격 : %s \n" +
                    "할인율 : %s \n" +
                    "할인전 가격 : %s \n\n" +
                    "엑셀 색 유무 %s \n\n " +
                    "Extra 할인: %s\n\n";

    protected static final String DISCORD_DISCOUNT_CHANGE_MESSAGE_FORMAT =
                    "상품 이름 : %s \n " +
                    "상품 시즌 : %s \n " +
                    "상품 품번 : %s \n" +
                    "상품 브랜드 : %s \n" +
                    "상품 카테고리 : %s \n" +
                    "원산지: %s \n\n"+
                    "최종 가격 : %s \n" +
                    "이전 할인율 : %s \n" +
                    "현재 할인율 : %s \n" +
                    "할인전 가격 : %s \n\n" +
                    "엑셀 색 유무 %s \n\n " +
                    "기타 정보: %s\n\n";

    protected static final String DISCORD_DISCOUNT_CHANGE_MESSAGE_FORMAT_DOUBLE_F =
            "상품 이름 : %s \n " +
                    "상품 시즌 : %s \n " +
                    "상품 품번 : %s \n" +
                    "상품 브랜드 : %s \n" +
                    "상품 카테고리 : %s \n" +
                    "원산지: %s \n\n"+
                    "최종 가격 : %s \n" +
                    "이전 할인율 : %s \n" +
                    "현재 할인율 : %s \n" +
                    "할인전 가격 : %s \n\n" +
                    "엑셀 색 유무 %s \n\n " +
                    "Extra 이전 할인: %s\n" +
                    "Extra 변경 할인 %s\n\n";


    public ConvertProduct changeToConvertProduct(String monitoringSite) {
        return ConvertProduct.builder()
                .sku(this.sku)
                .brandName(this.brandName)
                .monitoringSite(monitoringSite)
                .inputPrice(this.doublePrice)
                .originPrice(this.price)
                .madeBy(this.madeBy)
                .productLink(this.productLink)
                .colorCode(this.colorCode)
                .build();
    }

    public ProductFileInfo changeToProductFileInfo(String monitoringSite, String detectedCause) {

        return ProductFileInfo.builder()
                .sku(sku)
                .brandName(brandName)
                .price(price)
                .madeBy(madeBy)
                .colorCode(colorCode)
                .detectedDate(detectedCause)
                .detectedDate(getDateTimeFormat())
                .monitoringSite(monitoringSite)
                .build();
    }

    private String getDateTimeFormat() {
        Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
        return formatter.format(now);

    }
}

package com.example.monitor.monitoring.global;

import com.example.monitor.file.ProductFileInfo;
import com.example.monitor.infra.converter.dto.ConvertProduct;
import lombok.Getter;
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
    protected String monitoringSite;  // converting Rule 사용
    protected double doublePrice;

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

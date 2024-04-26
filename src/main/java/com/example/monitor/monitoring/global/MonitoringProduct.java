package com.example.monitor.monitoring.global;

import com.example.monitor.infra.converter.dto.ConvertProduct;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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
                .madeBy(this.madeBy)
                .productLink(this.productLink)
                .colorCode(this.colorCode)
                .build();
    }
}

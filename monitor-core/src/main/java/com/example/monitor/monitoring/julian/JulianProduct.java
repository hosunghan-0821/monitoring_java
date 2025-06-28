package com.example.monitor.monitoring.julian;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class JulianProduct extends MonitoringProduct {


    private String imageUrl;

    @Setter
    private String category;

    @Setter
    private String gender;

    @Setter
    private String season;


    private String originPrice;


    private String finalPrice;


    private int salePercent;

    private String excelKeyInfo;


    public void setMadeBy(String madeBy) {
        this.madeBy = madeBy;
    }

    public void setMorePriceInfo(String originPrice, String finalPrice, int salePercent, String keyInfo) {
        this.originPrice = originPrice;
        this.finalPrice = finalPrice;
        this.salePercent = salePercent;
        this.excelKeyInfo = keyInfo;
        this.doublePrice = Double.parseDouble(finalPrice);

    }

    public void setKeyInfo(String exceptKeyInfo) {
        this.excelKeyInfo = exceptKeyInfo;
    }

    public String makeDiscordMessageDescription() {

        return String.format(DISCORD_NEW_PRODUCT_MESSAGE_FORMAT,
                this.name,
                this.season,
                this.sku,
                this.brandName,
                this.category,
                this.madeBy,
                this.finalPrice,
                this.salePercent,
                this.originPrice,
                null,
                this.excelKeyInfo
        );
    }
}

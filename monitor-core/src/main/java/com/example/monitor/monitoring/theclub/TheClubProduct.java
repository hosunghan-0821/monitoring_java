package com.example.monitor.monitoring.theclub;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class TheClubProduct extends MonitoringProduct {

    private String salePercent;
    private String detectedCause;
    private String imageUrl;
    private String season;
    private String originPrice;
    
    @Setter
    private String category;
    
    @Setter
    private String gender;

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setMadeBy(String madeBy) {
        this.madeBy = madeBy;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public void updateDetectedCause(String detectedCause) {
        this.detectedCause = detectedCause;
    }

    public void updateSalePercent(String salePercent) {
        this.salePercent = salePercent;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String makeDiscordMessageDescription() {
        return String.format(DISCORD_NEW_PRODUCT_MESSAGE_FORMAT,
                this.name,
                this.season,
                this.sku,
                this.brandName,
                this.category,
                this.madeBy,
                this.doublePrice,
                this.salePercent,
                this.originPrice,
                null,
                "TheClub 신상품"
        );
    }

    public String makeDiscordDiscountMessageDescription(String beforeSalesPercent) {
        return String.format(DISCORD_DISCOUNT_CHANGE_MESSAGE_FORMAT,
                this.name,
                this.season,
                this.sku,
                this.brandName,
                this.category,
                this.madeBy,
                this.doublePrice,
                beforeSalesPercent,
                this.salePercent,
                this.originPrice,
                null,
                "TheClub 할인 변경"
        );
    }
}
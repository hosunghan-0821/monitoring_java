package com.example.monitor.monitoring.dobulef;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class DoubleFProduct extends MonitoringProduct {

    private String discountPercentage;
    private String detectedCause;
    private String brandSex;

    public void updateMadeBy(String madeBy) {
        this.madeBy = madeBy;
    }

    public void updateDetectedCause(String detectedCause) {
        this.detectedCause = detectedCause;
    }

    public void updateDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String makeDiscordMessageDescription() {

        return String.format(DISCORD_NEW_PRODUCT_MESSAGE_FORMAT,
                this.name,
                null,
                this.sku,
                this.brandName,
                null,
                this.madeBy,
                this.price,
                this.discountPercentage,
                null,
                null,
                null
        );
    }

    public String makeDiscordDiscountMessageDescription(String beforeSalesPercent) {

        return String.format(DISCORD_DISCOUNT_CHANGE_MESSAGE_FORMAT,
                this.name,
                null,
                this.sku,
                this.brandName,
                null,
                this.madeBy,
                this.price,
                beforeSalesPercent,
                this.discountPercentage,
                null,
                null,
                null
        );
    }
}

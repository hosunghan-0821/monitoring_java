package com.example.monitor.monitoring.eic;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
@Setter
public class EicProduct extends MonitoringProduct {

    private String discountPercentage;
    private String detectedCause;
    private String imageSrc;

    public String makeDiscordMessageDescription() {

        return String.format(DISCORD_NEW_PRODUCT_MESSAGE_FORMAT,
                this.name,
                null,
                this.sku + this.colorCode,
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
                this.sku + this.colorCode,
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

    public void updateDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}

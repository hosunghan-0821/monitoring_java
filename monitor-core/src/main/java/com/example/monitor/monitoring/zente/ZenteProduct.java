package com.example.monitor.monitoring.zente;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class ZenteProduct extends MonitoringProduct {

    private String salesPrevPrice;
    private String category;

    public String makeDiscordMessageDescription() {

        return String.format(DISCORD_NEW_PRODUCT_MESSAGE_FORMAT,
                this.name,
                null,
                this.sku,
                this.brandName,
                null,
                this.madeBy,
                this.price,
                null,
                null,
                null,
                null
        );
    }

    public String makeDiscordDiscountMessageDescription(String beforePrice) {

        return String.format(DISCORD_DISCOUNT_CHANGE_MESSAGE_FORMAT,
                this.name,
                null,
                this.sku,
                this.brandName,
                this.category,
                null,
                this.price,
                null,
                null,
                beforePrice,
                null,
                null

        );
    }
}

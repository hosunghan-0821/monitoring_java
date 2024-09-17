package com.example.monitor.monitoring.vietti;


import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class ViettiProduct extends MonitoringProduct {

    private String discountPercentage;
    private String imageSrc;
    private String originPrice;

    public void updateSku(String sku) {
        this.sku = sku;
    }

    public void updateMadeBy(String madeBy) {
        this.madeBy = madeBy;
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
                this.originPrice,
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
                this.originPrice,
                null,
                null

        );
    }
}

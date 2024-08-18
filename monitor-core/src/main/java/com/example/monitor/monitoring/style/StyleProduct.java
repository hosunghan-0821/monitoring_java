package com.example.monitor.monitoring.style;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class StyleProduct extends MonitoringProduct {

    private String salePercent;
    private String detectedCause;
    private String imageUrl;
    private String season;

    private String originPrice;

    public void setSku(String sku) {
        this.sku=sku;
    }

    public void setMadeBy(String madeBy) {
        this.madeBy = madeBy;
    }

    public void setSeason(String season) {
        this.season = season;
    }


    public String makeDiscordMessageDescription() {

        return String.format(DISCORD_NEW_PRODUCT_MESSAGE_FORMAT,
                this.name,
                this.season,
                this.sku,
                this.brandName,
                null,
                this.madeBy,
                this.doublePrice,
                this.salePercent,
                this.originPrice,
                null,
                "최종 가격은 할인전 가격 할인율 * 0.95가 적용된 가격입니다."

        );
    }

    public String makeDiscordDiscountMessageDescription(String beforeSalesPercent) {

        return String.format(DISCORD_NEW_PRODUCT_MESSAGE_FORMAT,
                this.name,
                this.season,
                this.sku,
                this.brandName,
                null,
                this.madeBy,
                this.doublePrice,
                beforeSalesPercent,
                this.salePercent,
                this.originPrice,
                null,
                "최종 가격은 할인전 가격 할인율 * 0.95가 적용된 가격입니다."

        );
    }



}

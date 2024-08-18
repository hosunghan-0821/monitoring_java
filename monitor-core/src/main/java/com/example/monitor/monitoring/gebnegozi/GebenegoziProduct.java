package com.example.monitor.monitoring.gebnegozi;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class GebenegoziProduct extends MonitoringProduct {


    private String imageSrc;
    private String season;
    @Setter
    private String category;

    private String originPrice;
    private String finalPrice;
    private int salePercent;
    private boolean isColored;

    public void updateImageUrl(String imageSrc) {
        this.imageSrc = imageSrc;
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
                this.isColored,
                this.price
        );
    }
}

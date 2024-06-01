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

    private String wholeSaleOrigin;
    private String wholeSale;
    private int wholeSalePercent;
    private boolean isColored;

    public void updateImageUrl(String imageSrc) {
        this.imageSrc = imageSrc;
    }
}

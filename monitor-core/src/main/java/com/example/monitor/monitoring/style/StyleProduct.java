package com.example.monitor.monitoring.style;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class StyleProduct extends MonitoringProduct {

    private String discountPercentage;
    private String detectedCause;
    private String imageUrl;
    private String season;

    public void setSku(String sku) {
        this.sku=sku;
    }

    public void setMadeBy(String madeBy) {
        this.madeBy = madeBy;
    }

    public void setSeason(String season) {
        this.season = season;
    }

}

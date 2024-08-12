package com.example.monitor.monitoring.biffi;


import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class BiffiProduct extends MonitoringProduct {

    private String discountPercentage;
    private String detectedCause;
    private String imgUrl;

    public void updateMadeBy(String origin) {
        this.madeBy = origin;
    }
}

package com.example.monitor.monitoring.dobulef;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class DoubleFProduct {

    private String id;
    private String name;
    private String discountPercentage;
    private String brand;
    private String price;
    private String productLink;
    private String sku;
    private String colorCode;
    private String madeBy;
    private String detectedCause;


    public void updateMadeBy(String madeBy) {
        this.madeBy = madeBy;
    }

    public void updateDetectedCause(String detectedCause) {
        this.detectedCause = detectedCause;
    }
}

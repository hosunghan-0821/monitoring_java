package com.example.monitor.monitoring.dobulef;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Builder;
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
}

package com.example.monitor.monitoring.julian;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class JulianProduct extends MonitoringProduct {


    private String imageUrl;

    @Setter
    private String category;

    @Setter
    private String gender;

    @Setter
    private String season;


    private String wholeSaleOrigin;


    private String wholeSale;


    private int wholeSalePercent;

    private String wholeSaleKeyInfo;


    public void setMadeBy(String madeBy) {
        this.madeBy = madeBy;
    }

    public void setMorePriceInfo(String wholeSaleOrigin, String wholeSale, int wholeSalePercent, String wholeSaleKeyInfo) {
        this.wholeSaleOrigin = wholeSaleOrigin;
        this.wholeSale = wholeSale;
        this.wholeSalePercent = wholeSalePercent;
        this.wholeSaleKeyInfo = wholeSaleKeyInfo;
    }
}

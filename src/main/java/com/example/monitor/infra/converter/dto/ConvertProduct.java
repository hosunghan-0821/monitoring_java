package com.example.monitor.infra.converter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class ConvertProduct {

    private String productLink;
    private String monitoringSite;
    private String brandName;

    private String originSku;
    private String originColorCode;
    private String originPrice;
    //부띠끄
    private String sku;
    private String name;
    private String madeBy;
    private String colorCode;
    private String imgUrl;
    private double inputPrice; // 해외 떼오는 가격
    private String unit;
    private Boolean isFta;


    public void update(String finalSku, String finalColorCode ,String originSku, String originColorCode,boolean isFta) {
        this.sku = finalSku;
        this.colorCode = finalColorCode;
        this.originSku = originSku;
        this.originColorCode = originColorCode;
        this.isFta =isFta;
    }
}

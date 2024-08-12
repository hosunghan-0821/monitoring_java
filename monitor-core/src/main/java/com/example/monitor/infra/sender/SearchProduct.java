package com.example.monitor.infra.sender;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class SearchProduct {



    //부띠끄
    private String productLink;
    private String sku;
    private String name;
    private String madeBy;
    private String monitoringSite;
    private String colorCode;
    private String imgUrl;
    private double inputPrice; // 해외 떼오는 가격
    private String unit;
    private Boolean fta;

    private String originSku;
    private String originColorCode;
    private String originPrice;



}

package com.example.monitor.monitoring.biffi;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class BiffiProduct {

    private String id;
    private String name;
    private String discountPercentage;
    private String brand;
    private String price;
    private String imgUrl;
    private String sku;
    private String colorCode;
}

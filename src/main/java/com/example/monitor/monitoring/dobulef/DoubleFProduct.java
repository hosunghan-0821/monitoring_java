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
    private String SKU;
    private String colorCode;

}

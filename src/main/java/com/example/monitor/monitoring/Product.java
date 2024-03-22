package com.example.monitor.monitoring;

import lombok.*;

@Builder
@Getter
@Setter(AccessLevel.PROTECTED)
@ToString
public class Product {
    private String name;
    private String Id;
    private String imageSrc;
    private String price;
}

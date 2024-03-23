package com.example.monitor.monitoring;

import lombok.*;

@Builder
@Getter
@ToString
public class Product {
    private String name;
    private String Id;
    private String imageSrc;
    private String price;
    @Setter
    private String category;

}

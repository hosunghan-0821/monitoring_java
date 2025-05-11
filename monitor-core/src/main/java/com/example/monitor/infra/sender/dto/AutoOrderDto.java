package com.example.monitor.infra.sender.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AutoOrderDto {

    private String boutique;
    private double price;
    private String sku;
    private String id;
    private String productLink;
}

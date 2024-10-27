package com.example.monitor.monitoring.antonioli;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
@ToString(callSuper = true)
public class AntonioliProduct extends MonitoringProduct {

    private String discountPercentage;
    private String imgUrl;

}

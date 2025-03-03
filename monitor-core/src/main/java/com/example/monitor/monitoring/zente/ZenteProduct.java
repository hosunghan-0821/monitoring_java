package com.example.monitor.monitoring.zente;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class ZenteProduct extends MonitoringProduct {

    private String salesPrevPrice;
}

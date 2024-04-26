package com.example.monitor.monitoring.global;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
public class MonitoringProduct {

    protected String id;
    protected String name;
    protected String brand;
    protected String price;
    protected String productLink;
    protected String sku;
    protected String colorCode;
    protected String madeBy;
    protected String monitoringSite;
}

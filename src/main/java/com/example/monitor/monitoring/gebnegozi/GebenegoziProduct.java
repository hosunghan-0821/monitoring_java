package com.example.monitor.monitoring.gebnegozi;

import com.example.monitor.monitoring.global.MonitoringProduct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class GebenegoziProduct extends MonitoringProduct {


    @Setter
    private String category;

}

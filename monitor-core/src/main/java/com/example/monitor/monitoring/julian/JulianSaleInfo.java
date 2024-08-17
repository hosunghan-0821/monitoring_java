package com.example.monitor.monitoring.julian;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class JulianSaleInfo {
    private String brandName;
    private int salesPercent;
    private String season;
    private String category;
}

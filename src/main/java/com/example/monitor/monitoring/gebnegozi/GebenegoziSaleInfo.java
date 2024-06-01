package com.example.monitor.monitoring.gebnegozi;


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
public class GebenegoziSaleInfo {

    private String brandName;
    private int salesPercent;
    private boolean isColored;
    private String season;
    private String category;
}

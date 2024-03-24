package com.example.monitor.monitoring.julian;

import lombok.*;

@Builder
@Getter
@ToString
public class JulianProduct {
    private String name;
    private String Id;
    private String imageSrc;
    private String price;
    @Setter
    private String category;

}

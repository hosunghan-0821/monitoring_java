package com.example.monitor.file;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
@Builder
public class ProductFileInfo {

    private String id;
    private String name;
    private String brandName; // converting Rule 사용
    private String price;
    private String productLink;
    private String sku;
    private String colorCode;
    private String madeBy;
    private String monitoringSite;  // converting Rule 사용
    private String detectedCause;
    private String detectedDate;
}

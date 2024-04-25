package com.example.monitor.infra.sender;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Builder
@Getter
@ToString
public class SearchProduct implements Serializable {

    static final long serialVersionUID = 1L;

    //부띠끄
    private String sku;
    private String name;
    private String madeBy;
    private String monitoringSite;
    private String colorCode;
    private String imgUrl;
    private double inputPrice; // 해외 떼오는 가격
    private String unit;
    private boolean isFta;



}

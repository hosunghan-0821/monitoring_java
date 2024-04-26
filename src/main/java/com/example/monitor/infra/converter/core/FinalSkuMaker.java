package com.example.monitor.infra.converter.core;

import org.springframework.stereotype.Component;

@Component
public class FinalSkuMaker {
    public String convertFinal(String convertedSku, String convertedColorCode, String endRule) {

        assert (endRule != null);
        return switch (endRule) {
            case "sku-only" -> convertedSku;
            default -> convertedSku + convertedColorCode;
        };
    }
}

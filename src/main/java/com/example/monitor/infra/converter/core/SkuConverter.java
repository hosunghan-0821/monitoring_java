package com.example.monitor.infra.converter.core;

import org.springframework.stereotype.Component;

@Component
public class SkuConverter {

    public String convertSku(String sku, String convertRule) {

        assert (convertRule != null && sku != null);

        return switch (convertRule) {
            case "default" -> sku;
            case "delete-final-english" -> deleteFinalEnglish(sku);
            case "rule1" -> logic1(sku);
            default -> sku;
        };

    }

    private String deleteFinalEnglish(String sku) {

        int length = sku.length();
        int finalIndex =  sku.length() - 1;
        for (int i = length - 1; i >= 0; i--) {
            char word = sku.charAt(i);
            if ((word >='A' && word <= 'Z') || (word >='a' && word<='z')) {
                finalIndex = i;
            } else {
                break;
            }
        }
        return sku.substring(0,finalIndex);
    }

    public String logic1(String sku) {
        return sku.substring(0, sku.length() - 1);

    }


}

package com.example.monitor.infra.converter.core;

import org.springframework.stereotype.Component;

@Component
public class ColorCodeConverter {


    public String convertColorCode(String colorCode, String convertRule) {

        assert (convertRule != null);

        return switch (convertRule) {
            case "rule1" -> logic1(colorCode);
            default -> colorCode;
        };
    }

    private String logic1(String colorCode) {
        return colorCode;
    }
}

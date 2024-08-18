package com.example.monitor.monitoring.style;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


@Component
public class StyleBrandHashData {

    private final HashMap<String, HashMap<String, StyleProduct>> styleHashMap;

    @Getter
    private final HashSet<String> productKeySet;

    public StyleBrandHashData() {
        styleHashMap = new HashMap<>();
        for (String brandName : StyleFindString.STYLE_BRAND_NAME_LIST) {
            styleHashMap.put(brandName, new HashMap<>());
        }
        productKeySet = new HashSet<>();
    }

    public Map<String, StyleProduct> getBrandHashMap(String brandName) {
        return styleHashMap.get(brandName);
    }



}

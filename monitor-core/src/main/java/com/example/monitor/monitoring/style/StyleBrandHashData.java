package com.example.monitor.monitoring.style;

import com.example.monitor.monitoring.dobulef.DoubleFProduct;
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
        for (String brandName : StyleFindString.brandNameList) {
            styleHashMap.put(brandName, new HashMap<>());
        }
        productKeySet = new HashSet<>();
    }

    public Map<String, StyleProduct> getBrandHashMap(String brandName) {
        return styleHashMap.get(brandName);
    }



}

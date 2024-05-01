package com.example.monitor.monitoring.julian;


import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.example.monitor.monitoring.julian.JulianFindString.JULIAN_MONITORING_SITE;

@Component
public class JulianBrandHashData {

    private final HashMap<String, HashMap<String, JulianProduct>> julianHashMap;


    @Getter
    private final HashSet<String> productKeySet;

    public JulianBrandHashData() {
        julianHashMap = new HashMap<>();
        for (String brandName : JULIAN_MONITORING_SITE) {
            julianHashMap.put(brandName, new HashMap<>());
        }

        productKeySet = new HashSet<>();
    }

    public HashMap<String, JulianProduct> getBrandHashMap(String brandName) {
        return julianHashMap.get(brandName);
    }
}

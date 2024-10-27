package com.example.monitor.monitoring.antonioli;

import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.example.monitor.monitoring.antonioli.AntonioliFindString.MANS_PREFIX;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.WOMANS_PREFIX;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.manBrandNameList;
import static com.example.monitor.monitoring.antonioli.AntonioliFindString.womanBrandNameList;


@Component
public class AntonioliBrandHashData {

    private final HashMap<String, HashMap<String, AntonioliProduct>> antonioliHashMap;

    @Getter
    private final HashSet<String> productKeySet;

    public AntonioliBrandHashData() {
        antonioliHashMap = new HashMap<>();
        for (String brandName : manBrandNameList) {
            antonioliHashMap.put(MANS_PREFIX + brandName, new HashMap<>());
        }
        for (String brandName : womanBrandNameList) {
            antonioliHashMap.put(WOMANS_PREFIX + brandName, new HashMap<>());
        }

        productKeySet = new HashSet<>();
    }

    public Map<String, AntonioliProduct> getBrandHashMap(String sexPrefix, String brandName) {
        return antonioliHashMap.get(sexPrefix + brandName);
    }
}

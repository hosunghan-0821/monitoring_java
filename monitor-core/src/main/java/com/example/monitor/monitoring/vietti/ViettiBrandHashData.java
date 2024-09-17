package com.example.monitor.monitoring.vietti;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


@Component
public class ViettiBrandHashData {

    private final HashMap<String, HashMap<String, ViettiProduct>> viettiHashMap;


    @Getter
    private final HashSet<String> productKeySet;

    public ViettiBrandHashData() {
        viettiHashMap = new HashMap<>();

        for (String brandName : ViettiFindString.VIETTI_BRAND_NAME_LIST) {
            viettiHashMap.put(brandName, new HashMap<>());
        }
        productKeySet = new HashSet<>();
    }

    public Map<String, ViettiProduct> getBrandHashMap(String brandName) {
        return viettiHashMap.get(brandName);
    }
}

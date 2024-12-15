package com.example.monitor.monitoring.eic;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class EicBrandHashData {

    private final HashMap<String, HashMap<String, EicProduct>> eicProductHashMap;

    @Getter
    private final HashSet<String> productKeySet;

    public EicBrandHashData() {
        eicProductHashMap = new HashMap<>();

        for (String brandName : EicFindString.brandNameList) {
            eicProductHashMap.put(brandName, new HashMap<>());
        }

        productKeySet = new HashSet<>();
    }


    public Map<String, EicProduct> getBrandHashMap( String brandName) {
        return eicProductHashMap.get(brandName);
    }
}

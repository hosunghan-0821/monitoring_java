package com.example.monitor.monitoring.biffi;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI_BRAND_NAME_LIST;

@Component
public class BiffiBrandHashData {

    private final HashMap<String, HashMap<String, BiffiProduct>> biffiHashMap;


    @Getter
    private final HashSet<String> productKeySet;

    public BiffiBrandHashData() {
        biffiHashMap = new HashMap<>();
        for (String brandName : BIFFI_BRAND_NAME_LIST) {
            biffiHashMap.put(brandName, new HashMap<>());
        }

        productKeySet = new HashSet<>();
    }

    public Map<String, BiffiProduct> getBrandHashMap(String brandName) {
        return biffiHashMap.get(brandName);
    }
}

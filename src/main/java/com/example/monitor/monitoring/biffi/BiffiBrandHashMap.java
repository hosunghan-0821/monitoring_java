package com.example.monitor.monitoring.biffi;

import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.example.monitor.monitoring.biffi.BiffiFindString.BIFFI_BRAND_NAME_LIST;

@Component
public class BiffiBrandHashMap {

    private final HashMap<String, HashMap<String, BiffiProduct>> biffiHashMap;

    public BiffiBrandHashMap() {
        biffiHashMap = new HashMap<>();
        for (String brandName : BIFFI_BRAND_NAME_LIST) {
            biffiHashMap.put(brandName, new HashMap<>());
        }
    }

    public Map<String, BiffiProduct> getBrandHashMap(String brandName) {
        return biffiHashMap.get(brandName);
    }
}

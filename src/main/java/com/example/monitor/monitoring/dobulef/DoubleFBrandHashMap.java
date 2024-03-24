package com.example.monitor.monitoring.dobulef;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DoubleFBrandHashMap {

    private final HashMap<String, HashMap<String, DoubleFProduct>> doubleFHashMap = new HashMap<>();

    public Map<String, DoubleFProduct> getBrandHashMap(String brandName) {
        return doubleFHashMap.get(brandName);
    }

    public HashMap<String, HashMap<String, DoubleFProduct>> getAllHashMap() {
        return doubleFHashMap;
    }


}

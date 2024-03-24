package com.example.monitor.monitoring.dobulef;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.example.monitor.monitoring.dobulef.DoubleFFindString.manBrandNameList;
import static com.example.monitor.monitoring.dobulef.DoubleFFindString.womanBrandNameList;

@Component
public class DoubleFBrandHashMap {

    private final HashMap<String, HashMap<String, DoubleFProduct>> doubleFHashMap;

    public DoubleFBrandHashMap() {
        doubleFHashMap = new HashMap<>();
        for (String brandName : manBrandNameList) {
            doubleFHashMap.put(brandName, new HashMap<>());
        }
        for (String brandName : womanBrandNameList) {
            doubleFHashMap.put(brandName, new HashMap<>());
        }
    }

    public Map<String, DoubleFProduct> getBrandHashMap(String brandName) {
        return doubleFHashMap.get(brandName);
    }


}

package com.example.monitor.monitoring.dobulef;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.example.monitor.monitoring.dobulef.DoubleFFindString.*;

@Component
public class DoubleFBrandHashData {

    private final HashMap<String, HashMap<String, DoubleFProduct>> doubleFHashMap;

    @Getter
    private final HashSet<String> productKeySet;

    public DoubleFBrandHashData() {
        doubleFHashMap = new HashMap<>();
        for (String brandName : manBrandNameList) {
            doubleFHashMap.put(MANS_PREFIX + brandName, new HashMap<>());
        }
        for (String brandName : womanBrandNameList) {
            doubleFHashMap.put(WOMANS_PREFIX + brandName, new HashMap<>());
        }

        productKeySet = new HashSet<>();
    }

    public Map<String, DoubleFProduct> getBrandHashMap(String sexPrefix, String brandName) {
        return doubleFHashMap.get(sexPrefix + brandName);
    }


}

package com.example.monitor.monitoring.zente;

import com.example.monitor.monitoring.dobulef.DoubleFProduct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.example.monitor.monitoring.zente.ZenteFindString.ZENTE_URL_INFO;

@Component
public class ZenteBrandHashData {

    private final HashMap<String, HashMap<String, ZenteProduct>> zenteHashMap;

    @Getter
    private final HashSet<String> productKeySet;

    public ZenteBrandHashData() {
        zenteHashMap = new HashMap<>();
        productKeySet = new HashSet<>();

        String[][] zenteUrlInfos = ZENTE_URL_INFO;

        for (String[] zenteUrlInfo : zenteUrlInfos) {
            String key = zenteUrlInfo[0] + zenteUrlInfo[1];
            zenteHashMap.put(key, new HashMap<>());
        }

    }

    public Map<String, ZenteProduct> getBrandHashMap(String brand, String category) {
        return zenteHashMap.get(brand + category);
    }
}

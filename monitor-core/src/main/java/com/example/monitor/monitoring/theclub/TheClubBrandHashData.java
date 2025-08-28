package com.example.monitor.monitoring.theclub;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.example.monitor.monitoring.theclub.TheClubFindString.THE_CLUB;
import static com.example.monitor.monitoring.theclub.TheClubFindString.THE_CLUB_BRAND_NAME_LIST;

@Slf4j
@Component
public class TheClubBrandHashData {

    private final HashMap<String, Map<String, TheClubProduct>> theClubHashMap;

    @Getter
    private final HashSet<String> productKeySet;

    public TheClubBrandHashData() {
        theClubHashMap = new HashMap<>();
        
        for (String brandName : THE_CLUB_BRAND_NAME_LIST) {
            theClubHashMap.put(brandName, new HashMap<>());
        }
        
        productKeySet = new HashSet<>();
    }

    public Map<String, TheClubProduct> getBrandHashMap(String brandName) {
        return theClubHashMap.get(brandName);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initTheClubData() {
        MDC.put("threadName", THE_CLUB);
        log.info("TheClub 모니터링 데이터 초기화 시작");
        
        // 필요한 경우 추가 초기화 로직 구현
        
        log.info("TheClub 모니터링 데이터 초기화 완료");
    }
}
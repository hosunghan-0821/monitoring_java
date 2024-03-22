package com.example.monitor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@Component
public class MonitorHashMap {

    private final HashMap<String, Product> productHashMap = new HashMap<>();

}

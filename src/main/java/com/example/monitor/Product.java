package com.example.monitor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter(AccessLevel.PROTECTED)
public class Product {
    private String name;
    private String Id;
    private String imageSrc;
}

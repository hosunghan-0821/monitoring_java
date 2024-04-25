package com.example.monitor.infra.sender;

import lombok.*;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {

    private String monitoringSite;
    private List<SearchProduct> data;
}

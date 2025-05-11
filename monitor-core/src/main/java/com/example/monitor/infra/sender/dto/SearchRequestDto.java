package com.example.monitor.infra.sender.dto;

import com.example.monitor.infra.sender.dto.SearchProduct;
import lombok.*;

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

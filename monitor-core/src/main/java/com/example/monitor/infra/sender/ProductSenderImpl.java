package com.example.monitor.infra.sender;


import com.example.monitor.infra.sender.dto.AutoOrderDto;
import com.example.monitor.infra.sender.dto.SearchProduct;
import com.example.monitor.infra.sender.dto.SearchRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;


import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSenderImpl implements ProductSender {

    @Value("${search.api}")
    private String searchUrl;

    @Value("${auto.order.api}")
    private String autoOrderUrl;

    @Value("${auto.order.bulk.api}")
    private String autoOrderBulkUrl;

    private final RestTemplate rt;

    private final ObjectMapper objectMapper;

    @Override
    public void sendToSearchServer(List<SearchProduct> searchProductList) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        SearchRequestDto searchRequestDto = SearchRequestDto.builder()
                .data(searchProductList)
                .monitoringSite(searchProductList.get(0).getMonitoringSite())
                .build();

        String body = null;
        try {
            body = objectMapper.writeValueAsString(searchRequestDto);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("json parsing Error");
            return;
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = rt
                .exchange(searchUrl, HttpMethod.POST, requestEntity, String.class);

        log.info(response.getStatusCode().toString());

    }

    @Async
    @Override
    public void sendToAutoOrderServer(AutoOrderDto autoOrderDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = null;
        try {
            body = objectMapper.writeValueAsString(autoOrderDto);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("json parsing Error");
            return;
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = rt
                .exchange(autoOrderUrl, HttpMethod.POST, requestEntity, String.class);

        log.info(response.getStatusCode().toString());
    }

    @Async
    @Override
    public void sendToAutoOrderServer(List<AutoOrderDto> autoOrderDtos) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = null;
        try {
            body = objectMapper.writeValueAsString(autoOrderDtos);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("json parsing Error");
            return;
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = rt
                .exchange(autoOrderBulkUrl, HttpMethod.POST, requestEntity, String.class);

        log.info(response.getStatusCode().toString());
    }


}

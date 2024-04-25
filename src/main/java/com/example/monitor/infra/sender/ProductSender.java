package com.example.monitor.infra.sender;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;


import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductSender {

    @Value("${search.api}")
    private String searchUrl;


    private final RestTemplate rt;

    private final ObjectMapper objectMapper;

    public void sendToSearchServer(List<SearchProduct> searchProductList) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        SearchRequestDto searchRequestDto = SearchRequestDto.builder()
                .data(searchProductList)
                .monitoringSite("JULIAN")
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
}

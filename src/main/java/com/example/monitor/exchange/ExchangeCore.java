package com.example.monitor.exchange;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ExchangeCore {

    @Value("${exchange.api.key}")
    private String exchangeApiKey;

    private final RestTemplate request = new RestTemplate();

    public void     getExchangeRateInfo() {

        // HTTP 요청 시 사용할 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<String> responseEntity =
                request.exchange("https://www.koreaexim.go.kr/site/program/financial/exchangeJSON?authkey=" + exchangeApiKey + "&data=AP01", HttpMethod.GET, requestEntity, String.class);

        System.out.println(responseEntity.getBody());

    }


}

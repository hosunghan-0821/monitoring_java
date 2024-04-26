package com.example.monitor.infra.converter.core;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class SkuConverterTest {


    @InjectMocks
    private SkuConverter skuConverter;


    @Test
    @DisplayName("[Converter] : 제일 마지막 영어 빼기 테스트")
    void convertSku() {

        //given
        String sku = "aulwul56abc";
        String rule = "delete-final-english";

        //when
        String result = skuConverter.convertSku(sku, rule);

        System.out.println(result);
        //then
        assertThat(result).isEqualTo("aulwul56");

    }

    @Test
    @DisplayName("[Converter] : 제일 마지막 영어 빼기 테스트 2")
    void convertSkuTest2() {

        //given
        String sku = "aulwul56";
        String rule = "delete-final-english";

        //when
        String result = skuConverter.convertSku(sku, rule);

        System.out.println(result);
        //then
        assertThat(result).isEqualTo("aulwul56");

    }
}
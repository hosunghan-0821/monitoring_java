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

    @Test
    @DisplayName("[Converter] : 뒤에 숫자6개 빼기 / Burberry")
    void convertSkuTest3() {

        //given
        String sku = "8062374130362";
        String rule = "delete-final-six-char";

        //when
        String result = skuConverter.convertSku(sku, rule);

        System.out.println(result);
        //then
        assertThat(result).isEqualTo("8062374");

    }

    @Test
    @DisplayName("[Converter] : 뒤에 영어 1개남기기 / Canada Goose")
    void convertSkuTest4() {

        //given
        String sku = "2235LNY";
        String rule = "delete-final-english-without-one";

        //when
        String result = skuConverter.convertSku(sku, rule);

        System.out.println(result);
        //then
        assertThat(result).isEqualTo("2235L");

    }

    @Test
    @DisplayName("[Converter] : 앞에 두번째 영어전까지 빼기 / JACQUEMUS")
    void convertSkuTest5() {

        //given
        String sku = "21H213BA0073000";
        String rule = "delete-until-second-english-appear";

        //when
        String result = skuConverter.convertSku(sku, rule);

        System.out.println(result);
        //then
        assertThat(result).isEqualTo("BA0073000");

    }

    @Test
    @DisplayName("[Converter] :  멘뒤에 영어2개 빼기 / KENZO")
    void convertSkuTest6() {

        //given
        String sku = "FD55SW4404MFCO";
        String rule = "delete-final-two-english";

        //when
        String result = skuConverter.convertSku(sku, rule);

        System.out.println(result);
        //then
        assertThat(result).isEqualTo("FD55SW4404MF");

    }

    @Test
    @DisplayName("[Converter] : 뒤에 3글자 빼기 , 만약 멘앞에 NB붙어있으면 빼기 / New Balance")
    void convertSkuTest7() {

        //given
        String sku = "MR530CKSUE";
        String rule = "new-balance-rule";

        //when
        String result = skuConverter.convertSku(sku, rule);

        System.out.println(result);
        //then
        assertThat(result).isEqualTo("MR530CK");

    }


    @Test
    @DisplayName("[Converter] : 뒤에 3글자 빼기 , 만약 멘앞에 NB붙어있으면 빼기 / New Balance")
    void convertSkuTest8() {

        //given
        String sku = "NBMR530CKSUE";
        String rule = "new-balance-rule";

        //when
        String result = skuConverter.convertSku(sku, rule);

        System.out.println(result);
        //then
        assertThat(result).isEqualTo("MR530CK");

    }

}
package com.example.monitor.infra.converter.controller;

import com.example.monitor.infra.converter.config.BrandConverterRule;
import com.example.monitor.infra.converter.core.ColorCodeConverter;
import com.example.monitor.infra.converter.core.FinalSkuMaker;
import com.example.monitor.infra.converter.core.SkuConverter;
import com.example.monitor.infra.converter.dto.ConvertProduct;
import com.example.monitor.infra.sender.SearchProduct;
import com.example.monitor.monitoring.dobulef.DoubleFFindString;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConverterFacadeImplTest {

    @InjectMocks
    private ConverterFacadeImpl converterFacade;

    @Spy
    private BrandConverterRule brandConverterRule;

    @Spy
    private ColorCodeConverter colorCodeConverter;

    @Spy
    private SkuConverter skuConverter;

    @Spy
    private FinalSkuMaker finalSkuMaker;


    @Test
    @DisplayName("[Converter] : ami-paris 상품 Converting Test")
    void convertProduct() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("bfuts005726")
                .colorCode("0951")
                .brandName("ami-paris")
                .madeBy("italy")
                .build();

        List<ConvertProduct> convertProductList = new ArrayList<>();
        convertProductList.add(convertProduct);
        //when
        converterFacade.convertProduct(convertProductList);

        //then
        assertThat(convertProductList.get(0).getSku()).isEqualTo("bfuts0057260951");
        assertThat(convertProductList.get(0).getOriginSku()).isEqualTo("bfuts005726");
        assertThat(convertProductList.get(0).getOriginColorCode()).isEqualTo("0951");
        assertThat(convertProductList.get(0).getIsFta()).isEqualTo(true);
    }
    @Test
    @DisplayName("[Converter] : 상품 Convert Search Product Test")
    void changeToSearchProductList() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("bfuts005726")
                .colorCode("0951")
                .brandName("ami-paris")
                .madeBy("italy")
                .productLink("https://naver.com")
                .originSku("bfuts005726")
                .originColorCode("0951")
                .isFta(true)
                .build();

        List<ConvertProduct> convertProductList = new ArrayList<>();
        convertProductList.add(convertProduct);
        List<SearchProduct> searchProductList = new ArrayList<>();

        //when
        converterFacade.changeToSearchProductList(convertProductList, searchProductList);

        //then
        assertThat(searchProductList.get(0).getOriginColorCode()).isEqualTo("0951");
        assertThat(searchProductList.get(0).getProductLink()).isEqualTo("https://naver.com");
        assertThat(searchProductList.get(0).getFta()).isEqualTo(true);
        assertThat(searchProductList.get(0).getInputPrice()).isEqualTo(100.1);
        assertThat(searchProductList.get(0).getMadeBy()).isEqualTo("italy");
        assertThat(searchProductList.get(0).getSku()).isEqualTo("bfuts005726");
        assertThat(searchProductList.get(0).getMonitoringSite()).isEqualTo("doublef");
    }
}
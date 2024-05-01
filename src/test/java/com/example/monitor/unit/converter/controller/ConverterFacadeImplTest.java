package com.example.monitor.unit.converter.controller;

import com.example.monitor.infra.converter.config.BrandConverterRule;
import com.example.monitor.infra.converter.controller.ConverterFacadeImpl;
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
    void convertProductAmiParis() {
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
    @DisplayName("[Converter] : acne 상품 Converting Test")
    void convertProductAcne() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("AL0135CO")
                .colorCode("0951")
                .brandName("acne")
                .madeBy("italy")
                .build();

        List<ConvertProduct> convertProductList = new ArrayList<>();
        convertProductList.add(convertProduct);
        //when
        converterFacade.convertProduct(convertProductList);

        //then
        assertThat(convertProductList.get(0).getSku()).isEqualTo("AL01350951");
        assertThat(convertProductList.get(0).getOriginSku()).isEqualTo("AL0135CO");
        assertThat(convertProductList.get(0).getOriginColorCode()).isEqualTo("0951");
        assertThat(convertProductList.get(0).getIsFta()).isEqualTo(true);
    }

    @Test
    @DisplayName("[Converter] : adidas 상품 Converting Test")
    void convertProductAdidas() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("B75806LE")
                .colorCode("0951")
                .brandName("adidas")
                .madeBy("italy")
                .build();

        List<ConvertProduct> convertProductList = new ArrayList<>();
        convertProductList.add(convertProduct);
        //when
        converterFacade.convertProduct(convertProductList);

        //then
        assertThat(convertProductList.get(0).getSku()).isEqualTo("B75806");
        assertThat(convertProductList.get(0).getOriginSku()).isEqualTo("B75806LE");
        assertThat(convertProductList.get(0).getOriginColorCode()).isEqualTo("0951");
        assertThat(convertProductList.get(0).getIsFta()).isEqualTo(true);
    }


    @Test
    @DisplayName("[Converter] : a-p-c 상품 Converting Test")
    void convertProductApc() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("PXAWV-F61161LE")
                .colorCode("0951")
                .brandName("a-p-c")
                .madeBy("italy")
                .build();

        List<ConvertProduct> convertProductList = new ArrayList<>();
        convertProductList.add(convertProduct);
        //when
        converterFacade.convertProduct(convertProductList);

        //then
        assertThat(convertProductList.get(0).getSku()).isEqualTo("PXAWV-F611610951");
        assertThat(convertProductList.get(0).getOriginSku()).isEqualTo("PXAWV-F61161LE");
        assertThat(convertProductList.get(0).getOriginColorCode()).isEqualTo("0951");
        assertThat(convertProductList.get(0).getIsFta()).isEqualTo(true);
    }


    @Test
    @DisplayName("[Converter] : autry 상품 Converting Test")
    void convertProductAutry() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("AULWLL22A")
                .colorCode("0951")
                .brandName("autry")
                .madeBy("italy")
                .build();

        List<ConvertProduct> convertProductList = new ArrayList<>();
        convertProductList.add(convertProduct);
        //when
        converterFacade.convertProduct(convertProductList);

        //then
        assertThat(convertProductList.get(0).getSku()).isEqualTo("AULWLL22");
        assertThat(convertProductList.get(0).getOriginSku()).isEqualTo("AULWLL22A");
        assertThat(convertProductList.get(0).getOriginColorCode()).isEqualTo("0951");
        assertThat(convertProductList.get(0).getIsFta()).isEqualTo(true);
    }

    @Test
    @DisplayName("[Converter] : balenciaga 상품 Converting Test")
    void convertProductBalenciaga() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("7716401VG9Y")
                .colorCode("0951")
                .brandName("balenciaga")
                .madeBy("italy")
                .build();

        List<ConvertProduct> convertProductList = new ArrayList<>();
        convertProductList.add(convertProduct);
        //when
        converterFacade.convertProduct(convertProductList);

        //then
        assertThat(convertProductList.get(0).getSku()).isEqualTo("7716401VG9Y0951");
        assertThat(convertProductList.get(0).getOriginSku()).isEqualTo("7716401VG9Y");
        assertThat(convertProductList.get(0).getOriginColorCode()).isEqualTo("0951");
        assertThat(convertProductList.get(0).getIsFta()).isEqualTo(true);
    }

    @Test
    @DisplayName("[Converter] : celine 상품 Converting Test")
    void convertProductCeline() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("bfuts005726")
                .colorCode("0951")
                .brandName("celine")
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
    @DisplayName("[Converter] : chloe 상품 Converting Test")
    void convertProductChloe() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("bfuts005726")
                .colorCode("0951")
                .brandName("celine")
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
    @DisplayName("[Converter] : courreges 상품 Converting Test")
    void convertProductCourreges() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("bfuts005726")
                .colorCode("0951")
                .brandName("celine")
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
    @DisplayName("[Converter] : gucci 상품 Converting Test")
    void convertProductGucci() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("bfuts005726")
                .colorCode("0951")
                .brandName("celine")
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
    @DisplayName("[Converter] : maison-margiela 상품 Converting Test")
    void convertProductMaisonMargiela() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("bfuts005726")
                .colorCode("0951")
                .brandName("maison-margiela")
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
    @DisplayName("[Converter] : miu-miu 상품 Converting Test")
    void convertProductMiuMiu() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("bfuts005726")
                .colorCode("0951")
                .brandName("miu-miu")
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
    @DisplayName("[Converter] : burberry 상품 Converting Test")
    void convertProductBurberry() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("8062374130362")
                .colorCode("0951")
                .brandName("burberry")
                .madeBy("italy")
                .build();

        List<ConvertProduct> convertProductList = new ArrayList<>();
        convertProductList.add(convertProduct);
        //when
        converterFacade.convertProduct(convertProductList);

        //then
        assertThat(convertProductList.get(0).getSku()).isEqualTo("8062374");
        assertThat(convertProductList.get(0).getOriginSku()).isEqualTo("8062374130362");
        assertThat(convertProductList.get(0).getOriginColorCode()).isEqualTo("0951");
        assertThat(convertProductList.get(0).getIsFta()).isEqualTo(true);
    }

    @Test
    @DisplayName("[Converter] : Canada Goose 상품 Converting Test")
    void convertProductCanadaGoose() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("2235LNY")
                .colorCode("0951")
                .brandName("canada-goose")
                .madeBy("italy")
                .build();

        List<ConvertProduct> convertProductList = new ArrayList<>();
        convertProductList.add(convertProduct);
        //when
        converterFacade.convertProduct(convertProductList);

        //then
        assertThat(convertProductList.get(0).getSku()).isEqualTo("2235L");
        assertThat(convertProductList.get(0).getOriginSku()).isEqualTo("2235LNY");
        assertThat(convertProductList.get(0).getOriginColorCode()).isEqualTo("0951");
        assertThat(convertProductList.get(0).getIsFta()).isEqualTo(true);
    }

    @Test
    @DisplayName("[Converter] : jacquemus 상품 Converting Test")
    void convertProductJacquemus() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("21H213BA0073000")
                .colorCode("0951")
                .brandName("jacquemus")
                .madeBy("italy")
                .build();

        List<ConvertProduct> convertProductList = new ArrayList<>();
        convertProductList.add(convertProduct);
        //when
        converterFacade.convertProduct(convertProductList);

        //then
        assertThat(convertProductList.get(0).getSku()).isEqualTo("BA00730000951");
        assertThat(convertProductList.get(0).getOriginSku()).isEqualTo("21H213BA0073000");
        assertThat(convertProductList.get(0).getOriginColorCode()).isEqualTo("0951");
        assertThat(convertProductList.get(0).getIsFta()).isEqualTo(true);
    }

    @Test
    @DisplayName("[Converter] : kenzo 상품 Converting Test")
    void convertProductKenzo() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("FD55SW4404MFCO")
                .colorCode("0951")
                .brandName("kenzo")
                .madeBy("italy")
                .build();

        List<ConvertProduct> convertProductList = new ArrayList<>();
        convertProductList.add(convertProduct);
        //when
        converterFacade.convertProduct(convertProductList);

        //then
        assertThat(convertProductList.get(0).getSku()).isEqualTo("FD55SW4404MF0951");
        assertThat(convertProductList.get(0).getOriginSku()).isEqualTo("FD55SW4404MFCO");
        assertThat(convertProductList.get(0).getOriginColorCode()).isEqualTo("0951");
        assertThat(convertProductList.get(0).getIsFta()).isEqualTo(true);
    }

    @Test
    @DisplayName("[Converter] : new-balance 상품 Converting Test")
    void convertProductNewBalance() {
        //given
        ConvertProduct convertProduct = ConvertProduct.builder()
                .inputPrice(100.1)
                .monitoringSite("doublef")
                .sku("MR530CKSUE")
                .colorCode("0951")
                .brandName("new-balance")
                .madeBy("italy")
                .build();

        List<ConvertProduct> convertProductList = new ArrayList<>();
        convertProductList.add(convertProduct);
        //when
        converterFacade.convertProduct(convertProductList);

        //then
        assertThat(convertProductList.get(0).getSku()).isEqualTo("MR530CK");
        assertThat(convertProductList.get(0).getOriginSku()).isEqualTo("MR530CKSUE");
        assertThat(convertProductList.get(0).getOriginColorCode()).isEqualTo("0951");
        assertThat(convertProductList.get(0).getIsFta()).isEqualTo(true);
    }






    @Test
    @DisplayName("[Converter] : 상품 Convert to Search Product Test")
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
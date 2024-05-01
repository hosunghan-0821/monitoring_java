package com.example.monitor.infra.converter.controller;

import com.example.monitor.infra.converter.config.BrandConverterRule;
import com.example.monitor.infra.converter.core.ColorCodeConverter;
import com.example.monitor.infra.converter.core.FinalSkuMaker;
import com.example.monitor.infra.converter.core.SkuConverter;
import com.example.monitor.infra.converter.dto.ConvertProduct;
import com.example.monitor.infra.sender.ProductSender;
import com.example.monitor.infra.sender.ProductSenderImpl;
import com.example.monitor.infra.sender.SearchProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.monitor.infra.converter.config.BrandConverterInfoString.FTA_COUNTRY_LIST;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConverterFacadeImpl implements IConverterFacade {
    private final BrandConverterRule brandConverterRule;

    private final ColorCodeConverter colorCodeConverter;

    private final SkuConverter skuConverter;

    private final FinalSkuMaker finalSkuMaker;

    private final ProductSender sender;

    @Async
    @Override
    public List<ConvertProduct> convertProduct(List<ConvertProduct> convertProductList) {

        for (ConvertProduct convertProduct : convertProductList) {
            HashMap<String, BrandConverterRule.ConvertRuleInfo> brandConverterRuleMap = brandConverterRule.getBrandConverterRuleMap();
            BrandConverterRule.ConvertRuleInfo convertRuleInfo = brandConverterRuleMap.getOrDefault(convertProduct.getMonitoringSite() + convertProduct.getBrandName(), null);

            if (convertRuleInfo == null) {
                log.error("[CONVERT MODULE]" + convertProduct.getBrandName() + "의 브랜드에 변환 룰이 없습니다. 기본 룰로 진행합니다.");
                convertRuleInfo = new BrandConverterRule.ConvertRuleInfo();
            }

            String convertedSku = skuConverter.convertSku(convertProduct.getSku(), convertRuleInfo.getSkuRule());
            String convertedColorCode = colorCodeConverter.convertColorCode(convertProduct.getColorCode(), convertRuleInfo.getSkuRule());
            String finalSku = finalSkuMaker.convertFinal(convertedSku, convertedColorCode, convertRuleInfo.getEndRule());
            boolean isFta = isFtaCountry(convertProduct.getMadeBy());

            convertProduct.update(finalSku, convertedColorCode, convertProduct.getSku(), convertProduct.getColorCode(), isFta);

        }


        return convertProductList;
    }

    @Override
    public void sendToSearchServer(List<ConvertProduct> convertProductList) {


        List<SearchProduct> searchProductList = new ArrayList<>();
        changeToSearchProductList(convertProductList, searchProductList);

        sender.sendToSearchServer(searchProductList);
    }

    public void changeToSearchProductList(List<ConvertProduct> convertProductList, List<SearchProduct> searchProductList) {
        for (ConvertProduct convertProduct : convertProductList) {
            SearchProduct searchProduct = SearchProduct.builder()
                    .sku(convertProduct.getSku())
                    .productLink(convertProduct.getProductLink())
                    .colorCode(convertProduct.getColorCode())
                    .originSku(convertProduct.getOriginSku())
                    .monitoringSite(convertProduct.getMonitoringSite())
                    .inputPrice(convertProduct.getInputPrice())
                    .fta(isFtaCountry(convertProduct.getMadeBy()))
                    .madeBy(convertProduct.getMadeBy())
                    .originColorCode(convertProduct.getOriginColorCode())
                    .build();

            searchProductList.add(searchProduct);
        }
    }

    private Boolean isFtaCountry(String madeBy) {

        if (madeBy == null) {
            return false;
        } else {
            if (Arrays.asList(FTA_COUNTRY_LIST).contains(madeBy.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


}

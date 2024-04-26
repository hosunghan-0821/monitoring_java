package com.example.monitor.infra.converter.config;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class BrandConverterRule {

    private final HashMap<String, ConvertRuleInfo> brandConverterRuleMap = new HashMap<>();

    BrandConverterRule() {
        String[][] brandConvertingRule = BrandConverterInfoString.BRAND_CONVERTING_RULE;
        for (String[] strings : brandConvertingRule) {
            String key = strings[0] + strings[1];
            ConvertRuleInfo convertRuleInfo = new ConvertRuleInfo(strings[2], strings[3], strings[4]);
            brandConverterRuleMap.put(key, convertRuleInfo);
        }
    }

    @Getter
    public static class ConvertRuleInfo {

        private String skuRule;
        private String colorCodeRule;
        private String endRule;

         public ConvertRuleInfo(String skuRule, String colorCodeRule, String endRule) {
            this.skuRule = skuRule;
            this.colorCodeRule = colorCodeRule;
            this.endRule = endRule;
        }

        public ConvertRuleInfo(){
            this.skuRule = "default";
            this.colorCodeRule = "default";
            this.endRule ="default";
        }
    }
}

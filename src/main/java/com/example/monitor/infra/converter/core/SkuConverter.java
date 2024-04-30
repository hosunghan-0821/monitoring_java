package com.example.monitor.infra.converter.core;

import org.springframework.stereotype.Component;

@Component
public class SkuConverter {

    public String convertSku(String sku, String convertRule) {

        assert (convertRule != null && sku != null);

        return switch (convertRule) {
            case "default" -> sku;
            case "delete-final-english" -> deleteFinalEnglish(sku);
            case "delete-final-six-char" -> deleteFinalSixChar(sku);
            case "delete-final-english-without-one" -> deleteFinalEnglishWithoutOne(sku);
            case "delete-until-second-english-appear" -> deleteUntilSecondEnglishAppear(sku);
            case "delete-final-two-english" -> deleteFinalTwoEnglishChar(sku);
            case "new-balance-rule" -> newBalanceConvertingRule(sku);
            default -> sku;
        };

    }

    /*
     * 뒤에 영어 빼기
     * brand : Acne Studios, adidas Originals, Ami Paris, A.P.C, AUTRY,
     * */

    private String deleteFinalEnglish(String sku) {

        int length = sku.length();
        int finalIndex = sku.length();
        for (int i = length - 1; i >= 0; i--) {
            char word = sku.charAt(i);
            if ((word >= 'A' && word <= 'Z') || (word >= 'a' && word <= 'z')) {
                finalIndex = i;
            } else {
                break;
            }
        }
        return sku.substring(0, finalIndex);
    }

    /*
     * 뒤에 숫자6개 빼기
     * brand : Burberry
     * */
    private String deleteFinalSixChar(String sku) {
        int length = sku.length();
        return sku.substring(0, length - 6);
    }

    /*
     * 뒤에 영어 1개남기기
     * brand : Canada Goose
     * */
    private String deleteFinalEnglishWithoutOne(String sku) {

        int length = sku.length();
        int finalIndex = sku.length();
        for (int i = length - 1; i >= 0; i--) {
            char word = sku.charAt(i);
            if ((word >= 'A' && word <= 'Z') || (word >= 'a' && word <= 'z')) {
                finalIndex = i;
            } else {
                break;
            }
        }
        //without 1 english char
        return sku.substring(0, finalIndex + 1);
    }

    /*
     * 앞에 두번째 영어전까지 빼기
     * brand : JACQUEMUS
     * */
    private String deleteUntilSecondEnglishAppear(String sku) {


        int finalIndex = sku.length();
        int count = 0;
        int startIndex = 0;
        for (int i = 0; i < finalIndex; i++) {
            char word = sku.charAt(i);
            if ((word >= 'A' && word <= 'Z') || (word >= 'a' && word <= 'z')) {
                count++;
            }
            if (count == 2) {
                startIndex = i;
                break;
            }
        }
        return sku.substring(startIndex, finalIndex);
    }


    /*
     * 맨뒤에 영어 2개 빼기
     * brand : KENZO
     * */
    private String deleteFinalTwoEnglishChar(String sku) {
        int length = sku.length();
        return sku.substring(0, length - 2);
    }

    /*
     * 뒤에 3글자 빼기 , 만약 멘앞에 NB붙어있으면 빼기
     * brand : New Balance
     * */
    private String newBalanceConvertingRule(String sku) {
        int length = sku.length();
        int startIndex = 0;
        if (sku.startsWith("nb") || sku.startsWith("NB")) {
            startIndex = 2;
        }
        return sku.substring(startIndex, length - 3);
    }

    /*
     * 숫자뒤-영어앞까지 빼기 (x000) + 컬러코드
     * brand : Prada
     * TO-DO
     * */
    private String pradaConvertingRule(String sku) {
        return null;
    }

}

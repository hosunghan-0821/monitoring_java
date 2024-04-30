package com.example.monitor.infra.converter.config;

public class BrandConverterInfoString {

    /*
     * (모니터링 사이트,브랜드,품번처리 방법, 칼라코드 처리방법, 최종처리방법)
     *
     */

    public static final String[][] BRAND_CONVERTING_RULE = {

            //DOUBLEF FINISH
            {"doublef", "acne", "delete-final-english", "default", "default"},
            {"doublef", "adidas", "delete-final-english", "default", "sku-only"},
            {"doublef", "adidas-y-3", "default", "default", "sku-only"},
            {"doublef", "ami-paris", "delete-final-english", "default", "default"},
            {"doublef", "a-p-c", "delete-final-english", "default", "default"},
            {"doublef", "autry", "delete-final-english", "default", "sku-only"},

            {"doublef", "balenciaga", "default", "default", "default"},

            {"doublef", "celine", "default", "default", "default"},
            {"doublef", "chloe", "default", "default", "default"},
            {"doublef", "courreges", "default", "default", "default"},

            {"doublef", "gucci", "default", "default", "default"},

            {"doublef", "maison-margiela", "default", "default", "default"},
            {"doublef", "miu-miu", "default", "default", "default"},

            //verify
            {"doublef", "burberry", "delete-final-six-char", "default", "sku-only"},
            {"doublef", "canada-goose", "delete-final-english-without-one", "default", "sku-only"},
            {"doublef", "jacquemus", "delete-until-second-english-appear", "default", "default"},
            {"doublef", "kenzo", "delete-final-two-english", "default", "default"},
            {"doublef", "new-balance", "new-balance-rule", "default", "sku-only"},

            //TO-DO

            //Check
            {"doublef", "prada", "default", "default", "default"},

            {"doublef", "asics", "default", "default", "default"},

            {"doublef", "barbour", "default", "default", "default"},
            {"doublef", "birkenstock", "default", "default", "default"},
            {"doublef", "bottega-veneta", "default", "default", "default"},

            {"doublef", "c-p-company", "default", "default", "default"},
            {"doublef", "carhartt-wip", "default", "default", "default"},

            {"doublef", "drkshdw", "default", "default", "default"},

            {"doublef", "hoka-one-one", "default", "default", "default"},

            {"doublef", "maison-kitsune", "default", "default", "default"},
            {"doublef", "mihara-yasuhiro", "default", "default", "default"},
            {"doublef", "moncler", "default", "default", "default"},

            {"doublef", "rick-owens", "default", "default", "default"},

            {"doublef", "saintlaurent", "default", "default", "default"},
            {"doublef", "salomon", "default", "default", "default"},
            {"doublef", "stone-island", "default", "default", "default"},

            {"doublef", "the-north-face", "default", "default", "default"},
            {"doublef", "thom-browne", "default", "default", "default"},
            {"doublef", "ugg", "default", "default", "default"},
            {"doublef", "vivienne-westwood", "default", "default", "default"},


            //BIFFI
            {"biffi", "bottega veneta", "default", "default", "sku-only"},
            {"biffi", "thom browne", "default", "default", "sku-only"},
            {"biffi", "jacquemus", "default", "default", "sku-only"},
    };

    public static final String[] FTA_COUNTRY_LIST = {
            "belgium",
            "bulgaria",
            "cyprus",
            "czech republic",
            "denmark",
            "estonia",
            "finland",
            "france",
            "germany",
            "greece",
            "hungary",
            "ireland",
            "italy",
            "latvia",
            "lithuania",
            "luxembourg",
            "malta",
            "netherlands",
            "portugal",
            "romania",
            "slovakia",
            "slovenia",
            "spain",
            "sweden"
    };

    private BrandConverterInfoString() {
    }

    ;
}

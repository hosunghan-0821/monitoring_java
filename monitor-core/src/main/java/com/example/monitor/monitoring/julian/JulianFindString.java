package com.example.monitor.monitoring.julian;

public class JulianFindString {

    //Category 작업단위
    public static final String JULIAN ="julian";
    public static final String ALL_CATEGORIES ="all_categories";
    public static final String PROMO= "promo";

    public static final String[] JULIAN_MONITORING_SITE = {
            ALL_CATEGORIES,
            PROMO,
    };

    public static final String[] JULIAN_TARGET_BRAND_NAME_LIST ={
            "A.P.C",
            "ADIDAS",
            "ALEXANDER MCQUEEN",
            "AMI PARIS",
            "ASICS",
            "BALENCIAGA",
            "BARBOUR",
            "BIRKENSTOCK",
            "BURBERRY",
            "C.P COMPANY",
            "CANADA GOOSE",
            "CARHARTT WIP ",
            "CHLOE'",
            "COMME DES GARCONS",
            "COMME DES GARCONS PLAY",
            "COURREGES",
            "DIESEL",
            "DRKSHDW",
            "FENDI",
            "GOLDEN GOOSE",
            "GUCCI",
            "HOKA ONE ONE",
            "JACQUEMUS",
            "JUNYA WATANABE",
            "LEMAIRE",
            "MAISON KITSUNE'",
            "MAISON MARGIELA",
            "MAISON MIHARA YASUHIRO",
            "MARNI",
            "MAX MARA",
            "MIU MIU",
            "MM6 MAISON MARGIELA",
            "MONCLER",
            "NEW BALANCE",
            "PRADA",
            "RICK OWENS",
            "SAINT LAURENT",
            "SALOMON",
            "STONE ISLAND",
            "THE NORTH FACE",
            "THOM BROWNE",
            "UGG",
            "VIVIENNE WESTWOOD",
            "WALES BONNER",
            "LONGCHAMP"
    };

    //로그인 관련
    public static final String ID_FORM = "email";
    public static final String PASS_FORM = "pass";
    public static final String SUBMIT_FORM = "submit_login";


    //Find All Categories 관련
    public static final String FIND_ALL_CATEGORIES = "//div[@class='title title_font']//span[contains(text(),'ALL CATEGORIES')]";
    public static final String CLICK_BUTTON_WITH_SCRIPTS = "arguments[0].click();";

    public static final String ALL_CATEGORIES_URL = "https://b2bfashion.online/306-all";

    public static final String LEMAIRE_BAGS_URL = "https://b2bfashion.online/brand/113-lemaire?category=crossbody-bags,hand-bags,shoulder-bags,tote-bag,bag-accessories";

    public static final String PROMO_URL = "https://b2bfashion.online/439-promo";

    // 상품 내부 데이터 관련
    public static final String PRODUCT_TOP_DIV = "box-product-grid";
    public static final String CHILD_DIV = "./div";

    public static final String JULIAN_LOG_PREFIX = "[JULIAN] : \t";
    public static final String PRODUCT_IMAGE = ".//img[@class='img-responsive']";

    public static final String PRODUCT_NAME = ".//div[@class='product_name']";

    public static final String PRODUCT_SKU = ".//div[@class='produt_reference']";



    //생성자 생성 금지
    private JulianFindString() {

    }
}

package com.example.monitor.monitoring.theclub;

public class TheClubFindString {

    public static final String THE_CLUB_LOG_PREFIX = "[THE_CLUB] : \t";

    public static final String THE_CLUB = "theclub";

    public static final String[] THE_CLUB_BRAND_NAME_LIST = {
            "AMI PARIS",
            "BALENCIAGA",
            "BOTTEGA VENETA",
            "BURBERRY",
            "C.P. COMPANY",
            "CARHARTT WIP",
            "CELINE",
            "CANADA GOOSE",
            "DIESEL",
            "DIOR",
            "GUCCI",
            "LEMAIRE",
            "MAISON MARGIELA",
            "MONCLER",
            "MIU MIU",
            "MAISON KITSUNE",
            "MM6 MAISON MARGIELA",
            "MAX MARA",
            "NEW BALANCE",
            "ON",
            "RICK OWENS",
            "STONE ISLAND",
            "STUSSY",
            "SAINT LAURENT"
    };

    public static final String[] THE_CLUB_BRAND_URL_LIST = {
            "https://www.theclub.team/collections/ami-paris",
            "https://www.theclub.team/collections/balenciaga",
            "https://www.theclub.team/collections/bottega-veneta",
            "https://www.theclub.team/collections/burberry",
            "https://www.theclub.team/collections/c-p-company",
            "https://www.theclub.team/collections/carhartt-wip",
            "https://www.theclub.team/collections/celine",
            "https://www.theclub.team/collections/canada-goose",
            "https://www.theclub.team/collections/diesel",
            "https://www.theclub.team/collections/dior",
            "https://www.theclub.team/collections/gucci",
            "https://www.theclub.team/collections/lemaire",
            "https://www.theclub.team/collections/maison-margiela",
            "https://www.theclub.team/collections/moncler",
            "https://www.theclub.team/collections/miu-miu",
            "https://www.theclub.team/collections/maison-kitsune",
            "https://www.theclub.team/collections/mm6-maison-margiela",
            "https://www.theclub.team/collections/max-mara",
            "https://www.theclub.team/collections/new-balance",
            "https://www.theclub.team/collections/on",
            "https://www.theclub.team/collections/rick-owens",
            "https://www.theclub.team/collections/stone-island",
            "https://www.theclub.team/collections/stussy",
            "https://www.theclub.team/collections/saint-laurent"
    };

    // 로그인 관련
    public static final String THE_CLUB_MAIN_PAGE = "https://www.theclub.team/account/login";
    public static final String TC_ID_ID = "CustomerEmail";
    public static final String TC_PASS_ID = "CustomerPassword";
    public static final String TC_LOGIN_BUTTON_XPATH = "//form[@id='customer_login']//button";

    // 상품 관련 XPATH
    //ul[@id='product-grid']//li[@class='grid__item scroll-trigger animate--slide-in']
    public static final String TOP_DIV_XPATH = "//ul[@id='product-grid']";
    public static final String CHILD_PRODUCT_DIV = ".//li[@class='grid__item scroll-trigger animate--slide-in']";
    //ul[@id='product-grid']//li[@class='grid__item scroll-trigger animate--slide-in']
    public static final String PRODUCT_NAME_XPATH = ".//div[@class='card__content']//div[@class='card__information']//h3";
    public static final String PRODUCT_ID_XPATH = ".//div[@class='card__content']//div[@class='card__information']//h3//a";
    public static final String PRODUCT_PRICE_XPATH = ".//span[@class='price-item price-item--sale price-item--last']";
    public static final String PRODUCT_IMAGE_XPATH = ".//div[@class='media media--transparent media--hover-effect']//img";
    public static final String PRODUCT_SKU_XPATH = ".//span[@class='product-sku']";

    public static final String PRODUCT_DISCOUNT_XPATH = ".//div[@class='card__badge bottom left']//span";
    public static final String DISCOUNT_CHANGE = "discount_change";
    public static final String NEW_PRODUCT = "new_product";

    // 생성자 생성 금지
    private TheClubFindString() {
    }
}
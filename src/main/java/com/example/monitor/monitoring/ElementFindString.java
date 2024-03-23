package com.example.monitor.monitoring;

public class ElementFindString {

    //Category 작업단위
    public static final String ALL_CATEGORIES ="all_categories";
    public static final String PROMO= "promo";

    //로그인 관련
    public static final String ID_FORM = "email";
    public static final String PASS_FORM = "pass";
    public static final String SUBMIT_FORM = "submit_login";


    //Find All Categories 관련
    public static final String FIND_ALL_CATEGORIES = "//div[@class='title title_font']//span[contains(text(),'ALL CATEGORIES')]";
    public static final String CLICK_BUTTON_WITH_SCRIPTS = "arguments[0].click();";

    public static final String ALL_CATEGORIES_URL = "https://b2bfashion.online/306-all";

    public static final String PROMO_URL = "https://b2bfashion.online/439-promo";

    // 상품 내부 데이터 관련
    public static final String PRODUCT_TOP_DIV = "box-product-grid";
    public static final String CHILD_DIV = "./div";

    public static final String PRODUCT_IMAGE = ".//img[@class='img-responsive']";

    public static final String PRODUCT_NAME = ".//div[@class='product_name']";

    public static final String PRODUCT_ID = ".//div[@class='produt_reference']";

    //생성자 생성 금지
    private ElementFindString() {

    }
}

package com.example.monitor.monitoring.biffi;

public class BiffiFindString {

    public static final String[] BIFFI_BRAND_URL_LIST = {
            "https://biffi.atelier-software.eu/it/shop.html?tp=search&QCerca=bottega+veneta&idsett=DONNA",
            "https://biffi.atelier-software.eu/it/shop.html?tp=search&QCerca=jacquemus&idsett=DONNA",
            "https://biffi.atelier-software.eu/it/shop.html?tp=search&QCerca=thom+browne&idsett=DONNA",
    };

    public static final String[] BIFFI_BRAND_NAME_LIST = {
            "bottega veneta",
            "jacquemus",
            "thom browne"
    };

    public static final String BIFFI = "biffi";
    public static final String BIFFI_MAIN_URL = "https://biffi.atelier-software.eu/it/register.html";

    public static final String BIFFI_LOGIN_FORM_ID = "UserID";

    public static final String BIFFI_PASSWORD_FORD_ID = "passform3";

    public static final String BIFFI_LOG_PREFIX = "[BIFFI] : \t";

    public static final String LOGIN_BUTTON_XPATH = "//input[@type='submit']";
    public static final String NEXT_PRODUCT_PAGE_LINK = "//div[@class='col5 last right']//ul[@class='bloccopagine']//a";
    public static final String HREF = "href";
    public static final String PRODUCT_TOP_DIV_ID = "catalogogen";
    public static final String CHILD_DIV_XPATH = "./div";
    public static final String SALES_PERCENT_XPATH = ".//div[@class='prezzo']//span[@class='percsaldi']";
    public static final String PRICE_ELMENT_XPATH = ".//div[@class='prezzo']//span[@class='saldi2']";
    public static final String DETAIL_LINK_XPATH = ".//p[@class='pspec']//a";
    public static final String ID_ATTRIBUTE = "id";
    public static final String PRODUCT_SKU_XPATH = ".//div[@class='testofoto']//a//p";
    public static final String IMAGE_XPATH = ".//div[@class='cotienifoto']//a//img";
    public static final String SRC_ATTRIBUTE = "src";

    public static final String INFO_CSS_SELECTOR = "div.aks-accordion-row";
    public static final String GET_INFO_XPATH = "//div[@class='aks-accordion-item-content']//p";


}

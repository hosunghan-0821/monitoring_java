package com.example.monitor.monitoring.dobulef;

public class DoubleFFindString {

    public static final String[] manBrandNameList =
            {
                    "balenciaga",
                    "adidas",
                    "ami-paris",
                    "a-p-c",
                    "asics",
                    "bottega-veneta",
                    "gucci",
                    "hoka-one-one",
                    "maison-kitsune",
                    "maison-margiela",
                    "mihara-yasuhiro",
                    "moncler",
                    "new-balance",
                    "rick-owens",
                    "saintlaurent",
                    "salomon",
                    "stone-island",
                    "the-north-face",
                    "thom-browne"
            };
    public static final String[] womanBrandNameList = {
            "chloe",
            "jacquemus",
            "kenzo",
            "miu-miu",
            "prada",
            "vivienne-westwood"
    };

    //
    public static final String DOUBLE_F ="doublef";

    public static final String DOUBLE_F_MAIN_PAGE ="https://www.thedoublef.com/bu_en/customer/account/login/referer/aHR0cHM6Ly93d3cudGhlZG91YmxlZi5jb20vYnVfZW4v/";

    //로그인 관련
    public static final String DF_ID_ID = "email";
    public static final String DF_PASS_ID = "pass";
    public static final String DF_LOGIN_BUTTON_XPATH = "//Button[@class='btn btn-primary btn-animation w-full lg:w-2/3 mt-5']";

    //쿠키 관련
    public static final String DF_COOKIE_ID = "CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll";

    //프로덕트
    public static final String TOP_DIV_XPATH = "//div[@class='grid grid-cols-4 gap-5 md:gap-4 md:gap-y-16 md:grid-cols-12 lg:gap-5-5 opacity-100 transition-opacity duration-500 items-container items-stretch']";

    public static final String  PRODUCT_NAME_XPATH =".//h4[@class='product-card__name truncate ... font-light text-xs tracking-1-08 leading-snug mb-5px']";

    public static final String PRODUCT_DISCOUNT_XPATH =".//div[@class='product-card__discount absolute z-5 top-0 left-0 font-medium tracking-0-12 leading-8 text-white text-4xs h-5-5 px-5px bg-primary lg:tracking-0-15 lg:leading-10 lg:text-2xs lg:h-6 lg:px-10px']//span";

    public static final String PRODUCT_PRICE_XPATH =".//span[@class='price']";
    //생성방지
    private DoubleFFindString() {

    }
}

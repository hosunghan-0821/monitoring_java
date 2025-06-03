package com.example.monitor.monitoring.dobulef;

import com.example.monitor.monitoring.global.Locator;

public enum DoubleFLocator implements Locator {

    DF_ID("email"),
    DF_PASS("pass"),
    DF_COOKIE("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"),
    DF_PRODUCT_TOP_CONTAINER("//div[@class='grid grid-cols-4 gap-5 md:gap-4 md:gap-y-16 md:grid-cols-12 lg:gap-5-5 opacity-100 transition-opacity duration-500 items-container items-stretch']"),
    DF_PRODUCT_CHILD_CONTAINER(".//div[@class='w-full item product product-item gallery col-span-2 md:col-span-3 flex transition-all transform translate-y-0 duration-300 ease-in-out']"),

    //이 Link에서 SKU 값을 뽑아내기 때문에 이 부분이 상품정보 핵심
    DF_PRODUCT_LINK(".//h2[@class='product-card__name truncate ... font-light text-xs tracking-1-08 leading-snug mb-5px']//a"),
    DF_DISCOUNT_INFO(".//div[@class='product-card__discount absolute z-5 top-0 left-0 font-medium tracking-0-12 leading-8 text-white text-4xs h-5-5 px-5px bg-primary lg:tracking-0-15 lg:leading-10 lg:text-2xs lg:h-6 lg:px-10px']//span");
    private final String value;

    DoubleFLocator(String value) {
        this.value = value;
    }

    @Override
    public String selector() {
        return value;
    }
}

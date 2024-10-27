package com.example.monitor.monitoring.antonioli;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString(callSuper = true)
public class AntonioliLegacyProduct {

    private String gtmItemId;
    private String gtmItemName;
    private String gtmItemBrand;
    private String gtmItemCategory;
    private String gtmItemCategory2;
    private String gtmItemCategory3;
    private double gtmItemPrice;
    private int gtmItemIndex;
    private double gtmItemSaleValue;
    private String gtmItemSale;
    private String gtmItemAvailability;
    private String gtmItemListName;
    private long gtmItemListId;
    private String gtmItemCurrency;
    private String gtmItemAffiliation;
    private int gtmItemQuantity;

    public AntonioliLegacyProduct() {
    }
}

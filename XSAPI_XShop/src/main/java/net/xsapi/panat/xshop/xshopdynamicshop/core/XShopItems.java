package net.xsapi.panat.xshop.xshopdynamicshop.core;

import org.bukkit.Material;

public class XShopItems {

    private Material mat;
    private String privateName;
    private XShopItemsType itemsType;
    private int customModelData = 0;
    private double value;
    private double median;
    private double stock;
    private double previousPrice;
    private XShopPriceType priceType;
    private String customTags = "";

    private int volumeBuy = 0;
    private int volumeSell = 0;

    public XShopItems(Material mat,XShopItemsType itemsType,String privateName) {
        this.mat = mat;
        this.itemsType = itemsType;
        this.privateName = privateName;
    }

    public void setCustomTags(String customTags) {
        this.customTags = customTags;
    }

    public String getCustomTags() {
        return this.customTags;
    }

    public void setPriceType(XShopPriceType priceType) {
        this.priceType = priceType;
    }

    public XShopPriceType getPriceType() {
        return priceType;
    }

    public int getVolumeSell() {
        return volumeSell;
    }

    public void setVolumeSell(int volumeSell) {
        this.volumeSell = volumeSell;
    }

    public void setVolumeBuy(int volumeBuy) {
        this.volumeBuy = volumeBuy;
    }

    public int getVolumeBuy() {
        return volumeBuy;
    }

    public void setPreviousPrice(double previousPrice) {
        this.previousPrice = previousPrice;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public double getPreviousPrice() {
        return previousPrice;
    }

    public String getPrivateName() {
        return privateName;
    }

    public Material getMat() {
        return  this.mat;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public double getStock() {
        return stock;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getMedian() {
        return median;
    }

    public double getValue() {
        return value;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public XShopItemsType getItemsType() {
        return itemsType;
    }
}

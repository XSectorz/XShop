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

    public XShopItems(Material mat,XShopItemsType itemsType,String privateName) {
        this.mat = mat;
        this.itemsType = itemsType;
        this.privateName = privateName;
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

package net.xsapi.panat.xshop.xshopdynamicshop.core;

import java.util.ArrayList;

public class XShopDynamic {

    private String name;
    private XShopType shopType;
    private ArrayList<XShopItems> shopItems = new ArrayList<XShopItems>();

    public XShopDynamic(String name,XShopType shopType) {
        this.name = name;
        this.shopType = shopType;
    }

    public ArrayList<XShopItems> getShopItems() {
        return shopItems;
    }

    public void setShopItems(ArrayList<XShopItems> shopItems) {
        this.shopItems = shopItems;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShopType(XShopType shopType) {
        this.shopType = shopType;
    }

    public XShopType getShopType() {
        return shopType;
    }
}

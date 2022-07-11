package net.xsapi.panat.xshop.xshopdynamicshop.core;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class XShopItemsCustom extends XShopItems {

    private String name = "";
    private ArrayList<String> lore = new ArrayList<String>();

    private ArrayList<String> cmd;
    private boolean customItemStorage = false;
    private boolean isCustomItemStorageSell = false;
    private String storgeName = "";
    private String customType = "";
    private ItemStack itemStack;

    public XShopItemsCustom(String name, ArrayList<String> arr,Material mat, XShopItemsType itemsType,String privateName) {
        super(mat, itemsType, privateName);
        this.name = name;
        this.lore = arr;
    }

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setIsCustomItemStorageSell(boolean isCustomItemStorageSell) {
        this.isCustomItemStorageSell = isCustomItemStorageSell;
    }

    public boolean getIsCustomItemStorageSell() {
        return this.isCustomItemStorageSell;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public String getStorageName() {
        return storgeName;
    }

    public void setStorageName(String storageName) {
        this.storgeName = storageName;
    }

    public boolean getCustomStorage() {
        return this.customItemStorage;
    }

    public void setCustomItemStorage(boolean customItemStorage) {
        this.customItemStorage = customItemStorage;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getLore() {
        return lore;
    }

    public void setCmd(ArrayList<String> cmd) {
        this.cmd = cmd;
    }

    public ArrayList<String> getCmd() {
        return cmd;
    }
}

package net.xsapi.panat.xshop.xshopdynamicshop.gui;

import net.xsapi.panat.xshop.xshopdynamicshop.configuration.config;
import net.xsapi.panat.xshop.xshopdynamicshop.configuration.messages;
import net.xsapi.panat.xshop.xshopdynamicshop.configuration.storages;
import net.xsapi.panat.xshop.xshopdynamicshop.core.*;
import net.xsapi.panat.xshop.xshopdynamicshop.utils.ItemCreator;
import net.xsapi.panat.xsseasons.api.XSAPISeasons;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class XShopConfirm {

    static ArrayList<Integer> slot = new ArrayList<>(Arrays.asList(10,11,12,13,14,15,16));

    public static void openGUI(Player p, Material material,String privateName,boolean isBuy) {

        String title = "";
        if(isBuy) {
            title = config.customConfig.getString("gui_confirm.title_buy").replace("&", "§");
        } else {
            title = config.customConfig.getString("gui_confirm.title_sell").replace("&", "§");
        }

        Inventory inv = Bukkit.createInventory(null, 36, title);

        XShopDynamicShopCore.shopPrivateName.put(p.getUniqueId(),privateName);

        XShopItems shopItems = XShopDynamicShopCore.getItemsByPrivateNameAndShop(XShopDynamicShopCore.shopType.get(p.getUniqueId()),
                privateName);

        String displayName = "";
        ArrayList<String> lore = new ArrayList<>();
        ItemStack it = null;
        Map<Enchantment,Integer> enchant = new HashMap<>();
        boolean isUseCustomItemStorage = false;
        int modelData = 0;

        double stockChecker = 1;
        if(shopItems.getStock() == -1) {
            stockChecker = 1;
        } else {
            stockChecker = shopItems.getStock();
        }

        DecimalFormat df = new DecimalFormat("0.00");

        if(isBuy) {
            displayName = config.customConfig.getString("gui_confirm.templates.buy.displayName").replace("&", "§");
            lore  = new ArrayList<>(config.customConfig.getStringList("gui_confirm.templates.buy.lore"));
            XShopDynamicShopCore.shopConfirmType.put(p.getUniqueId(), XShopConfirmType.BUY);
        } else {
            displayName = config.customConfig.getString("gui_confirm.templates.sell.displayName").replace("&", "§");
            lore = new ArrayList<>(config.customConfig.getStringList("gui_confirm.templates.sell.lore"));
            XShopDynamicShopCore.shopConfirmType.put(p.getUniqueId(), XShopConfirmType.SELL);
        }


        if(shopItems.getItemsType().equals(XShopItemsType.CUSTOM)) {
            XShopItemsCustom shopItemsCustom = (XShopItemsCustom) shopItems;

            if(!shopItemsCustom.getCustomStorage()) { //not use custom storage item to display
                if(!shopItemsCustom.getName().isEmpty()) {
                    displayName = displayName.replace("%type%",shopItemsCustom.getName().replace("&", "§"));
                }

                ArrayList<String> loreNew = new ArrayList<>();

                if(!shopItemsCustom.getLore().isEmpty()) {

                    if(!shopItemsCustom.getLore().isEmpty()) {
                        for(String loreList : shopItemsCustom.getLore()) {
                            loreList = loreList.replace("&", "§");
                            loreNew.add(loreList);
                        }
                        loreNew.addAll(lore);
                    }
                }
                modelData = shopItemsCustom.getCustomModelData();
                lore = loreNew;
            } else {
                it = storages.customConfig.getItemStack(shopItemsCustom.getStorageName());
                ArrayList<String> loreNew = new ArrayList<>();

                if(it.hasItemMeta()) {
                    if(it.getItemMeta().hasDisplayName()) {
                        displayName = displayName.replace("%type%",it.getItemMeta().getDisplayName().replace("&", "§"));
                    }
                    if(it.getItemMeta().hasLore()) {
                        loreNew = (ArrayList<String>) it.getItemMeta().getLore();
                    }
                    if(it.getItemMeta().hasCustomModelData()) {
                        modelData = it.getItemMeta().getCustomModelData();
                    }
                    if(it.getItemMeta().hasEnchants()) {
                        enchant = it.getEnchantments();
                    }
                } else {
                    displayName = displayName.replace("%type%",it.getType().toString().replace("&", "§"));
                }
                isUseCustomItemStorage = true;
                loreNew.addAll(lore);
                lore = loreNew;
            }

        } else {
            displayName = displayName.replace("%type%",shopItems.getMat().toString());
            modelData = shopItems.getCustomModelData();
        }

        if(!shopItems.getCustomTags().isEmpty()) {
            if(shopItems.getCustomTags().split(":")[0].equalsIgnoreCase("XS_SEASON")) {
                displayName = displayName.replace(shopItems.getMat().toString(),shopItems.getCustomTags().split(":")[2].replace("&","§"));
            }
        }

        DecimalFormat df2 = new DecimalFormat("#");
        df2.setGroupingUsed(true);
        df2.setGroupingSize(3);
        String tempData = "";
        for(int i = 0 ; i < 7 ; i++) {

            if(i == 0) {
                tempData = df2.format(Math.pow(2,i))+"";
                displayName = displayName.replace("%multiple%",tempData);
            } else {
                displayName = displayName.replace(tempData,df2.format(Math.pow(2,i))+"");
                tempData = df2.format(Math.pow(2,i))+"";
            }

            ArrayList<String> loreItem = new ArrayList<>();

            String priceType = messages.customConfig.getString("price_" + shopItems.getPriceType().toString().toLowerCase());

            boolean isSeason = false;

            if(!shopItems.getCustomTags().isEmpty()) {
                String season = shopItems.getCustomTags().split(":")[1];
                XSAPISeasons seasonsAPI = new XSAPISeasons();

                if(seasonsAPI.getSeason().getSeasonRealName().equalsIgnoreCase(season)) {
                    isSeason = true;
                }
            }

            double price = (shopItems.getMedian()*shopItems.getValue())/stockChecker;

            for(String lores : lore) {
                if(isBuy) {
                    double v = price * Math.pow(2, i);
                    if(isSeason) {
                        lores = lores.replace("%price%","&8&m"+df.format(v) + "&f &x&f&f&d&e&3&1" + df.format(v*125/100) + " " + priceType);
                    } else {
                        lores = lores.replace("%price%",""+df.format(v) + " " + priceType);
                    }

                } else {
                    double v = (price * 75) / 100 * Math.pow(2, i);
                    if(isSeason) {
                        lores = lores.replace("%price%","&8&m"+df.format(v) + "&f &x&f&f&d&e&3&1" + df.format(v*125/100) + " " + priceType);
                    } else {
                        lores = lores.replace("%price%",""+df.format(v) + " " + priceType);
                    }

                }

                if(shopItems.getStock() != -1) {
                    lores = lores.replace("%stock%",df2.format(shopItems.getStock())+"");
                } else {
                    lores = lores.replace("%stock%","∞");
                }

                loreItem.add(lores);
            }
            if(isUseCustomItemStorage) {
                inv.setItem(slot.get(i), ItemCreator.createItem(material,
                        (int) Math.pow(2,i),modelData,displayName,loreItem,false,enchant));
                continue;
            }
            inv.setItem(slot.get(i), ItemCreator.createItem(material,
                    (int) Math.pow(2,i),modelData,displayName,loreItem,false));

        }

        inv.setItem(30,ItemCreator.createItem(Material.valueOf(config.customConfig.getString("gui_confirm.back_button.material")),
                1,config.customConfig.getInt("gui_confirm.back_button.customModelData"),config.customConfig.getString("gui_confirm.back_button.displayName").replace("&", "§")
                ,new ArrayList<String>(),false));

        ArrayList<String> loreInfo = new ArrayList<>();

        for(String lores : config.customConfig.getStringList("gui_confirm.info_button.lore")) {
            lores = lores.replace("&", "§");
            lores = lores.replace("%balance%",df.format(XShopDynamicShopCore.getEconomy().getBalance(p.getName())));
            lores = lores.replace("%points%",df.format(XShopDynamicShopCore.getPlayerPoint().look(p.getUniqueId())));
            loreInfo.add(lores);
        }


        inv.setItem(31,ItemCreator.createItem(Material.valueOf(config.customConfig.getString("gui_confirm.info_button.material")),
                1,config.customConfig.getInt("gui_confirm.info_button.customModelData"),config.customConfig.getString("gui_confirm.info_button.displayName").replace("&", "§")
                ,loreInfo,false));

        p.openInventory(inv);

    }

}

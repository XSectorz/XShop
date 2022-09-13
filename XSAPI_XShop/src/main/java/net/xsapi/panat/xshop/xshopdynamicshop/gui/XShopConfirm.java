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
        double realStock = 0;
        double addition_stock = 0;
        if(shopItems.getStock() == -1) {
            stockChecker = 1;
            realStock = -1;
        } else {
            stockChecker = shopItems.getStock();
            if(shopItems.getStock() == 1) {
                addition_stock+=1;
            }
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
            if(shopItems.getCustomTags().split(":")[0].equalsIgnoreCase("XS_SEASON")
        || shopItems.getCustomTags().split(":")[0].equalsIgnoreCase("XS_FISH")) {
                displayName = displayName.replace(shopItems.getMat().toString(),shopItems.getCustomTags().split(":")[2].replace("&","§"));
            }
        }

        DecimalFormat df2 = new DecimalFormat("#");
        df2.setGroupingUsed(true);
        df2.setGroupingSize(3);
        String tempData = "";

        ArrayList<Double> listPriceBuy = new ArrayList<>();
        ArrayList<Double> listPriceSell = new ArrayList<>();
        double price = 0;
        double priceSell = 0;
        int countMultipier = 0;
        double tStock = stockChecker;
        double tStockSell = stockChecker;

        if(realStock != -1) {
            tStock += 1;
            tStockSell += 1;
        }

        for(int data = 1 ; data <= 64 ; data++) {

            boolean isOutofStock = false;

            if(tStock <= 0) { //out of stock not calculate
                isOutofStock = true;
            }

            if(!isOutofStock) {
                price = price + (shopItems.getMedian()*shopItems.getValue())/(tStock+addition_stock);
            }

            priceSell = priceSell + (shopItems.getMedian()*shopItems.getValue())/(tStockSell+addition_stock);

            tStock -= 1;
            tStockSell += 1;

            if(data%(int) (Math.pow(2, countMultipier)) == 0) { //1 2 4 8 16 32 64
                if(isOutofStock) {
                    listPriceBuy.add(-1.0);
                } else {
                    if(price <= 0) {
                        price = -1;
                    }
                    listPriceBuy.add(price);
                }
                listPriceSell.add(priceSell*75/100);
                countMultipier += 1;
            }
        }

        XShopDynamicShopCore.shopConfirmPrice.put(p.getUniqueId(),listPriceBuy);
        XShopDynamicShopCore.shopConfirmPriceSell.put(p.getUniqueId(),listPriceSell);

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

            if(realStock == -1) {
                price =  (shopItems.getMedian()*shopItems.getValue())/1;
                //Bukkit.broadcastMessage("PRICE: " + price);
            }


            for(String lores : lore) {

                if(isBuy) {
                    double value = 0;
                    if(realStock == -1) {
                        value = price * Math.pow(2, i);
                    } else {
                        value = listPriceBuy.get(i);
                    }

                    String v = df.format(value);
                    if(value <= 0) {
                        v = "&cไม่สามาถซื้อได้";
                    }

                    if(isSeason) {

                        v = df.format(value);
                        value = (value*125/100);

                        if(value <= 0) {
                            v = "&cไม่สามาถซื้อได้";
                            lores = lores.replace("%price%","&8&m"+v + "&f &x&f&f&d&e&3&1");
                        } else {
                            lores = lores.replace("%price%","&8&m"+v + "&f &x&f&f&d&e&3&1" + df.format(value) + " " + priceType);
                        }

                    } else {
                        lores = lores.replace("%price%",""+ v + " " + priceType);
                    }

                } else {
                   // double v = (price * 75) / 100 * Math.pow(2, i);
                    double value = 0;
                    if(realStock == -1) {
                        value = (price * 75) / 100  * Math.pow(2, i);
                    } else {
                        value = listPriceSell.get(i);
                    }

                    String v = df.format(value);
                    if(value <= 0) {
                        v = "&cไม่สามารถขายได้";
                    }

                    if(isSeason) {
                        value = value*125/100;
                        if(value <= 0) {
                            v = "&cไม่สามาถขายได้";
                            lores = lores.replace("%price%","&8&m"+v);
                        } else {
                            lores = lores.replace("%price%","&8&m"+v + "&f &x&f&f&d&e&3&1" + df.format(value) + " " + priceType);
                        }

                    } else {
                        lores = lores.replace("%price%",""+v + " " + priceType);
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

package net.xsapi.panat.xshop.xshopdynamicshop.gui;

import net.xsapi.panat.xshop.xshopdynamicshop.configuration.*;
import net.xsapi.panat.xshop.xshopdynamicshop.core.*;
import net.xsapi.panat.xshop.xshopdynamicshop.task.task_updateUI;
import net.xsapi.panat.xshop.xshopdynamicshop.utils.ItemCreator;
import net.xsapi.panat.xshop.xshopdynamicshop.utils.TimeConverter;
import net.xsapi.panat.xsseasons.api.XSAPISeasons;
import net.xsapi.panat.xsseasons.core.SeasonsHandler;
import net.xsapi.panat.xsseasons.core.SeasonsInterface;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.*;

public class XShop {

    public static void openInv(Player p, XShopType shopType, int page,boolean isUpdate) {

        String title = "";

        if (shopType.equals(XShopType.NoneType)) {
            title = config.customConfig.getString("gui.gui_title").replace("&", "§");
        } else if (shopType.equals(XShopType.Block)) {
            title = block.customConfig.getString("gui.title").replace("&", "§");
        } else if (shopType.equals(XShopType.Farming)) {
            title = farming.customConfig.getString("gui.title").replace("&", "§");
        } else if (shopType.equals(XShopType.Minerals)) {
            title = minerals.customConfig.getString("gui.title").replace("&", "§");
        } else if (shopType.equals(XShopType.Miscellaneous)) {
            title = miscellaneous.customConfig.getString("gui.title").replace("&", "§");
        } else if (shopType.equals(XShopType.Mobs)) {
            title = mobs.customConfig.getString("gui.title").replace("&", "§");
        }

        Inventory inv = Bukkit.createInventory(null, 54, title);

        for (int slot : config.customConfig.getIntegerList("gui.blocked_barrier.slot")) {
            inv.setItem(slot, ItemCreator.createItem(Material.getMaterial(config.customConfig.getString("gui.blocked_barrier.material"))
                    , 1, config.customConfig.getInt("gui.blocked_barrier.customModelData"), config.customConfig.getString("gui.blocked_barrier.displayName")
                    , new ArrayList<String>(), false));
        }

        for (String menu : config.customConfig.getConfigurationSection("gui.menu").getKeys(false)) {
            List<String> list = config.customConfig.getStringList("gui.menu." + menu + ".lore");
            Collections.replaceAll(list, "&", "§");

            boolean glowing = false;

            if (shopType.toString().equalsIgnoreCase(menu)) {
                glowing = true;
            }

            inv.setItem(config.customConfig.getInt("gui.menu." + menu + ".slot"), ItemCreator.createItem(Material.getMaterial(config.customConfig.getString("gui.menu." + menu + ".material"))
                    , 1, config.customConfig.getInt("gui.menu." + menu + ".customModelData"), config.customConfig.getString("gui.menu." + menu + ".displayName")
                    , new ArrayList<>(list), glowing));
        }

        for (String items : config.customConfig.getConfigurationSection("gui.items").getKeys(false)) {
            String displayName = config.customConfig.getString("gui.items." + items + ".displayName");

            if(items.equalsIgnoreCase("reset_timer")) {
                displayName = displayName.replace("%timer%",new TimeConverter().getTime((config.customConfig.getLong("reset_price.time_stamp")-System.currentTimeMillis())));
                if(!config.customConfig.getBoolean("reset_price.enable")) {
                    continue;
                }
            }

            inv.setItem(config.customConfig.getInt("gui.items." + items + ".slot"), ItemCreator.createItem(Material.getMaterial(config.customConfig.getString("gui.items." + items + ".material"))
                    , 1, config.customConfig.getInt("gui.items." + items + ".customModelData"), displayName
                    , new ArrayList<>(), false));
        }

        if (!shopType.equals(XShopType.NoneType)) {
            XShopDynamic shop = null;

            for (XShopDynamic shopList : XShopDynamicShopCore.shopList) {
                if (shopList.getShopType().equals(shopType)) {
                    shop = shopList;
                }
            }

            if (!shop.getShopItems().isEmpty()) {

                ArrayList<XShopItems> itemsIterator = new ArrayList<XShopItems>(shop.getShopItems());
                int startIndex = (XShopDynamicShopCore.shopPage.get(p.getUniqueId()) * config.customConfig.getIntegerList("gui.slot").size())
                        - config.customConfig.getIntegerList("gui.slot").size();

                List<Integer> slot = config.customConfig.getIntegerList("gui.slot");

                Material mat = null;
                ItemStack it = null;
                boolean isUseCustomItemStorage = false;
                Map<Enchantment,Integer> enchant = new HashMap<>();
                int modelData = 0;
                int amount = 1;

                for (int i = 0; i < slot.size() ; i++) {

                    if (startIndex + i >= XShopDynamicShopCore.shopPage.get(p.getUniqueId()) * config.customConfig.getIntegerList("gui.slot").size()) {
                        break;
                    }

                    if (startIndex + i >= itemsIterator.size()) {
                        break;
                    }

                    XShopItems shopItems = itemsIterator.get(startIndex + i);

                    String display = "";
                    List<String> list = new ArrayList<>();


                    if(shopItems.getItemsType().equals(XShopItemsType.CUSTOM)) {
                        XShopItemsCustom shopItemsCustom = (XShopItemsCustom) shopItems;
                        display = config.customConfig.getString("gui.templates." + String.valueOf(shopItems.getItemsType().toString()).toLowerCase() + "." + shopItemsCustom.getCustomType() + ".displayName").replace("&", "§");
                        list = config.customConfig.getStringList("gui.templates." + String.valueOf(shopItems.getItemsType().toString()).toLowerCase() + "." + shopItemsCustom.getCustomType() + ".lore");

                    } else {
                        display = config.customConfig.getString("gui.templates." + String.valueOf(shopItems.getItemsType().toString()).toLowerCase() + ".displayName").replace("&", "§");
                        list = config.customConfig.getStringList("gui.templates." + String.valueOf(shopItems.getItemsType().toString()).toLowerCase() + ".lore");

                    }
                    Collections.replaceAll(list, "&", "§");

                    DecimalFormat df = new DecimalFormat("0.00");
                    ArrayList<String> listLore = new ArrayList<String>();

                    double stockChecker = 1;
                    if (shopItems.getStock() == -1) {
                        stockChecker = 1;
                    } else {
                        stockChecker = shopItems.getStock();
                    }

                    float totalVol = shopItems.getVolumeBuy()+shopItems.getVolumeSell();
                    float totalVolDiv = totalVol;

                    if(totalVol == 0) {
                        totalVolDiv = 1;
                    }

                    String priceType = messages.customConfig.getString("price_" + shopItems.getPriceType().toString().toLowerCase());

                    boolean isSeason = false;

                    if(!shopItems.getCustomTags().isEmpty()) {
                        String season = shopItems.getCustomTags().split(":")[1];
                        XSAPISeasons seasonsAPI = new XSAPISeasons();

                        if(seasonsAPI.getSeason().getSeasonRealName().equalsIgnoreCase(season)) {
                            isSeason = true;
                        }
                    }

                    double price = ((shopItems.getMedian() * shopItems.getValue()) / stockChecker);

                    for (String L : list) {

                        if(isSeason) {
                            L = L.replace("%price_to_buy%",
                                    "&8&m" + df.format(price)+ "&f &x&4&d&e&a&4&9" + df.format(price*125/100) + " " + priceType);
                            L = L.replace("%price_to_sell%",
                                    "&8&m" + df.format((price * 75) / 100) + "&f &x&f&f&5&0&6&5" + df.format((price * 75) / 100*125/100) + " " + priceType);
                        } else {
                            L = L.replace("%price_to_buy%",
                                    "" + df.format((price)) + " " + priceType);
                            L = L.replace("%price_to_sell%",
                                    "" + df.format(((price * 75) / 100)) + " " + priceType);
                        }


                        L = L.replace("%total_volume%", (int) totalVol + "");
                        L = L.replace("%vol_buy%",shopItems.getVolumeBuy()+"");
                        L = L.replace("%vol_sell%",shopItems.getVolumeSell()+"");
                        L = L.replace("%vol_percent_buy%",df.format((float) (shopItems.getVolumeBuy()/(totalVolDiv) * 100.0)) + "%");
                        L = L.replace("%vol_percent_sell%",df.format((float) (shopItems.getVolumeSell()/(totalVolDiv) * 100.0))+ "%");

                        if (shopItems.getStock() != -1) {
                            DecimalFormat df2 = new DecimalFormat("#");
                            df2.setGroupingUsed(true);
                            df2.setGroupingSize(3);

                            L = L.replace("%stock%",
                                    df2.format(stockChecker) + "");
                        } else {
                            L = L.replace("%stock%", "∞");
                        }
                        listLore.add(L);
                    }

                    if (shopItems.getItemsType().equals(XShopItemsType.CUSTOM)) {
                        XShopItemsCustom shopItemsCustom = (XShopItemsCustom) shopItems;

                        if (!shopItemsCustom.getCustomStorage()) { //not use custom storage item to display
                            if (!shopItemsCustom.getName().isEmpty()) {
                                display = display.replace("%type%", shopItemsCustom.getName().replace("&", "§"));
                            }

                            ArrayList<String> loreNew = new ArrayList<>();

                            if (!shopItemsCustom.getLore().isEmpty()) {

                                if (!shopItemsCustom.getLore().isEmpty()) {
                                    for (String loreList : shopItemsCustom.getLore()) {
                                        loreList = loreList.replace("&", "§");
                                        loreNew.add(loreList);
                                    }
                                    loreNew.addAll(listLore);
                                }
                            }

                            if (shopItems.getMat() != null) {
                                mat = shopItems.getMat();
                            }

                            modelData = shopItems.getCustomModelData();

                            listLore = loreNew;
                        } else {
                            it = storages.customConfig.getItemStack(shopItemsCustom.getStorageName());
                            ArrayList<String> loreNew = new ArrayList<>();

                            if (it.hasItemMeta()) {
                                if (it.getItemMeta().hasDisplayName()) {
                                    display = display.replace("%type%", it.getItemMeta().getDisplayName().replace("&", "§"));
                                }
                                if (it.getItemMeta().hasLore()) {
                                    loreNew = (ArrayList<String>) it.getItemMeta().getLore();
                                }
                                if (it.getItemMeta().hasCustomModelData()) {
                                    modelData = it.getItemMeta().getCustomModelData();
                                }
                                if(it.hasItemMeta()) {
                                    if(it.getItemMeta().hasEnchants()) {
                                        enchant = it.getEnchantments();
                                    }
                                }
                            } else {
                                display = display.replace("%type%", it.getType().toString().replace("&", "§"));
                            }
                            loreNew.addAll(listLore);
                            isUseCustomItemStorage = true;
                            listLore = loreNew;
                            mat = it.getType();
                            amount = it.getAmount();
                        }

                    } else {
                        display = display.replace("%type%", shopItems.getMat().toString());
                        if (shopItems.getMat() != null) {
                            mat = shopItems.getMat();
                        }
                    }

                    if(!shopItems.getCustomTags().isEmpty()) {
                        String tags = shopItems.getCustomTags();

                        if(tags.split(":")[0].equalsIgnoreCase("XS_SEASON")) {
                            display = display.replace(shopItems.getMat().toString(),tags.split(":")[2].replace("&","§"));

                            String Season = tags.split(":")[1];
                            XSAPISeasons seasonsAPI = new XSAPISeasons();
                            if(seasonsAPI.getSeason().getSeasonRealName().equalsIgnoreCase(Season)) {
                                listLore.add(0,"&r".replace("&", "§"));
                                listLore.add(1,messages.customConfig.getString("xsseasons_included").replace("&", "§"));
                            }

                        }
                    }

                    if(shopItems.getCustomModelData() != -1) {
                        modelData = shopItems.getCustomModelData();
                    }

                    String trend = "";
                    double stock = 1;

                    if (shopItems.getStock() == -1) {
                        stock = 1;
                    } else {
                        stock = shopItems.getStock();
                    }

                    if (shopItems.getValue() * shopItems.getMedian() / stock < shopItems.getPreviousPrice()) {
                        double value = 100 - ((shopItems.getValue() * shopItems.getMedian() / stock / shopItems.getPreviousPrice()) * 100);

                        trend = config.customConfig.getString("gui.down_trend").replace("%value%", df.format(value));

                    } else {
                        double value = ((shopItems.getValue() * shopItems.getMedian() / stock / shopItems.getPreviousPrice()) * 100) - 100;

                        if (!df.format(value).equalsIgnoreCase("0.00")) {
                            trend = config.customConfig.getString("gui.up_trend").replace("%value%", df.format(value));
                        } else {
                            trend = "";
                        }
                    }
                    display = display.replace("%valueChange%", trend);

                    if(isUseCustomItemStorage) {
                        inv.setItem(slot.get(i), ItemCreator.createItem(mat, amount, modelData
                                , display, listLore, false,enchant));
                        continue;
                    }

                    inv.setItem(slot.get(i), ItemCreator.createItem(mat, amount, modelData, display, listLore, false));
                }
            }
        }

        if(isUpdate) {
            if (!XShopDynamicShopCore.getPlayerOpenGUI().contains(p)) {
                XShopDynamicShopCore.getPlayerOpenGUI().add(p);
            }
        }

        p.openInventory(inv);
    }

}

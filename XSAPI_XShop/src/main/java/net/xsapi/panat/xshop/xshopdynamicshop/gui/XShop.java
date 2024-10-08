package net.xsapi.panat.xshop.xshopdynamicshop.gui;

import net.xsapi.panat.xshop.xshopdynamicshop.configuration.*;
import net.xsapi.panat.xshop.xshopdynamicshop.core.*;
import net.xsapi.panat.xshop.xshopdynamicshop.utils.ItemCreator;
import net.xsapi.panat.xshop.xshopdynamicshop.utils.TimeConverter;
import net.xsapi.panat.xsseasons.api.XSAPISeasons;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;

public class XShop {

    public static void openInv(Player p, XShopType shopType, int page,boolean isUpdate,boolean isSpecial) {

        String title = "";
        int size = 54;
        String typeItems = "items";
        String typeBarrier = "blocked_barrier";

        if(!core.getBuyAmountCooldown().containsKey(p)) {
            core.getBuyAmountCooldown().put(p,new HashMap<>());
        }
        if(!core.getSellAmountCooldown().containsKey(p)) {
            core.getSellAmountCooldown().put(p,new HashMap<>());
        }

        if(!core.getBuyTempDisable().containsKey(p)) {
            core.getBuyTempDisable().put(p,0L);
        }

        if(!core.getSellTempDisable().containsKey(p)) {
            core.getSellTempDisable().put(p,0L);
        }

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
        } else if (shopType.equals(XShopType.Seasonitems)) {
            title = seasonitems.customConfig.getString("gui.title").replace("&", "§");
            //p.sendMessage("title: " + title);
            size = 45;
        } else if (shopType.equals(XShopType.Fishing)) {
            title = fishing.customConfig.getString("gui.title").replace("&", "§");
            //p.sendMessage("title2: " + title);
            size = 45;
        } else if (shopType.equals(XShopType.Foods)) {
            title = foods.customConfig.getString("gui.title").replace("&", "§");
            //p.sendMessage("title2: " + title);
            size = 45;
        } else if (shopType.equals(XShopType.New_block)) {
            title = new_block.customConfig.getString("gui.title").replace("&", "§");
            //p.sendMessage("title2: " + title);
            size = 45;
            typeItems = "new_block_items";
            typeBarrier = "new_block_barrier";
        }

        Inventory inv = Bukkit.createInventory(null, size, title);

        if(isSpecial) {
            typeBarrier = "blocked_barrier_special";
            typeItems = "items_special";
        }
        for (int slot : config.customConfig.getIntegerList("gui." + typeBarrier +".slot")) {
            inv.setItem(slot, ItemCreator.createItem(Material.getMaterial(config.customConfig.getString("gui." + typeBarrier + ".material"))
                    , 1, config.customConfig.getInt("gui." + typeBarrier + ".customModelData"), config.customConfig.getString("gui." + typeBarrier + ".displayName")
                    , new ArrayList<String>(), false));
        }

        if(!isSpecial && !shopType.equals(XShopType.New_block)) {
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

        }
        XShopDynamic shop = null;
        if (!shopType.equals(XShopType.NoneType)) {

            for (XShopDynamic shopList : core.shopList) {
                if (shopList.getShopType().equals(shopType)) {
                    shop = shopList;
                }
            }

            //Bukkit.broadcastMessage("SIZE" + shop.getShopItems().size());

            if (!shop.getShopItems().isEmpty()) {
                String typeSlot = "slot";
                if(isSpecial) {
                    typeSlot = "slot_special";
                } else if(shopType.equals(XShopType.New_block)) {
                    typeSlot = "slot_newblock";
                }
                ArrayList<XShopItems> itemsIterator = new ArrayList<XShopItems>(shop.getShopItems());
                int startIndex = (core.shopPage.get(p.getUniqueId()) * config.customConfig.getIntegerList("gui." + typeSlot).size())
                        - config.customConfig.getIntegerList("gui." + typeSlot).size();

                List<Integer> slot = config.customConfig.getIntegerList("gui." + typeSlot);

                Material mat = null;
                ItemStack it = null;
                boolean isUseCustomItemStorage = false;
                Map<Enchantment,Integer> enchant = new HashMap<>();
                int modelData = 0;
                int amount = 1;
                int indexSlot = 0;
                int specialCounter = 0;

                if(isSpecial) {
                    if(shopType.equals(XShopType.Seasonitems)) {
                        specialCounter = core.seasonShops.get(core.seasonsAPI.getSeason().getSeasonRealName()).size();
                        itemsIterator = core.seasonShops.get(core.seasonsAPI.getSeason().getSeasonRealName());
                        specialCounter -= startIndex;
                        //specialCounter += seasonitems.customConfig.getConfigurationSection("items_special").getKeys(false).size();
                    } else if(shopType.equals(XShopType.Fishing)) {
                        specialCounter = core.fishShops.get(core.seasonsAPI.getSeason().getSeasonRealName()).size();
                        itemsIterator = core.fishShops.get(core.seasonsAPI.getSeason().getSeasonRealName());
                        specialCounter -= startIndex;
                        //specialCounter += fishing.customConfig.getConfigurationSection("items_special").getKeys(false).size();
                    } else if(shopType.equals(XShopType.Foods)) {
                        specialCounter = core.foodsShops.size();
                        itemsIterator = core.foodsShops;
                        specialCounter -= startIndex;
                    }
                    //p.sendMessage("SEASON: " + XShopDynamicShopCore.seasonsAPI.getSeason().getSeasonRealName() + " " + " COUNTER " + specialCounter);
                    //p.sendMessage("START INDEX: " + startIndex);
                } else {
                    if(shopType.equals(XShopType.New_block)) {
                        itemsIterator = core.newBlockShops;
                    }
                }
                int i = 0;

                //Bukkit.broadcastMessage("SLOT SIZE: " + slot.size());
                //Bukkit.broadcastMessage("ITEM SIZE: " + itemsIterator.size());
                //Bukkit.broadcastMessage("SPECIAL COUNTER: " + specialCounter);
                while (i < slot.size() || specialCounter > 0) {

                   /// Bukkit.broadcastMessage("I: " + (i+startIndex) + "/"+ slot.size() + " " + " COUNTER " + specialCounter);
                    if (startIndex + indexSlot >= core.shopPage.get(p.getUniqueId()) * config.customConfig.getIntegerList("gui."+typeSlot).size()) {
                        break;
                    }

                    if (startIndex + i >= itemsIterator.size()) {
                        break;
                    }

                    if(core.isUsingSpecialShop.get(p.getUniqueId())) {
                        if(specialCounter <= 0) {
                            break;
                        }
                    }

                    XShopItems shopItems = itemsIterator.get(startIndex + i);
                    //Bukkit.broadcastMessage("START INDEX"+(startIndex + i));
                    //Bukkit.broadcastMessage("SHOP: " + shopItems.getPrivateName());
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
                    double realStock = 0;
                    double addition_stock = 0;
                    if (shopItems.getStock() == -1) {
                        stockChecker = 1;
                        realStock = -1;
                    } else {
                        stockChecker = shopItems.getStock();
                        if(shopItems.getStock() == 1) {
                            addition_stock+=1;
                        }
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

                    double price = ((shopItems.getMedian() * shopItems.getValue()) / (stockChecker));

                    if(realStock != -1) {
                        price = ((shopItems.getMedian() * shopItems.getValue()) / (stockChecker+1+addition_stock));
                    }


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
                                listLore = loreNew;
                            }

                            if (shopItems.getMat() != null) {
                                mat = shopItems.getMat();
                            }

                            modelData = shopItems.getCustomModelData();

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

                    if(shopItems.getPrivateName().startsWith("SPECIAL_")) {
                        specialCounter--;
                    }

                    if(!shopItems.getCustomTags().isEmpty()) {
                        String tags = shopItems.getCustomTags();

                        if(tags.split(":")[0].equalsIgnoreCase("XS_SEASON") || tags.split(":")[0].equalsIgnoreCase("XS_FISH")
                                || tags.split(":")[0].equalsIgnoreCase("XS_FOODS")) {
                            display = display.replace(shopItems.getMat().toString(),tags.split(":")[2].replace("&","§"));

                            String Season = tags.split(":")[1];
                            XSAPISeasons seasonsAPI = new XSAPISeasons();
                            if(seasonsAPI.getSeason().getSeasonRealName().equalsIgnoreCase(Season)) {
                                listLore.add(0,"&r".replace("&", "§"));
                                listLore.add(1,messages.customConfig.getString("xsseasons_included").replace("&", "§"));
                                specialCounter--;
                            } else {
                                if(!tags.split(":")[1].equalsIgnoreCase("All")) { //not equal all season
                                    i++;
                                    continue;
                                } else {
                                    specialCounter--;
                                }
                            }
                        }
                    }

                    if(!isUseCustomItemStorage) {
                        if(shopItems.getCustomModelData() != -1) {
                            modelData = shopItems.getCustomModelData();
                        }
                    }

                    String trend = "";
                    double stock = 1;

                    if (shopItems.getStock() == -1) {
                        stock = 1;
                    } else {
                        stock = shopItems.getStock()+addition_stock;
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
                        inv.setItem(slot.get(indexSlot), ItemCreator.createItem(mat, 1, modelData
                                , display, listLore, false,enchant));
                        indexSlot++;
                        i++;
                        continue;
                    }

                    inv.setItem(slot.get(indexSlot), ItemCreator.createItem(mat, 1, modelData, display, listLore, false));
                    indexSlot++;
                    i++;
                }
            }
        }

        for (String items : config.customConfig.getConfigurationSection("gui." + typeItems).getKeys(false)) {

            if(items.equalsIgnoreCase("info_season")) {
                if(shopType.equals(XShopType.Fishing) || shopType.equals(XShopType.Foods)) {
                    continue;
                }
            }
            if(items.equalsIgnoreCase("info_fishing")) {
                if(shopType.equals(XShopType.Seasonitems) || shopType.equals(XShopType.Foods)) {
                    continue;
                }
            }
            if(items.equalsIgnoreCase("info_foods")) {
                if(shopType.equals(XShopType.Seasonitems) || shopType.equals(XShopType.Fishing)) {
                    continue;
                }
            }

            String displayName = config.customConfig.getString("gui." + typeItems + "." + items + ".displayName");
            ItemStack it = ItemCreator.createItem(Material.getMaterial(config.customConfig.getString("gui." + typeItems + "." + items + ".material"))
                    , 1, config.customConfig.getInt("gui." + typeItems + "." + items + ".customModelData"), displayName
                    , new ArrayList<>(), false);
            ArrayList<String> lore = new ArrayList<>();
            ItemMeta itemMeta = it.getItemMeta();

            if(items.equalsIgnoreCase("reset_timer")) {
                displayName = displayName.replace("%timer%",new TimeConverter().getTime((config.customConfig.getLong("reset_price.time_stamp")-System.currentTimeMillis())));
                if(!config.customConfig.getBoolean("reset_price.enable")) {
                    continue;
                }
                itemMeta.setDisplayName(displayName.replace("&","§"));
            } else if(items.equalsIgnoreCase("next_page_s")) {
                if(!shop.getShopItems().isEmpty()) {
                    if(shopType.equals(XShopType.Seasonitems)) {
                        if(core.seasonShops.get(core.seasonsAPI.getSeason().getSeasonRealName()).size()
                                < (page*config.customConfig.getIntegerList("gui." + typeBarrier +".slot").size())+1 ) {
                            itemMeta.setCustomModelData(10234);
                        }
                    } else if(shopType.equals(XShopType.Fishing)) {
                        if(core.fishShops.get(core.seasonsAPI.getSeason().getSeasonRealName()).size()
                                < (page*config.customConfig.getIntegerList("gui." + typeBarrier +".slot").size())+1 ) {
                            itemMeta.setCustomModelData(10234);
                        }
                    } else if(shopType.equals(XShopType.Foods)) {
                        if(core.foodsShops.size()
                                < (page*config.customConfig.getIntegerList("gui." + typeBarrier +".slot").size())+1 ) {
                            itemMeta.setCustomModelData(10234);
                        }
                    } else if(shopType.equals(XShopType.New_block)) {
                        //Bukkit.broadcastMessage("SIZE: " + XShopDynamicShopCore.newBlockShops.size());
                        if(core.newBlockShops.size()
                                < (page*config.customConfig.getIntegerList("gui." + typeBarrier +".slot").size())+1 ) {
                            itemMeta.setCustomModelData(10234);
                        }
                    }
                }

            } else if(items.equalsIgnoreCase("previous_page_s")) {
                if(core.shopPage.get(p.getUniqueId()) <= 1) {
                    if(shopType.equals(XShopType.New_block)) {
                        itemMeta.setCustomModelData(config.customConfig.getInt("gui.new_block_items.previous_page_s.customModelData"));
                    } else {
                        itemMeta.setCustomModelData(config.customConfig.getInt("gui.items_special.previous_page_s.customModelData"));
                    }
                } else if(core.shopPage.get(p.getUniqueId()) > 1) {
                    if(shopType.equals(XShopType.New_block)) {
                        itemMeta.setCustomModelData(10218);
                    }
                }
            }
            if(config.customConfig.get("gui." + typeItems +"." + items + ".lore") != null) {

                for(String line : config.customConfig.getStringList("gui." + typeItems +"." + items + ".lore")) {
                    line = line.replace("&", "§");
                    lore.add(line);
                }
            }

            itemMeta.setLore(lore);
            it.setItemMeta(itemMeta);

            inv.setItem(config.customConfig.getInt("gui." + typeItems +"." + items + ".slot"), it);
        }

        if(isUpdate) {
            if (!core.getPlayerOpenGUI().contains(p)) {
                core.getPlayerOpenGUI().add(p);
            }
        }

        p.openInventory(inv);
    }

}

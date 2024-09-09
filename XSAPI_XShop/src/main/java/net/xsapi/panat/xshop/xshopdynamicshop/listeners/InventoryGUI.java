package net.xsapi.panat.xshop.xshopdynamicshop.listeners;

import net.xsapi.panat.xshop.xshopdynamicshop.configuration.*;
import net.xsapi.panat.xshop.xshopdynamicshop.core.*;
import net.xsapi.panat.xshop.xshopdynamicshop.gui.XShop;
import net.xsapi.panat.xshop.xshopdynamicshop.gui.XShopConfirm;
import net.xsapi.panat.xsseasons.api.XSAPISeasons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;

public class InventoryGUI implements Listener {

    @EventHandler
    public void onGUI(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();

        if(e.getView().getTitle().equalsIgnoreCase(config.customConfig.getString("gui.gui_title").replace("&","§"))
        || e.getView().getTitle().equalsIgnoreCase(block.customConfig.getString("gui.title").replace("&","§"))
        || e.getView().getTitle().equalsIgnoreCase(farming.customConfig.getString("gui.title").replace("&","§"))
        || e.getView().getTitle().equalsIgnoreCase(minerals.customConfig.getString("gui.title").replace("&","§"))
        || e.getView().getTitle().equalsIgnoreCase(miscellaneous.customConfig.getString("gui.title").replace("&","§"))
        || e.getView().getTitle().equalsIgnoreCase(mobs.customConfig.getString("gui.title").replace("&","§"))
        || e.getView().getTitle().equalsIgnoreCase(seasonitems.customConfig.getString("gui.title").replace("&","§"))
        || e.getView().getTitle().equalsIgnoreCase(fishing.customConfig.getString("gui.title").replace("&","§"))
        || e.getView().getTitle().equalsIgnoreCase(foods.customConfig.getString("gui.title").replace("&","§"))
        || e.getView().getTitle().equalsIgnoreCase(new_block.customConfig.getString("gui.title").replace("&","§"))) {
            if(e.getSlot() < 0) {
                return;
            }
            if(e.getClickedInventory().equals(e.getView().getBottomInventory())){
                e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
            boolean isSpecial = core.isUsingSpecialShop.get(p.getUniqueId());
            String menuType = "items";
            String slotType = "slot";
            if(!isSpecial && !core.shopType.get(p.getUniqueId()).equals(XShopType.New_block)) {
                for(String menu : config.customConfig.getConfigurationSection("gui.menu").getKeys(false)) {
                    if(e.getSlot() == config.customConfig.getInt("gui.menu." + menu + ".slot")) {
                        core.shopType.put(p.getUniqueId(),XShopType.valueOf(menu));
                        core.shopPage.put(p.getUniqueId(),1);
                        XShop.openInv(p, core.shopType.get(p.getUniqueId()),
                                core.shopPage.get(p.getUniqueId()),true, core.isUsingSpecialShop.get(p.getUniqueId()));

                        return;
                    }
                }
            } else if(core.shopType.get(p.getUniqueId()).equals(XShopType.New_block)){
                menuType = "new_block_items";
                slotType = "slot_newblock";
            } else {
                menuType = "items_special";
                slotType = "slot_special";
            }

            if(core.shopType.get(p.getUniqueId()).equals(XShopType.NoneType)) {
                return;
            }

            ItemStack it = e.getCurrentItem();

            if(it != null) {
                if(it.hasItemMeta()) {
                    String name = it.getItemMeta().getDisplayName().replace("&","§");

                    int customModelData = 0;

                    if(it.getItemMeta().hasCustomModelData()) {
                        customModelData = it.getItemMeta().getCustomModelData();
                    }

                    XShopType shopType = core.shopType.get(p.getUniqueId());
                    int page = core.shopPage.get(p.getUniqueId());

                    XShopDynamic shop = null;

                    for (XShopDynamic shopList : core.shopList) {
                        if (shopList.getShopType().equals(shopType)) {
                            shop = shopList;
                        }
                    }

                    for(String pre_menu : config.customConfig.getConfigurationSection("gui." + menuType).getKeys(false)) {
                        //p.sendMessage(pre_menu + " Slot: " + config.customConfig.getInt("gui." + menuType + "." + pre_menu + ".slot") + "Click Slot: " + e.getSlot());
                        if (e.getSlot() == config.customConfig.getInt("gui." + menuType + "." + pre_menu + ".slot")
                                && name.equalsIgnoreCase(config.customConfig.getString("gui." + menuType +"." + pre_menu + ".displayName").replace("&", "§"))
                        ) {

                            if (pre_menu.equals("close")) {
                                p.closeInventory();
                            } else if (pre_menu.equals("next_page") || pre_menu.equals("next_page_s")) {

                                if(!shop.getShopItems().isEmpty()) {
                                    if(shop.getShopItems().size() >= page*config.customConfig.getIntegerList("gui." + slotType).size() ) {
                                        if(e.getInventory().getItem(34) == null) {
                                            e.setCancelled(true);
                                            return;
                                        }
                                        core.shopPage.put(p.getUniqueId(), core.shopPage.get(p.getUniqueId()) + 1);
                                        XShop.openInv(p, core.shopType.get(p.getUniqueId()),
                                                core.shopPage.get(p.getUniqueId()),true, core.isUsingSpecialShop.get(p.getUniqueId()));
                                    }
                                }

                            } else if (pre_menu.equals("previous_page") || pre_menu.equals("previous_page_s") ) {
                                if (core.shopPage.get(p.getUniqueId()) > 1) {
                                    core.shopPage.put(p.getUniqueId(), core.shopPage.get(p.getUniqueId()) - 1);
                                    XShop.openInv(p, core.shopType.get(p.getUniqueId()),
                                            core.shopPage.get(p.getUniqueId()),true, core.isUsingSpecialShop.get(p.getUniqueId()));
                                }
                            }
                            return;
                        }
                    }


                    if(!shop.getShopItems().isEmpty()) {

                        int indexClickedSlot = config.customConfig.getIntegerList("gui." + slotType).indexOf(e.getSlot());

                        if(indexClickedSlot == -1) {
                            return;
                        }
                        XShopItems itemClicked = null;
                        if(!isSpecial) {
                            if(shopType.equals(XShopType.New_block)) {
                                itemClicked = core.newBlockShops.get(indexClickedSlot + ((page * config.customConfig.getIntegerList("gui." + slotType).size())
                                        - config.customConfig.getIntegerList("gui." + slotType).size()));
                            } else {
                                itemClicked = shop.getShopItems().get(indexClickedSlot + ((page * config.customConfig.getIntegerList("gui." + slotType).size())
                                        - config.customConfig.getIntegerList("gui." + slotType).size()));
                            }
                        } else {
                            if(core.shopType.get(p.getUniqueId()).equals(XShopType.Seasonitems)) {
                                itemClicked = core.seasonShops.get(core.seasonsAPI.getSeason().getSeasonRealName())
                                        .get(indexClickedSlot + ((page * config.customConfig.getIntegerList("gui." + slotType).size())
                                                - config.customConfig.getIntegerList("gui." + slotType).size()));
                            } else if(core.shopType.get(p.getUniqueId()).equals(XShopType.Fishing)) {
                                itemClicked = core.fishShops.get(core.seasonsAPI.getSeason().getSeasonRealName())
                                        .get(indexClickedSlot + ((page * config.customConfig.getIntegerList("gui." + slotType).size())
                                                - config.customConfig.getIntegerList("gui." + slotType).size()));
                            } else if(core.shopType.get(p.getUniqueId()).equals(XShopType.Foods)) {
                                itemClicked = core.foodsShops.get(indexClickedSlot + ((page * config.customConfig.getIntegerList("gui." + slotType).size())
                                                - config.customConfig.getIntegerList("gui." + slotType).size()));
                            }
                            //p.sendMessage(itemClicked.getPrivateName());
                        }

                        Material mat = null;

                        if(itemClicked.getItemsType().equals(XShopItemsType.CUSTOM)) {
                            XShopItemsCustom itemClickedCustom = (XShopItemsCustom) itemClicked;

                            if(itemClickedCustom.getCustomStorage()) {
                                ItemStack itemStack = storages.customConfig.getItemStack(itemClickedCustom.getStorageName());

                                mat = itemStack.getType();

                            } else {
                                mat = itemClicked.getMat();
                            }

                        } else {
                            mat = itemClicked.getMat();
                        }

                        if(e.getClick().equals(ClickType.LEFT)) {
                            if(itemClicked.getItemsType().equals(XShopItemsType.SELL_ONLY)) {
                                XShopConfirm.openGUI(p,mat,itemClicked.getPrivateName(),XShopConfirmType.SELL_ONLY);
                            } else if(itemClicked.getItemsType().equals(XShopItemsType.BUY_ONLY)) {
                                XShopConfirm.openGUI(p,mat,itemClicked.getPrivateName(),XShopConfirmType.BUY_ONLY);
                            } else {
                                XShopConfirm.openGUI(p,mat,itemClicked.getPrivateName(),XShopConfirmType.NORMAL);
                            }
                        }

                        //p.sendMessage(itemClicked.getPrivateName());

                    }

                }
            }

        }
    }

    @EventHandler
    public void onGUIConfirm(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();

        if(e.getView().getTitle().equalsIgnoreCase(config.customConfig.getString("gui_confirm.title").replace("&","§"))) {
            e.setCancelled(true);
            if(e.getSlot() < 0) {
                return;
            }
            if(e.getClickedInventory().equals(e.getView().getBottomInventory())){
                return;
            }
            ItemStack it = e.getCurrentItem();

            if(it != null) {
                if(it.hasItemMeta()) {
                    if(e.getSlot() == 48) {
                        XShop.openInv(p, core.shopType.get(p.getUniqueId()),
                                core.shopPage.get(p.getUniqueId()),false, core.isUsingSpecialShop.get(p.getUniqueId()));
                    } else {

                        if(it.getItemMeta().getDisplayName().equalsIgnoreCase(config.customConfig.getString("gui.none_sell.displayName").replace("&","§"))
                        || it.getItemMeta().getDisplayName().equalsIgnoreCase(config.customConfig.getString("gui.none_buy.displayName").replace("&","§"))) {
                            return;
                        }

                        XShopItems shopItems = core.getItemsByPrivateNameAndShop(core.shopType.get(p.getUniqueId()),
                                core.shopPrivateName.get(p.getUniqueId()));
                        DecimalFormat df = new DecimalFormat("#.00");
                        if(e.getSlot() >= 11 && e.getSlot() <= 17 && e.getClick().equals(ClickType.LEFT)) {
                            if(System.currentTimeMillis() - core.getBuyTempDisable().get(p) <= 0L) {
                                p.sendMessage((core.prefix + messages.customConfig.getString("cant_buy_temp_dsabled")).replace("&","§"));
                                return;
                            }

                            double price = 0;
                            Material mat = null;

                            if(p.getInventory().firstEmpty() == -1) { //full
                                p.sendMessage((core.prefix + messages.customConfig.getString("inventory_full")).replace("&","§"));
                                return;
                            }

                            if(shopItems.getStock() == -1) {
                                price = shopItems.getValue()*shopItems.getMedian()*Math.pow(2,e.getSlot()-11);
                            } else {
                                if(shopItems.getStock()-Math.pow(2,e.getSlot()-11) >= 1) {
                                    price = core.shopConfirmPrice.get(p.getUniqueId()).get((e.getSlot()-11));
                                    //Bukkit.broadcastMessage("PRICE: " + price);
                                } else {
                                    p.sendMessage((core.prefix + messages.customConfig.getString("out_of_stock")).replace("&","§"));
                                    return;
                                }
                            }

                            if(price <= 0) {
                                return;
                            }

                            boolean isSeason = false;

                            if(!shopItems.getCustomTags().isEmpty()) {
                                String season = shopItems.getCustomTags().split(":")[1];
                                XSAPISeasons seasonsAPI = new XSAPISeasons();

                                if(seasonsAPI.getSeason().getSeasonRealName().equalsIgnoreCase(season)) {
                                    isSeason = true;
                                }
                            }
                            if(isSeason) {
                                price = price * 125 /100;
                            }

                            if(shopItems.getPriceType().equals(XShopPriceType.Points)) {
                                if((core.getPlayerPoint().look(p.getUniqueId()))-price < 0) {
                                    p.sendMessage((core.prefix + messages.customConfig.getString("point_not_enough")).replace("&","§"));
                                    return;
                                }
                            } else {
                                if(core.getEconomy().getBalance(p.getName())-price < 0) {
                                    p.sendMessage((core.prefix + messages.customConfig.getString("money_not_enough")).replace("&","§"));
                                    return;
                                }
                            }


                            if(shopItems.getStock() != -1) {
                                shopItems.setStock(shopItems.getStock()-Math.pow(2,e.getSlot()-11));
                            }

                            shopItems.setVolumeBuy(shopItems.getVolumeBuy()+(int) Math.pow(2,e.getSlot()-11));

                            if(shopItems.getPriceType().equals(XShopPriceType.Points)) {
                                core.getPlayerPoint().take(p.getUniqueId(),(int) price);
                            } else {
                                core.getEconomy().withdrawPlayer(p.getName(),price);
                            }

                            if(shopItems.getItemsType().equals(XShopItemsType.CUSTOM)) {

                                XShopItemsCustom xsitemcustom = (XShopItemsCustom) shopItems;
                                for(String cmd : xsitemcustom.getCmd()) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd.replace("%amount%",""+(int) Math.pow(2,e.getSlot()-11)).replace("%player%",p.getName()));
                                }
                                if(xsitemcustom.getCustomStorage()) {
                                    ItemStack itMS = storages.customConfig.getItemStack(xsitemcustom.getStorageName());
                                    mat = itMS.getType();
                                    itMS.setAmount((int) Math.pow(2,e.getSlot()-11));
                                    p.getInventory().addItem(itMS);
                                } else {

                                    mat = shopItems.getMat();
                                }
                            } else {
                                mat = shopItems.getMat();
                                ItemStack itmstack = new ItemStack(mat,(int) Math.pow(2,e.getSlot()-11));

                                if(shopItems.getCustomModelData() != -1) {
                                    ItemMeta itmstackMeta = itmstack.getItemMeta();
                                    itmstackMeta.setCustomModelData(shopItems.getCustomModelData());
                                    itmstack.setItemMeta(itmstackMeta);
                                }

                                if(!shopItems.getCustomTags().isEmpty()) {

                                    if(shopItems.getCustomTags().split(":")[0].equalsIgnoreCase("XS_SEASON")) {
                                        String name = ChatColor.stripColor(shopItems.getCustomTags().split(":")[2].replace("&","§"));
                                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"iagive " + p.getName() + " customcrops:" + name.toLowerCase() + " " + itmstack.getAmount() + " silent");
                                    }
                                    if(shopItems.getCustomTags().split(":")[0].equalsIgnoreCase("XS_FISH")) {
                                        String name = ChatColor.stripColor(shopItems.getCustomTags().split(":")[2].replace("&","§"));
                                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"iagive " + p.getName() + " customfishing:" + name.toLowerCase().replace(" ","_") + " " + itmstack.getAmount() + " silent");
                                    }
                                } else {
                                    p.getInventory().addItem(itmstack);
                                }
                            }
                            XShopConfirm.openGUI(p,mat, core.shopPrivateName.get(p.getUniqueId()),core.shopConfirmType.get(p.getUniqueId()));
                            p.sendMessage((core.prefix + messages.customConfig.getString("buy_complete")).replace("%price%",df.format(price))
                                    .replace("%price_type%",messages.customConfig.getString("price_" + shopItems.getPriceType().toString().toLowerCase())).replace("&","§"));

                            String title = e.getCurrentItem().getItemMeta().getDisplayName();
                            title = title.replace("x1","")
                                    .replace("x2","")
                                    .replace("x4","")
                                    .replace("x8","")
                                    .replace("x16","")
                                    .replace("x32","")
                                    .replace("x64","");
                            int clickAmount = (int) Math.pow(2,e.getSlot()-11);
                            if(core.getBuyAmountCooldown().get(p).containsKey(title)) {

                                int current =  core.getBuyAmountCooldown().get(p).get(title);
                                core.getBuyAmountCooldown().get(p).put(title,clickAmount+current);
                                // p.sendMessage("Contain current is " +XShopDynamicShopCore.getBuyAmountCooldown().get(p).get(title));
                                // p.sendMessage("Click is " + clickAmount);
                            } else {
                                core.getBuyAmountCooldown().get(p).put(title,clickAmount);
                                // p.sendMessage("Not have added " + title + " " + clickAmount);
                            }

                            if(core.getBuyAmountCooldown().get(p).get(title) >= 64) {
                                // p.sendMessage("COOLDOWN 2 secs");
                                core.getBuyTempDisable().put(p,System.currentTimeMillis()+2000L);
                                core.getBuyAmountCooldown().get(p).put(title,0);
                            }

                        } else if(e.getSlot() >= 29 && e.getSlot() <= 35 && e.getClick().equals(ClickType.LEFT)) {
                            if(System.currentTimeMillis() - core.getSellTempDisable().get(p) <= 0L) {
                                p.sendMessage((core.prefix + messages.customConfig.getString("cant_sell_temp_dsabled")).replace("&","§"));
                                return;
                            }

                            double price = 0;
                            ItemStack itmstack = null;
                            boolean isNormalItemSell = true;

                            if(shopItems.getItemsType().equals(XShopItemsType.CUSTOM)) {
                                XShopItemsCustom shopItemsCustom = (XShopItemsCustom) shopItems;

                                if(shopItemsCustom.getIsCustomItemStorageSell()) {
                                    itmstack = shopItemsCustom.getItemStack();
                                    isNormalItemSell = false;

                                } else {
                                    p.sendMessage("§cItem cannot sell!");
                                    return;
                                }

                            } else {
                                itmstack = new ItemStack(shopItems.getMat());

                                if(shopItems.getCustomModelData() != -1) {
                                    ItemMeta itmstackMeta = itmstack.getItemMeta();
                                    itmstackMeta.setCustomModelData(shopItems.getCustomModelData());
                                    itmstack.setItemMeta(itmstackMeta);
                                }

                            }
                            int itemsToRemove = (int) Math.pow(2,e.getSlot()-29);

                            if(shopItems.getCustomTags().isEmpty()) {
                                if(!p.getInventory().containsAtLeast(itmstack,(int) Math.pow(2,e.getSlot()-29))) {
                                    p.sendMessage((core.prefix + messages.customConfig.getString("not_enough_item")).replace("%price%",df.format(price))
                                            .replace("&","§"));
                                    return;
                                }
                            } else {
                                int have = 0;
                                for(ItemStack invItem : p.getInventory().getContents()) {
                                    if (invItem != null) {
                                        if (invItem.getType().equals(itmstack.getType())) {
                                            if (shopItems.getCustomModelData() != -1) {
                                                if (!invItem.hasItemMeta()) {
                                                    continue;
                                                }
                                                if(invItem.getItemMeta().hasCustomModelData()) {
                                                    if (invItem.getItemMeta().getCustomModelData() != shopItems.getCustomModelData()) {
                                                        continue;
                                                    }

                                                    have += invItem.getAmount();
                                                }
                                            }
                                        }
                                    }
                                }

                                if(have < itemsToRemove) {
                                    p.sendMessage((core.prefix + messages.customConfig.getString("not_enough_item")).replace("%price%",df.format(price))
                                            .replace("&","§"));
                                    return;
                                }
                            }



                            if((shopItems.getStock() == -1)) {
                                price = (shopItems.getValue()*shopItems.getMedian()*75/100)*Math.pow(2,e.getSlot()-29);
                            } else {
                                //price = Math.pow(2,e.getSlot()-10)*(((shopItems.getValue()*shopItems.getMedian())/shopItems.getStock()*75/100));
                                price = core.shopConfirmPriceSell.get(p.getUniqueId()).get(e.getSlot()-29);
                                shopItems.setStock(shopItems.getStock()+Math.pow(2,e.getSlot()-29));
                            }

                            boolean isSeason = false;

                            if(!shopItems.getCustomTags().isEmpty()) {
                                String season = shopItems.getCustomTags().split(":")[1];
                                XSAPISeasons seasonsAPI = new XSAPISeasons();

                                if(seasonsAPI.getSeason().getSeasonRealName().equalsIgnoreCase(season)) {
                                    isSeason = true;
                                }
                            }
                            if(isSeason) {
                                price = price * 125 /100;
                            }

                            shopItems.setVolumeSell(shopItems.getVolumeSell()+itemsToRemove);

                            if(isNormalItemSell) {
                                for(ItemStack invItem : p.getInventory().getContents()) {
                                    if(invItem != null) {
                                        if(invItem.getType().equals(itmstack.getType())) {
                                            if(shopItems.getCustomModelData() != -1) {
                                                if(!invItem.hasItemMeta()) {
                                                    continue;
                                                }
                                                if(invItem.getItemMeta().hasCustomModelData()) {
                                                    if(invItem.getItemMeta().getCustomModelData() != shopItems.getCustomModelData()) {
                                                        continue;
                                                    }
                                                }
                                            }

                                            if(shopItems.getCustomTags().isEmpty()) {
                                                if(invItem.hasItemMeta()) {
                                                    if(!itmstack.hasItemMeta()) {
                                                        continue;
                                                    }
                                                    if(invItem.getItemMeta().hasDisplayName()) {
                                                        continue;
                                                    }
                                                    if(invItem.getItemMeta().hasEnchants()) {
                                                        continue;
                                                    }
                                                    if(invItem.getItemMeta().hasLore()) {
                                                        continue;
                                                    }
                                                    if(invItem.getItemMeta().hasAttributeModifiers()) {
                                                        continue;
                                                    }
                                                }
                                            }

                                            int preAmount = invItem.getAmount();
                                            int newAmount = Math.max(0, preAmount - itemsToRemove);
                                            itemsToRemove = Math.max(0, itemsToRemove - preAmount);
                                            invItem.setAmount(newAmount);
                                            if(itemsToRemove == 0) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                for(ItemStack invItem : p.getInventory().getContents()) {
                                    if (invItem != null) {
                                        if(invItem.getType().equals(itmstack.getType())) {
                                            if(itmstack.hasItemMeta()) {
                                                if(!invItem.hasItemMeta()) {
                                                    continue;
                                                }
                                                if(itmstack.getItemMeta().hasDisplayName() || invItem.getItemMeta().hasDisplayName()) {
                                                    if(!invItem.getItemMeta().hasDisplayName() || !itmstack.getItemMeta().hasDisplayName()) {
                                                        continue;
                                                    }
                                                    if(!itmstack.getItemMeta().getDisplayName().equalsIgnoreCase(invItem.getItemMeta().getDisplayName())) {
                                                        continue;
                                                    }
                                                }
                                                if(itmstack.getItemMeta().hasLore() || invItem.getItemMeta().hasLore()) {
                                                    if(!invItem.getItemMeta().hasLore() || !itmstack.getItemMeta().hasLore()) {
                                                        continue;
                                                    }
                                                    if(!itmstack.getItemMeta().getLore().equals(invItem.getItemMeta().getLore())) {
                                                        continue;
                                                    }
                                                }
                                                if(itmstack.getItemMeta().hasEnchants() || invItem.getItemMeta().hasEnchants()) {
                                                    if(!invItem.getItemMeta().hasEnchants() || !itmstack.getItemMeta().hasEnchants()) {
                                                        continue;
                                                    }
                                                    if(!itmstack.getEnchantments().equals(invItem.getEnchantments())) {
                                                        continue;
                                                    }
                                                }
                                                if(itmstack.getItemMeta().hasCustomModelData() || invItem.getItemMeta().hasCustomModelData()) {
                                                    if(!invItem.getItemMeta().hasCustomModelData() || !itmstack.getItemMeta().hasCustomModelData()) {
                                                        continue;
                                                    }
                                                    if(itmstack.getItemMeta().getCustomModelData() != invItem.getItemMeta().getCustomModelData()) {
                                                        continue;
                                                    }
                                                }
                                            } else {
                                                if(invItem.hasItemMeta()) {
                                                    continue;
                                                }
                                            }
                                            int preAmount = invItem.getAmount();
                                            int newAmount = Math.max(0, preAmount - itemsToRemove);
                                            itemsToRemove = Math.max(0, itemsToRemove - preAmount);
                                            invItem.setAmount(newAmount);
                                            if(itemsToRemove == 0) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            if(shopItems.getPriceType().equals(XShopPriceType.Points)) {
                                core.getPlayerPoint().give(p.getUniqueId(),(int) price);
                            } else {
                                core.getEconomy().depositPlayer(p.getName(),price);
                            }

                            p.sendMessage((core.prefix + messages.customConfig.getString("sell_complete")).replace("%price%",df.format(price))
                                    .replace("%price_type%",messages.customConfig.getString("price_" + shopItems.getPriceType().toString().toLowerCase())).replace("&","§"));

                            if(isNormalItemSell) {
                                XShopConfirm.openGUI(p,itmstack.getType(), core.shopPrivateName.get(p.getUniqueId()),core.shopConfirmType.get(p.getUniqueId()));
                            } else {
                                if(((XShopItemsCustom) shopItems).getCustomStorage()) { //use customItemStorage
                                    XShopConfirm.openGUI(p,itmstack.getType(), core.shopPrivateName.get(p.getUniqueId()),core.shopConfirmType.get(p.getUniqueId()));
                                } else {
                                    XShopConfirm.openGUI(p,shopItems.getMat(), core.shopPrivateName.get(p.getUniqueId()),core.shopConfirmType.get(p.getUniqueId()));
                                }
                            }

                            String title = e.getCurrentItem().getItemMeta().getDisplayName();
                            title = title.replace("x1","")
                                    .replace("x2","")
                                    .replace("x4","")
                                    .replace("x8","")
                                    .replace("x16","")
                                    .replace("x32","")
                                    .replace("x64","");
                            int clickAmount = (int) Math.pow(2,e.getSlot()-29);
                            if(core.getSellAmountCooldown().get(p).containsKey(title)) {

                                int current =  core.getSellAmountCooldown().get(p).get(title);
                                core.getSellAmountCooldown().get(p).put(title,clickAmount+current);
                                // p.sendMessage("Contain current is " +XShopDynamicShopCore.getBuyAmountCooldown().get(p).get(title));
                                // p.sendMessage("Click is " + clickAmount);
                            } else {
                                core.getSellAmountCooldown().get(p).put(title,clickAmount);
                                // p.sendMessage("Not have added " + title + " " + clickAmount);
                            }

                            if(core.getSellAmountCooldown().get(p).get(title) >= 64) {
                                // p.sendMessage("COOLDOWN 2 secs");
                                core.getSellTempDisable().put(p,System.currentTimeMillis()+2000L);
                                core.getSellAmountCooldown().get(p).put(title,0);
                            }
                        }

                    }
                }
            }
        }
    }

}

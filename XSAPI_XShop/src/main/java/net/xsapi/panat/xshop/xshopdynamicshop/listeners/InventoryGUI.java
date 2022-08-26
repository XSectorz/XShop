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
        || e.getView().getTitle().equalsIgnoreCase(fishing.customConfig.getString("gui.title").replace("&","§"))) {

            e.setCancelled(true);
            boolean isSpecial = XShopDynamicShopCore.isUsingSpecialShop.get(p.getUniqueId());
            String menuType = "items";
            String slotType = "slot";
            if(!isSpecial) {
                for(String menu : config.customConfig.getConfigurationSection("gui.menu").getKeys(false)) {
                    if(e.getSlot() == config.customConfig.getInt("gui.menu." + menu + ".slot")) {
                        XShopDynamicShopCore.shopType.put(p.getUniqueId(),XShopType.valueOf(menu));
                        XShopDynamicShopCore.shopPage.put(p.getUniqueId(),1);
                        XShop.openInv(p, XShopDynamicShopCore.shopType.get(p.getUniqueId()),
                                XShopDynamicShopCore.shopPage.get(p.getUniqueId()),true,XShopDynamicShopCore.isUsingSpecialShop.get(p.getUniqueId()));

                        return;
                    }
                }
            } else {
                menuType = "items_special";
                slotType = "slot_special";
            }

            if(XShopDynamicShopCore.shopType.get(p.getUniqueId()).equals(XShopType.NoneType)) {
                return;
            }

            ItemStack it = e.getCurrentItem();

            if(it != null) {
                if(it.hasItemMeta()) {
                    String name = it.getItemMeta().getDisplayName().replace("&","§");
                    int customModelData = it.getItemMeta().getCustomModelData();

                    XShopType shopType = XShopDynamicShopCore.shopType.get(p.getUniqueId());
                    int page = XShopDynamicShopCore.shopPage.get(p.getUniqueId());

                    XShopDynamic shop = null;

                    for (XShopDynamic shopList : XShopDynamicShopCore.shopList) {
                        if (shopList.getShopType().equals(shopType)) {
                            shop = shopList;
                        }
                    }

                    for(String pre_menu : config.customConfig.getConfigurationSection("gui." + menuType).getKeys(false)) {

                        if (e.getSlot() == config.customConfig.getInt("gui." + menuType + "." + pre_menu + ".slot")
                                && name.equalsIgnoreCase(config.customConfig.getString("gui." + menuType +"." + pre_menu + ".displayName").replace("&", "§"))
                                && customModelData == config.customConfig.getInt("gui." +menuType+"." + pre_menu + ".customModelData")) {

                            if (pre_menu.equals("close")) {
                                p.closeInventory();
                            } else if (pre_menu.equals("next_page") || pre_menu.equals("next_page_s")) {

                                if(!shop.getShopItems().isEmpty()) {
                                    if(shop.getShopItems().size() >= page*config.customConfig.getIntegerList("gui." + slotType).size() ) {
                                        XShopDynamicShopCore.shopPage.put(p.getUniqueId(), XShopDynamicShopCore.shopPage.get(p.getUniqueId()) + 1);
                                        XShop.openInv(p, XShopDynamicShopCore.shopType.get(p.getUniqueId()),
                                                XShopDynamicShopCore.shopPage.get(p.getUniqueId()),true,XShopDynamicShopCore.isUsingSpecialShop.get(p.getUniqueId()));
                                    }
                                }

                            } else if (pre_menu.equals("previous_page") || pre_menu.equals("previous_page_s") ) {
                                if (XShopDynamicShopCore.shopPage.get(p.getUniqueId()) > 1) {
                                    XShopDynamicShopCore.shopPage.put(p.getUniqueId(), XShopDynamicShopCore.shopPage.get(p.getUniqueId()) - 1);
                                    XShop.openInv(p, XShopDynamicShopCore.shopType.get(p.getUniqueId()),
                                            XShopDynamicShopCore.shopPage.get(p.getUniqueId()),true,XShopDynamicShopCore.isUsingSpecialShop.get(p.getUniqueId()));
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
                            itemClicked = shop.getShopItems().get(indexClickedSlot + ((page * config.customConfig.getIntegerList("gui." + slotType).size())
                                    - config.customConfig.getIntegerList("gui." + slotType).size()));
                        } else {
                            if(XShopDynamicShopCore.shopType.get(p.getUniqueId()).equals(XShopType.Seasonitems)) {
                                itemClicked = XShopDynamicShopCore.seasonShops.get(XShopDynamicShopCore.seasonsAPI.getSeason().getSeasonRealName())
                                        .get(indexClickedSlot + ((page * config.customConfig.getIntegerList("gui." + slotType).size())
                                                - config.customConfig.getIntegerList("gui." + slotType).size()));
                            } else if(XShopDynamicShopCore.shopType.get(p.getUniqueId()).equals(XShopType.Fishing)) {
                                itemClicked = XShopDynamicShopCore.fishShops.get(XShopDynamicShopCore.seasonsAPI.getSeason().getSeasonRealName())
                                        .get(indexClickedSlot + ((page * config.customConfig.getIntegerList("gui." + slotType).size())
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
                                XShopConfirm.openGUI(p,mat,itemClicked.getPrivateName(),false);
                            } else {
                                if(itemClicked.getItemsType().equals(XShopItemsType.CUSTOM)) {
                                    XShopItemsCustom itemClickedCustom = (XShopItemsCustom) itemClicked;

                                    if(itemClickedCustom.getCustomType().equalsIgnoreCase("normal")
                                    || itemClickedCustom.getCustomType().equalsIgnoreCase("buy")) {
                                        XShopConfirm.openGUI(p,mat,itemClicked.getPrivateName(),true);
                                    } else {

                                        XShopConfirm.openGUI(p,mat,itemClicked.getPrivateName(),false);
                                    }
                                    return;
                                }
                                XShopConfirm.openGUI(p,mat,itemClicked.getPrivateName(),true);
                            }
                        } else if(e.getClick().equals(ClickType.RIGHT)) {
                            if(!itemClicked.getItemsType().equals(XShopItemsType.SELL_ONLY)
                            && !itemClicked.getItemsType().equals(XShopItemsType.BUY_ONLY)) {
                                if(itemClicked.getItemsType().equals(XShopItemsType.CUSTOM)) {
                                    XShopItemsCustom itemClickedCustom = (XShopItemsCustom) itemClicked;
                                    if(!itemClickedCustom.getCustomType().equalsIgnoreCase("normal")) {
                                        return;
                                    }
                                }
                                XShopConfirm.openGUI(p,mat,itemClicked.getPrivateName(),false);
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

        if(e.getView().getTitle().equalsIgnoreCase(config.customConfig.getString("gui_confirm.title_buy").replace("&","§")
        ) || e.getView().getTitle().equalsIgnoreCase(config.customConfig.getString("gui_confirm.title_sell").replace("&","§"))) {

            ItemStack it = e.getCurrentItem();
            e.setCancelled(true);

            if(it != null) {
                if(it.hasItemMeta()) {
                    if(e.getSlot() == 30) {
                        XShop.openInv(p, XShopDynamicShopCore.shopType.get(p.getUniqueId()),
                                XShopDynamicShopCore.shopPage.get(p.getUniqueId()),false,XShopDynamicShopCore.isUsingSpecialShop.get(p.getUniqueId()));
                    } else {
                        if(e.getSlot() >= 10 && e.getSlot() <= 16 && e.getClick().equals(ClickType.LEFT)) {
                            XShopItems shopItems = XShopDynamicShopCore.getItemsByPrivateNameAndShop(XShopDynamicShopCore.shopType.get(p.getUniqueId()),
                                    XShopDynamicShopCore.shopPrivateName.get(p.getUniqueId()));
                            DecimalFormat df = new DecimalFormat("#.00");
                            if(XShopDynamicShopCore.shopConfirmType.get(p.getUniqueId()).equals(XShopConfirmType.BUY)) {

                                double price = 0;
                                Material mat = null;

                                if(p.getInventory().firstEmpty() == -1) { //full
                                    p.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("inventory_full")).replace("&","§"));
                                    return;
                                }

                                if(shopItems.getStock() == -1) {
                                    price = shopItems.getValue()*shopItems.getMedian()*Math.pow(2,e.getSlot()-10);
                                } else {
                                    if(shopItems.getStock()-Math.pow(2,e.getSlot()-10) >= 1) {
                                        price = Math.pow(2,e.getSlot()-10)*((shopItems.getValue()*shopItems.getMedian())/shopItems.getStock());
                                    } else {
                                        p.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("out_of_stock")).replace("&","§"));
                                        return;
                                    }
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
                                    if((XShopDynamicShopCore.getPlayerPoint().look(p.getUniqueId()))-price < 0) {
                                        p.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("point_not_enough")).replace("&","§"));
                                        return;
                                    }
                                } else {
                                    if(XShopDynamicShopCore.getEconomy().getBalance(p.getName())-price < 0) {
                                        p.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("money_not_enough")).replace("&","§"));
                                        return;
                                    }
                                }


                                if(shopItems.getStock() != -1) {
                                    shopItems.setStock(shopItems.getStock()-Math.pow(2,e.getSlot()-10));
                                }

                                shopItems.setVolumeBuy(shopItems.getVolumeBuy()+(int) Math.pow(2,e.getSlot()-10));

                                if(shopItems.getPriceType().equals(XShopPriceType.Points)) {
                                    XShopDynamicShopCore.getPlayerPoint().take(p.getUniqueId(),(int) price);
                                } else {
                                    XShopDynamicShopCore.getEconomy().withdrawPlayer(p.getName(),price);
                                }

                                if(shopItems.getItemsType().equals(XShopItemsType.CUSTOM)) {
                                    XShopItemsCustom xsitemcustom = (XShopItemsCustom) shopItems;
                                    for(String cmd : xsitemcustom.getCmd()) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd.replace("%amount%",""+(int) Math.pow(2,e.getSlot()-10)).replace("%player%",p.getName()));
                                    }
                                    if(xsitemcustom.getCustomStorage()) {
                                        mat = storages.customConfig.getItemStack(xsitemcustom.getStorageName()).getType();
                                    } else {
                                        mat = shopItems.getMat();
                                    }
                                } else {
                                    mat = shopItems.getMat();
                                    ItemStack itmstack = new ItemStack(mat,(int) Math.pow(2,e.getSlot()-10));

                                    if(shopItems.getCustomModelData() != -1) {
                                        ItemMeta itmstackMeta = itmstack.getItemMeta();
                                        itmstackMeta.setCustomModelData(shopItems.getCustomModelData());
                                        itmstack.setItemMeta(itmstackMeta);
                                    }

                                    if(!shopItems.getCustomTags().isEmpty()) {

                                        if(shopItems.getCustomTags().split(":")[0].equalsIgnoreCase("XS_SEASON")) {
                                            String name = ChatColor.stripColor(shopItems.getCustomTags().split(":")[2].replace("&","§"));
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"iagive " + p.getName() + " croper:" + name.toLowerCase() + " " + itmstack.getAmount() + " silent");
                                        }
                                        if(shopItems.getCustomTags().split(":")[0].equalsIgnoreCase("XS_FISH")) {
                                            String name = ChatColor.stripColor(shopItems.getCustomTags().split(":")[2].replace("&","§"));
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),"iagive " + p.getName() + " customfishing:" + name.toLowerCase().replace(" ","_") + " " + itmstack.getAmount() + " silent");
                                        }
                                    } else {
                                        p.getInventory().addItem(itmstack);
                                    }
                                }
                                XShopConfirm.openGUI(p,mat,XShopDynamicShopCore.shopPrivateName.get(p.getUniqueId()),true);
                                p.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("buy_complete")).replace("%price%",df.format(price))
                                        .replace("%price_type%",messages.customConfig.getString("price_" + shopItems.getPriceType().toString().toLowerCase())).replace("&","§"));
                                return;
                            } else if(XShopDynamicShopCore.shopConfirmType.get(p.getUniqueId()).equals(XShopConfirmType.SELL)){
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
                                int itemsToRemove = (int) Math.pow(2,e.getSlot()-10);

                                if(shopItems.getCustomTags().isEmpty()) {
                                    if(!p.getInventory().containsAtLeast(itmstack,(int) Math.pow(2,e.getSlot()-10))) {
                                        p.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("not_enough_item")).replace("%price%",df.format(price))
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
                                                    if (invItem.getItemMeta().getCustomModelData() != shopItems.getCustomModelData()) {
                                                        continue;
                                                    }

                                                    have++;
                                                }
                                            }
                                        }
                                    }

                                    if(have < itemsToRemove) {
                                        p.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("not_enough_item")).replace("%price%",df.format(price))
                                                .replace("&","§"));
                                        return;
                                    }
                                }



                                if((shopItems.getStock() == -1)) {
                                    price = (shopItems.getValue()*shopItems.getMedian()*75/100)*Math.pow(2,e.getSlot()-10);
                                } else {
                                    price = Math.pow(2,e.getSlot()-10)*(((shopItems.getValue()*shopItems.getMedian())/shopItems.getStock()*75/100));
                                    shopItems.setStock(shopItems.getStock()+Math.pow(2,e.getSlot()-10));
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
                                                    if(invItem.getItemMeta().getCustomModelData() != shopItems.getCustomModelData()) {
                                                        continue;
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
                                    XShopDynamicShopCore.getPlayerPoint().give(p.getUniqueId(),(int) price);
                                } else {
                                    XShopDynamicShopCore.getEconomy().depositPlayer(p.getName(),price);
                                }

                                p.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("sell_complete")).replace("%price%",df.format(price))
                                        .replace("%price_type%",messages.customConfig.getString("price_" + shopItems.getPriceType().toString().toLowerCase())).replace("&","§"));

                                if(isNormalItemSell) {
                                    XShopConfirm.openGUI(p,itmstack.getType(),XShopDynamicShopCore.shopPrivateName.get(p.getUniqueId()),false);
                                } else {
                                    if(((XShopItemsCustom) shopItems).getCustomStorage()) { //use customItemStorage
                                        XShopConfirm.openGUI(p,itmstack.getType(),XShopDynamicShopCore.shopPrivateName.get(p.getUniqueId()),false);
                                    } else {
                                        XShopConfirm.openGUI(p,shopItems.getMat(),XShopDynamicShopCore.shopPrivateName.get(p.getUniqueId()),false);

                                    }
                                }

                                return;
                            }
                        }

                    }
                }
            }
        }
    }

}

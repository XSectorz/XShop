package net.xsapi.panat.xshop.xshopdynamicshop.commands;

import net.xsapi.panat.xshop.xshopdynamicshop.configuration.config;
import net.xsapi.panat.xshop.xshopdynamicshop.configuration.messages;
import net.xsapi.panat.xshop.xshopdynamicshop.configuration.storages;
import net.xsapi.panat.xshop.xshopdynamicshop.core.*;
import net.xsapi.panat.xshop.xshopdynamicshop.gui.XShop;
import net.xsapi.panat.xshop.xshopdynamicshop.task.task_updateUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class XSCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String arg, String[] args) {

        if(commandSender instanceof Player) {

            Player sender = (Player) commandSender;

            if(command.getName().equalsIgnoreCase("xshop")) {
                if (args.length == 0) {
                    if(sender.hasPermission("xshop.use")) {
                        XShopDynamicShopCore.isUsingSpecialShop.put(sender.getUniqueId(),false);
                        XShopDynamicShopCore.shopPage.put(sender.getUniqueId(),1);
                        XShopDynamicShopCore.shopType.put(sender.getUniqueId(),XShopType.NoneType);
                        XShop.openInv(sender, XShopDynamicShopCore.shopType.get(sender.getUniqueId()),
                                XShopDynamicShopCore.shopPage.get(sender.getUniqueId()),true,XShopDynamicShopCore.isUsingSpecialShop.get(sender.getUniqueId()));

                        if(!XShopDynamicShopCore.getPlayerOpenGUI().contains(sender)) {
                            XShopDynamicShopCore.getPlayerOpenGUI().add(sender);
                        }

                        return true;
                    } else {
                        sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                        return false;
                    }
                } else if(args.length == 1) {
                    if((args[0].equalsIgnoreCase("help"))) {
                        sender.sendMessage((messages.customConfig.getString("cmd_xshop")).replace("&","§"));
                        sender.sendMessage((messages.customConfig.getString("cmd_xshop_help")).replace("&","§"));
                        if(sender.hasPermission("xshop.storages.save")) {
                            sender.sendMessage((messages.customConfig.getString("cmd_xshop_save")).replace("&","§"));
                        }
                        if(sender.hasPermission("xshop.storages.load")) {
                            sender.sendMessage((messages.customConfig.getString("cmd_xshop_load")).replace("&","§"));
                        }
                        if(sender.hasPermission("xshop.storages.remove")) {
                            sender.sendMessage((messages.customConfig.getString("cmd_xshop_remove")).replace("&","§"));
                        }
                        if(sender.hasPermission("xshop.storages.give")) {
                            sender.sendMessage((messages.customConfig.getString("cmd_xshop_give")).replace("&","§"));
                        }
                        if(sender.hasPermission("xshop.storages.reload")) {
                            sender.sendMessage((messages.customConfig.getString("cmd_xshop_reload")).replace("&","§"));
                        }
                        if(sender.hasPermission("xshop.setstock")) {
                            sender.sendMessage((messages.customConfig.getString("cmd_xshop_setstock")).replace("&","§"));
                        }
                        if(sender.hasPermission("xshop.setprice")) {
                            sender.sendMessage((messages.customConfig.getString("cmd_xshop_setprice")).replace("&","§"));
                        }
                        if(sender.hasPermission("xshop.farming")) {
                            sender.sendMessage((messages.customConfig.getString("cmd_xshop_farming")).replace("&","§"));
                        }
                        return true;
                    } else if(args[0].equalsIgnoreCase("reload")){
                        if(!sender.hasPermission("xshop.reload")) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                            return false;
                        }
                        XShopDynamicShopCore.saveData();
                        XShopDynamicShopCore.loadData();
                        messages.reload();
                        config.reload();
                        XShopDynamicShopCore.closeInventory();
                        sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("reload_complete")).replace("&","§"));
                        return true;
                    } else if(args[0].equalsIgnoreCase("farming")){
                        if(!sender.hasPermission("xshop.farming")) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                            return false;
                        }
                        XShopDynamicShopCore.isUsingSpecialShop.put(sender.getUniqueId(),true);
                        XShopDynamicShopCore.shopPage.put(sender.getUniqueId(),1);
                        XShopDynamicShopCore.shopType.put(sender.getUniqueId(),XShopType.Seasonitems);
                        XShop.openInv(sender, XShopDynamicShopCore.shopType.get(sender.getUniqueId()),
                                XShopDynamicShopCore.shopPage.get(sender.getUniqueId()),true,XShopDynamicShopCore.isUsingSpecialShop.get(sender
                                        .getUniqueId()));

                        if(!XShopDynamicShopCore.getPlayerOpenGUI().contains(sender)) {
                            XShopDynamicShopCore.getPlayerOpenGUI().add(sender);
                        }
                        return true;
                    } else if(args[0].equalsIgnoreCase("fishing")){
                        if(!sender.hasPermission("xshop.fishing")) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                            return false;
                        }
                        XShopDynamicShopCore.isUsingSpecialShop.put(sender.getUniqueId(),true);
                        XShopDynamicShopCore.shopPage.put(sender.getUniqueId(),1);
                        XShopDynamicShopCore.shopType.put(sender.getUniqueId(),XShopType.Fishing);
                        XShop.openInv(sender, XShopDynamicShopCore.shopType.get(sender.getUniqueId()),
                                XShopDynamicShopCore.shopPage.get(sender.getUniqueId()),true,XShopDynamicShopCore.isUsingSpecialShop.get(sender
                                        .getUniqueId()));

                        if(!XShopDynamicShopCore.getPlayerOpenGUI().contains(sender)) {
                            XShopDynamicShopCore.getPlayerOpenGUI().add(sender);
                        }
                        return true;
                    }
                } else if(args.length == 2) {
                    if((args[0].equalsIgnoreCase("save"))) {
                        if(!sender.hasPermission("xshop.storages.save")) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                            return false;
                        }
                        String name = args[1].toString();

                        if(sender.getInventory().getItemInMainHand().getType() == Material.AIR) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("storages_empty_hand")).replace("&","§"));
                            return false;
                        }

                        if(storages.customConfig.get(name) != null) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("storages_already_have")).replace("&","§"));
                            return false;
                        }

                        storages.customConfig.set(name,sender.getInventory().getItemInMainHand());
                        storages.save();
                        storages.reload();
                        sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("storages_saved"))
                                .replace("%item%",name).replace("&","§"));
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("load")) {
                        if(!sender.hasPermission("xshop.storages.load")) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                            return false;
                        }

                        String name = args[1].toString();
                        if(storages.customConfig.get(name) == null) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("storages_load_null")).replace("&","§"));
                            return false;
                        }
                        if(sender.getInventory().firstEmpty() == -1) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("inventory_full")).replace("&","§"));
                            return false;
                        }

                        ItemStack it = storages.customConfig.getItemStack(name);
                        sender.getInventory().addItem(it);
                        sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("storages_load_complete")).replace("&","§"));
                        return true;
                    } else if(args[0].equalsIgnoreCase("remove")) {
                        if(!sender.hasPermission("xshop.storages.remove")) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                            return false;
                        }

                        String name = args[1].toString();
                        if(storages.customConfig.get(name) == null) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("storages_remove_null")).replace("&","§"));
                            return false;
                        }

                        storages.customConfig.set(name,null);
                        storages.save();
                        storages.reload();

                        sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("storages_remove_complete"))
                                .replace("%item%",name).replace("&","§"));
                        return true;
                    }
                } else if(args.length == 3) {
                    if(args[0].equalsIgnoreCase("give")) {
                        if(!sender.hasPermission("xshop.storages.give")) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                            return false;
                        }
                        String name = args[1].toString();

                        if(storages.customConfig.get(name) == null) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("storages_give_null")).replace("&","§"));
                            return false;
                        }

                        String playerName = args[2].toString();
                        if(Bukkit.getPlayer(playerName) == null) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("storages_give_player_null")).replace("&", "§"));
                            return false;
                        }

                        Player target = Bukkit.getPlayer(playerName);
                        if(target.getInventory().firstEmpty() == -1) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("inventory_full")).replace("&","§"));
                            return false;
                        }

                        ItemStack it = storages.customConfig.getItemStack(name);
                        target.getInventory().addItem(it);
                        sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("storages_give_complete"))
                                .replace("%item%",name).replace("%name%",playerName).replace("&", "§"));
                        return true;
                    }
                } else if(args.length == 4) {
                     if(args[0].equalsIgnoreCase("setstock")) {
                        if(!sender.hasPermission("xshop.setstock")) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                            return false;
                        }
                        String type = args[1].toString();
                        String name = args[2].toString();
                        int amount = 0;

                        try {
                            amount = Integer.parseInt(args[3].toString());
                        } catch (NumberFormatException nfe) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("not_a_number")).replace("&", "§"));
                            return false;
                        }

                        if(amount <= 0) {
                            if(amount != -1) {
                                sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("stock_only_positive")).replace("&", "§"));
                                return false;
                            }
                        }

                        XShopType typeShop = null;
                        try {
                            type = type.toLowerCase();
                            type = type.substring(0, 1).toUpperCase() + type.substring(1);
                            typeShop = XShopType.valueOf(type);
                        } catch (IllegalArgumentException e) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("type_of_shop_null")).replace("&", "§"));
                            return false;
                        }

                        XShopItems shopItems = XShopDynamicShopCore.getItemsByPrivateNameAndShop(typeShop,name);

                        if(shopItems == null) {
                            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("item_of_shop_null")).replace("&", "§"));
                            return false;
                        }

                        shopItems.setStock(amount);

                        if(amount == -1) {
                            amount = 1;
                        }

                        shopItems.setPreviousPrice(shopItems.getValue()*shopItems.getMedian()/amount);

                        sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("setstock_complete")).replace("&", "§"));
                        XShopDynamicShopCore.saveData();
                        return true;
                    } else if(args[0].equalsIgnoreCase("setprice")) {
                         if(!sender.hasPermission("xshop.setprice")) {
                             sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                             return false;
                         }

                         String type = args[1].toString();
                         String name = args[2].toString();
                         double amount = 0;

                         try {
                             amount = Double.parseDouble(args[3].toString());
                         } catch (NumberFormatException nfe) {
                             sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("not_a_number")).replace("&", "§"));
                             return false;
                         }

                         if(amount <= 0) {
                             sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("price_only_positive")).replace("&", "§"));
                             return false;
                         }

                         XShopType typeShop = null;
                         try {
                             type = type.toLowerCase();
                             type = type.substring(0, 1).toUpperCase() + type.substring(1);
                             typeShop = XShopType.valueOf(type);
                         } catch (IllegalArgumentException e) {
                             sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("type_of_shop_null")).replace("&", "§"));
                             return false;
                         }

                         XShopItems shopItems = XShopDynamicShopCore.getItemsByPrivateNameAndShop(typeShop,name);

                         if(shopItems == null) {
                             sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("item_of_shop_null")).replace("&", "§"));
                             return false;
                         }

                         int stock = (int) shopItems.getStock();
                         if(stock == -1) {
                             stock = 1;
                         }

                         double val = amount*stock;

                         if(shopItems.getItemsType().equals(XShopItemsType.CUSTOM)) {
                             XShopItemsCustom shopItemsCustom = (XShopItemsCustom) shopItems;
                             if(shopItemsCustom.getCustomType().equalsIgnoreCase("sell")) {
                                 val = (val*100)/75;
                             }
                         } else {
                             if(shopItems.getItemsType().equals(XShopItemsType.SELL_ONLY)) {
                                 val = (val * 100)/75;
                             }
                         }

                         shopItems.setMedian(1);
                         shopItems.setValue(val);
                         shopItems.setPreviousPrice(shopItems.getValue()*shopItems.getMedian()/stock);
                         sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("setprice_complete")).replace("&", "§"));
                         XShopDynamicShopCore.saveData();
                         return true;
                     }
                }
            }
            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("cmd_not_found"))
                    .replace("&","§"));
            return false;
        } else { //Console

            if(command.getName().equalsIgnoreCase("xshop")) {
                if(args.length == 1) {
                    if(args[0].equalsIgnoreCase("reload")) {
                        XShopDynamicShopCore.saveData();
                        XShopDynamicShopCore.loadData();

                        messages.reload();
                        config.reload();
                        commandSender.sendMessage("§c[Xshop] §aReload complete!");
                        return true;
                    }
                } else if(args.length == 3) {
                    if(args[0].equalsIgnoreCase("give")) {
                        String name = args[1].toString();

                        if(storages.customConfig.get(name) == null) {
                            commandSender.sendMessage(("&cItem is null").replace("&","§"));
                            return false;
                        }

                        String playerName = args[2].toString();
                        if(Bukkit.getPlayer(playerName) == null) {
                            commandSender.sendMessage(("&cTarget null").replace("&", "§"));
                            return false;
                        }

                        Player target = Bukkit.getPlayer(playerName);
                        if(target.getInventory().firstEmpty() == -1) {
                            commandSender.sendMessage(("&cTarget inventory full").replace("&","§"));
                            return false;
                        }

                        ItemStack it = storages.customConfig.getItemStack(name);
                        target.getInventory().addItem(it);
                        commandSender.sendMessage(("&aSuccessful give item to player").replace("&", "§"));
                        return true;
                    } else if (args[0].equalsIgnoreCase("open")) {
                        if(Bukkit.getPlayer(args[2].toString()) == null) {
                            return false;
                        }

                        Player sender = Bukkit.getPlayer(args[2].toString());

                        if(args[1].equalsIgnoreCase("fishing")) {
                            if(!sender.hasPermission("xshop.fishing")) {
                                sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                                return false;
                            }
                            XShopDynamicShopCore.isUsingSpecialShop.put(sender.getUniqueId(),true);
                            XShopDynamicShopCore.shopPage.put(sender.getUniqueId(),1);
                            XShopDynamicShopCore.shopType.put(sender.getUniqueId(),XShopType.Fishing);
                            XShop.openInv(sender, XShopDynamicShopCore.shopType.get(sender.getUniqueId()),
                                    XShopDynamicShopCore.shopPage.get(sender.getUniqueId()),true,XShopDynamicShopCore.isUsingSpecialShop.get(sender
                                            .getUniqueId()));

                            if(!XShopDynamicShopCore.getPlayerOpenGUI().contains(sender)) {
                                XShopDynamicShopCore.getPlayerOpenGUI().add(sender);
                            }
                            return true;
                        } else if(args[1].equalsIgnoreCase("farming")) {
                            if(!sender.hasPermission("xshop.farming")) {
                                sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                                return false;
                            }
                            XShopDynamicShopCore.isUsingSpecialShop.put(sender.getUniqueId(),true);
                            XShopDynamicShopCore.shopPage.put(sender.getUniqueId(),1);
                            XShopDynamicShopCore.shopType.put(sender.getUniqueId(),XShopType.Seasonitems);
                            XShop.openInv(sender, XShopDynamicShopCore.shopType.get(sender.getUniqueId()),
                                    XShopDynamicShopCore.shopPage.get(sender.getUniqueId()),true,XShopDynamicShopCore.isUsingSpecialShop.get(sender
                                            .getUniqueId()));

                            if(!XShopDynamicShopCore.getPlayerOpenGUI().contains(sender)) {
                                XShopDynamicShopCore.getPlayerOpenGUI().add(sender);
                            }
                            return true;
                        } else if(args[1].equalsIgnoreCase("normal")) {
                            if(!sender.hasPermission("xshop.use")) {
                                sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                                return false;
                            }
                            XShopDynamicShopCore.isUsingSpecialShop.put(sender.getUniqueId(),false);
                            XShopDynamicShopCore.shopPage.put(sender.getUniqueId(),1);
                            XShopDynamicShopCore.shopType.put(sender.getUniqueId(),XShopType.NoneType);
                            XShop.openInv(sender, XShopDynamicShopCore.shopType.get(sender.getUniqueId()),
                                    XShopDynamicShopCore.shopPage.get(sender.getUniqueId()),true,XShopDynamicShopCore.isUsingSpecialShop.get(sender.getUniqueId()));

                            if(!XShopDynamicShopCore.getPlayerOpenGUI().contains(sender)) {
                                XShopDynamicShopCore.getPlayerOpenGUI().add(sender);
                            }
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}

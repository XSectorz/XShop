package net.xsapi.panat.xshop.xshopdynamicshop.commands;

import net.xsapi.panat.xshop.xshopdynamicshop.configuration.messages;
import net.xsapi.panat.xshop.xshopdynamicshop.configuration.storages;
import net.xsapi.panat.xshop.xshopdynamicshop.core.XShopDynamicShopCore;
import net.xsapi.panat.xshop.xshopdynamicshop.core.XShopType;
import net.xsapi.panat.xshop.xshopdynamicshop.gui.XShop;
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
                        sender.sendMessage("Opened Shop!");
                        XShopDynamicShopCore.shopPage.put(sender.getUniqueId(),1);
                        XShopDynamicShopCore.shopType.put(sender.getUniqueId(),XShopType.NoneType);
                        XShop.openInv(sender, XShopDynamicShopCore.shopType.get(sender.getUniqueId()),
                                XShopDynamicShopCore.shopPage.get(sender.getUniqueId()));
                        return true;
                    } else {
                        sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("no_perms")).replace("&","§"));
                        return false;
                    }
                } else if(args.length == 1) {
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
                    return true;
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
                }
            }
            sender.sendMessage((XShopDynamicShopCore.prefix + messages.customConfig.getString("cmd_not_found"))
                    .replace("&","§"));
            return false;
        } else { //Console

            if(command.getName().equalsIgnoreCase("xshop")) {
                if(args.length == 3) {
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
                    }
                }
            }
        }

        return false;
    }
}

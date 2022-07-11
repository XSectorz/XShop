package net.xsapi.panat.xshop.xshopdynamicshop.core;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.xsapi.panat.xshop.xshopdynamicshop.commands.XSCommands;
import net.xsapi.panat.xshop.xshopdynamicshop.configuration.loadConfig;
import net.xsapi.panat.xshop.xshopdynamicshop.configuration.messages;
import net.xsapi.panat.xshop.xshopdynamicshop.configuration.storages;
import net.xsapi.panat.xshop.xshopdynamicshop.listeners.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class XShopDynamicShopCore extends JavaPlugin {

    public static ArrayList<XShopDynamic> shopList = new ArrayList<XShopDynamic>();
    public static HashMap<UUID,XShopType> shopType = new HashMap<UUID, XShopType>();
    public static HashMap<UUID,Integer> shopPage = new HashMap<UUID, Integer>();
    public static HashMap<UUID,String> shopPrivateName = new HashMap<UUID, String>();

    public static String prefix = "";

    public static HashMap<UUID,XShopConfirmType> shopConfirmType = new HashMap<UUID, XShopConfirmType>();

    private static Economy econ;
    private static Permission perms;

    static XShopDynamicShopCore plugin;

    public static XShopDynamicShopCore getPlugin() {
        return plugin;
    }


    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            this.getLogger().info("§x§f§f§5§8§5§8[XSHOP] Vault Not Found!");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static XShopItems getItemsByPrivateNameAndShop(XShopType shopType,String privateName) {

        XShopDynamic shop = null;

        for(XShopDynamic shopListItr : shopList) {
            if(shopListItr.getShopType().equals(shopType)) {
                shop = shopListItr;
            }
        }

        for(XShopItems items : shop.getShopItems()) {
            if(items.getPrivateName().equalsIgnoreCase(privateName)) {
                return items;
            }
        }

        return null;

    }

    public void loadData() {
        File dir = new File(this.getDataFolder()+"/shops");
        File[] directoryListing = dir.listFiles();

        Bukkit.getLogger().info("§x§f§f§a§c§2§f******************************");
        Bukkit.getLogger().info("§x§f§f§a§c§2§f   XSAPI DynamicShop v1.0     ");
        Bukkit.getLogger().info("§r");
        Bukkit.getLogger().info("§x§f§f§a§c§2§f  Trying to load shop(s) data");
        Bukkit.getLogger().info("§r");
        Bukkit.getLogger().info("§x§f§f§a§c§2§f******************************");
        int shops = 0;
        int items = 0;

        if (directoryListing != null) {
            for (File child : directoryListing) {
                if(child.getName().endsWith(".yml")) {

                    File file = new File(XShopDynamicShopCore.getPlugin().getDataFolder() + "/shops", child.getName());
                    FileConfiguration fileConfig = (FileConfiguration) new YamlConfiguration();
                    try {
                        fileConfig.load(file);
                        shops++;
                    } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
                        e.printStackTrace();
                    }

                    String Name = child.getName().replace(".yml","");

                    XShopDynamic XShopDynamic = new XShopDynamic(Name,XShopType.valueOf(Name.substring(0, 1).toUpperCase() + Name.substring(1)));

                    if(fileConfig.get("items") != null) {
                        for(String itemList : fileConfig.getConfigurationSection("items").getKeys(false)) {

                            if(fileConfig.getString("items." + itemList + ".type").equals("CUSTOM")) {

                                String name = "";
                                List<String> lore = new ArrayList<>();
                                Material mat = null;

                                if(fileConfig.get("items." + itemList + ".displayName") != null) {
                                    name = fileConfig.getString("items." + itemList + ".displayName");
                                }
                                if(fileConfig.get("items." + itemList + ".lore") != null) {
                                    lore = fileConfig.getStringList("items." + itemList + ".lore");
                                }
                                if(fileConfig.get("items." + itemList + ".material") != null) {
                                    mat = Material.getMaterial(fileConfig.getString("items." + itemList + ".material"));
                                }

                                XShopItemsType typeItem = XShopItemsType.valueOf(fileConfig.getString("items." + itemList + ".type"));

                                XShopItemsCustom xsitemscustom = new XShopItemsCustom(name,new ArrayList<>(lore),mat,typeItem,itemList);

                                xsitemscustom.setValue(fileConfig.getDouble("items." + itemList + ".value"));
                                xsitemscustom.setMedian(fileConfig.getDouble("items." + itemList + ".median"));
                                xsitemscustom.setStock(fileConfig.getDouble("items." + itemList + ".stock"));
                                xsitemscustom.setCustomModelData(fileConfig.getInt("items." + itemList + ".customModelData"));

                                xsitemscustom.setCmd(new ArrayList<>(fileConfig.getStringList("items." + itemList + ".commands")));

                                if(fileConfig.get("items." + itemList + ".previousPrice") == null) {
                                    double stock = 1;

                                    if(xsitemscustom.getStock() == -1) {
                                        stock = 1;
                                    } else {
                                        stock = xsitemscustom.getStock();
                                    }
                                    xsitemscustom.setPreviousPrice(xsitemscustom.getValue()*xsitemscustom.getMedian()/stock);
                                } else {
                                    xsitemscustom.setPreviousPrice(fileConfig.getDouble("items." + itemList + ".previousPrice"));
                                }

                                if(fileConfig.get("items." + itemList + ".customItemsStorage") != null) {

                                    String nameStorage =  fileConfig.getString("items." + itemList + ".customItemsStorage");
                                    if(storages.customConfig.get(nameStorage) == null) {
                                        Bukkit.getLogger().info(("&c[Xshop] Cannot load item &e" + itemList + " &cthis customStorage name is NULL").replace("&","§"));
                                        continue;
                                    }

                                    ItemStack it = storages.customConfig.getItemStack(nameStorage);
                                    xsitemscustom.setStorageName(nameStorage);
                                    xsitemscustom.setItemStack(it);
                                }

                                if(mat == null) {
                                    if(fileConfig.get("items." + itemList + ".customItemsStorage") == null) {
                                        Bukkit.getLogger().info(("&c[Xshop] Cannot load item &e" + itemList + " &cthis item setup not complete!").replace("&","§"));
                                        continue;
                                    }
                                }

                                if(fileConfig.get("items." + itemList + ".useCustomItemsStorage") != null) {
                                    xsitemscustom.setCustomItemStorage(fileConfig.getBoolean("items." + itemList + ".useCustomItemsStorage"));
                                }

                                if(fileConfig.get("items." + itemList + ".customItemStorageSell") != null) {
                                    if(xsitemscustom.getStorageName().isEmpty()) {
                                        Bukkit.getLogger().info(("&c[Xshop] Cannot load item &e" + itemList + " &ccustomItemStorageSell is true but not have customItemsStorage!").replace("&","§"));
                                        continue;
                                    }
                                    xsitemscustom.setIsCustomItemStorageSell(fileConfig.getBoolean("items." + itemList + ".customItemStorageSell"));
                                }

                                if(fileConfig.get("items." + itemList + ".customType") == null) {
                                    Bukkit.getLogger().info(("&c[Xshop] Cannot load item &e" + itemList + " &ccustomType is NULL!").replace("&","§"));
                                    continue;
                                } else {
                                    xsitemscustom.setCustomType(fileConfig.getString("items." + itemList + ".customType"));
                                }

                                XShopDynamic.getShopItems().add(xsitemscustom);
                            } else {
                                Material mat = Material.getMaterial(fileConfig.getString("items." + itemList + ".material"));
                                XShopItemsType typeItem = XShopItemsType.valueOf(fileConfig.getString("items." + itemList + ".type"));
                                XShopItems xsitems = new XShopItems(mat,typeItem,itemList);
                                xsitems.setValue(fileConfig.getDouble("items." + itemList + ".value"));
                                xsitems.setMedian(fileConfig.getDouble("items." + itemList + ".median"));
                                xsitems.setStock(fileConfig.getDouble("items." + itemList + ".stock"));
                                xsitems.setCustomModelData(fileConfig.getInt("items." + itemList + ".customModelData"));

                                if(fileConfig.get("items." + itemList + ".previousPrice") == null) {
                                    double stock = 1;

                                    if(xsitems.getStock() == -1) {
                                        stock = 1;
                                    } else {
                                        stock = xsitems.getStock();
                                    }
                                    xsitems.setPreviousPrice(xsitems.getValue()*xsitems.getMedian()/stock);
                                } else {
                                    xsitems.setPreviousPrice(fileConfig.getDouble("items." + itemList + ".previousPrice"));
                                }

                                XShopDynamic.getShopItems().add(xsitems);
                            }

                            items++;
                        }
                    }

                    shopList.add(XShopDynamic);

                }
            }
        }
        Bukkit.getLogger().info("§x§f§f§a§c§2§f[XShop] Successfully load " + shops + " shop(s) with " + items + " item(s)");
    }

    public void saveData() {
        for(XShopDynamic shop : shopList) {

            File file = new File(this.getDataFolder()+"/shops", shop.getShopType().toString().toLowerCase() + ".yml");
            FileConfiguration fileConfig = (FileConfiguration) new YamlConfiguration();
            try {
                fileConfig.load(file);
            } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
                e.printStackTrace();
            }

            if(!shop.getShopItems().isEmpty()) {
                for(XShopItems items : shop.getShopItems()) {
                    double stock = 1;

                    if(items.getStock() == -1) {
                        stock = 1;
                    } else {
                        stock = items.getStock();
                    }

                    fileConfig.set("items." + items.getPrivateName() + ".stock",items.getStock());
                    fileConfig.set("items." + items.getPrivateName() + ".previousPrice",
                            items.getValue()*items.getMedian()/ stock);
                }
            }
            try {
                fileConfig.options().copyDefaults(true);
                fileConfig.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Bukkit.getLogger().info("§x§f§f§a§c§2§f[XShop] Successfully saved shop(s) data!");
    }


    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            this.getLogger().info("§x§f§f§5§8§5§8[XSHOP] Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        } else {
            this.getLogger().info("§x§7§a§f§f§4§b[XSHOP] Hooked into Vault <3");
        }
        this.setupPermissions();

        Bukkit.getLogger().info("§aPlugin Enabled 1.19!");

        plugin = this;
        new loadConfig();
        prefix = messages.customConfig.getString("prefix");

        getCommand("xshop").setExecutor(new XSCommands());

        loadData();

        Bukkit.getServer().getPluginManager().registerEvents(new InventoryGUI(),this);

    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("§cPlugin Disabled 1.19!");
        saveData();
    }
}

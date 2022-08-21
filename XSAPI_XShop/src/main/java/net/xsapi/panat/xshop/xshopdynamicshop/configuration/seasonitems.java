package net.xsapi.panat.xshop.xshopdynamicshop.configuration;

import net.xsapi.panat.xshop.xshopdynamicshop.core.XShopDynamicShopCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class seasonitems {

    public static File customConfigFile;

    public static FileConfiguration customConfig;

    public FileConfiguration getConfig() {
        return customConfig;
    }

    public void loadConfigu() {
        customConfigFile = new File(XShopDynamicShopCore.getPlugin().getDataFolder() + "/shops", "seasonitems.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            XShopDynamicShopCore.getPlugin().saveResource("shops/seasonitems.yml", false);
        }
        customConfig = (FileConfiguration) new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        customConfigFile = new File(XShopDynamicShopCore.getPlugin().getDataFolder() + "/shops", "seasonitems.yml");
        try {
            customConfig.options().copyDefaults(true);
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        config.customConfigFile = new File(XShopDynamicShopCore.getPlugin().getDataFolder() + "/shops", "seasonitems.yml");
        if (!config.customConfigFile.exists()) {
            config.customConfigFile.getParentFile().mkdirs();
            XShopDynamicShopCore.getPlugin().saveResource("shops/seasonitems.yml", false);
        } else {
            config.customConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(config.customConfigFile);
            try {
                config.customConfig.save(config.customConfigFile);
                XShopDynamicShopCore.getPlugin().reloadConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

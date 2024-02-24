package net.xsapi.panat.xshop.xshopdynamicshop.configuration;

import net.xsapi.panat.xshop.xshopdynamicshop.core.XShopDynamicShopCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class new_block {
    public static File customConfigFile;

    public static FileConfiguration customConfig;

    public FileConfiguration getConfig() {
        return customConfig;
    }

    public void loadConfigu() {
        customConfigFile = new File(XShopDynamicShopCore.getPlugin().getDataFolder() + "/shops", "new_block.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            XShopDynamicShopCore.getPlugin().saveResource("shops/new_block.yml", false);
        }
        customConfig = (FileConfiguration) new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        customConfigFile = new File(XShopDynamicShopCore.getPlugin().getDataFolder() + "/shops", "new_block.yml");
        try {
            customConfig.options().copyDefaults(true);
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

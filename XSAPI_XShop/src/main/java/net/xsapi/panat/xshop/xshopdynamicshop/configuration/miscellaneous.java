package net.xsapi.panat.xshop.xshopdynamicshop.configuration;

import net.xsapi.panat.xshop.xshopdynamicshop.core.core;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class miscellaneous {
    public static File customConfigFile;

    public static FileConfiguration customConfig;

    public FileConfiguration getConfig() {
        return customConfig;
    }

    public void loadConfigu() {
        customConfigFile = new File(core.getPlugin().getDataFolder() + "/shops", "miscellaneous.yml");
        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            core.getPlugin().saveResource("shops/miscellaneous.yml", false);
        }
        customConfig = (FileConfiguration) new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        customConfigFile = new File(core.getPlugin().getDataFolder() + "/shops", "miscellaneous.yml");
        try {
            customConfig.options().copyDefaults(true);
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        config.customConfigFile = new File(core.getPlugin().getDataFolder() + "/shops", "miscellaneous.yml");
        if (!config.customConfigFile.exists()) {
            config.customConfigFile.getParentFile().mkdirs();
            core.getPlugin().saveResource("shops/miscellaneous.yml", false);
        } else {
            config.customConfig = (FileConfiguration) YamlConfiguration.loadConfiguration(config.customConfigFile);
            try {
                config.customConfig.save(config.customConfigFile);
                core.getPlugin().reloadConfig();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

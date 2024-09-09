package net.xsapi.panat.xshop.xshopdynamicshop.utils;

import com.google.common.io.Files;
import net.xsapi.panat.xshop.xshopdynamicshop.configuration.config;
import net.xsapi.panat.xshop.xshopdynamicshop.core.core;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResetPriceFeatures {

    public void resetPrice() {
        File dir = new File(core.getPlugin().getDataFolder()+"/temp");
        File[] directoryListing = dir.listFiles();

        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.getName().endsWith(".yml")) {

                    File dest = new File(core.getPlugin().getDataFolder() + "/shops/" + child.getName());

                    if(!dest.exists()) {
                        dest.getParentFile().mkdirs();
                    }

                    try {
                        Files.copy(child, dest);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    FileConfiguration fileConfig = (FileConfiguration) new YamlConfiguration();
                    try {
                        fileConfig.load(dest);
                    } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
                        e.printStackTrace();
                    }

                    fileConfig.options().copyDefaults(true);
                    try {
                        fileConfig.save(dest);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Bukkit.getLogger().info("§x§5§d§f§f§6§3[XSHOP] copy temp to " + child.getName() + " successful!");
                }
            }
        }

        final List<Player> user_list = new ArrayList<Player>(core.getPlayerOpenGUI());

        if(!user_list.isEmpty()) {
            for (final Player user : user_list) {
                if (Bukkit.getPlayer(user.getName()) == null) {
                    continue;
                }
                user.closeInventory();
            }
        }

        config.customConfig.set("reset_price.time_stamp",System.currentTimeMillis()+
                config.customConfig.getLong("reset_price.repeat")*1000*86400);
        config.save();
        config.reload();

    }

}

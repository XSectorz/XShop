package net.xsapi.panat.xshop.xshopdynamicshop.task;

import net.xsapi.panat.xshop.xshopdynamicshop.configuration.*;
import net.xsapi.panat.xshop.xshopdynamicshop.core.core;
import net.xsapi.panat.xshop.xshopdynamicshop.gui.XShop;
import net.xsapi.panat.xshop.xshopdynamicshop.utils.ResetPriceFeatures;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class task_updateUI extends BukkitRunnable {


    @Override
    public void run() {

        if(config.customConfig.getBoolean("reset_price.enable")) {
            if(System.currentTimeMillis()-config.customConfig.getLong("reset_price.time_stamp") >= 0L) {
                new ResetPriceFeatures().resetPrice();
                core.loadData();
                Bukkit.getLogger().info("§x§f§f§a§c§2§f[XShop] reset price tasks successfully!");
            }
        }

        final List<Player> user_list = new ArrayList<Player>(core.getPlayerOpenGUI());

        if(!user_list.isEmpty()) {
            for (final Player user : user_list) {
                if (Bukkit.getPlayer(user.getName()) == null) {
                    continue;
                }
                XShop.openInv(user, core.shopType.get(user.getUniqueId()), core.shopPage.get(user.getUniqueId()),true,
                        core.isUsingSpecialShop.get(user.getUniqueId()));
            }
        }

    }

}

package net.xsapi.panat.xshop.xshopdynamicshop.task;

import net.xsapi.panat.xshop.xshopdynamicshop.configuration.*;
import net.xsapi.panat.xshop.xshopdynamicshop.core.XShopDynamicShopCore;
import net.xsapi.panat.xshop.xshopdynamicshop.gui.XShop;
import net.xsapi.panat.xshop.xshopdynamicshop.gui.XShopConfirm;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class task_updateUI extends BukkitRunnable {


    @Override
    public void run() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            if(p.getOpenInventory().getTitle() != null) {
                String title = p.getOpenInventory().getTitle().replace("&", "§");

                if(title.equalsIgnoreCase(config.customConfig.getString("gui.gui_title").replace("&", "§"))
                || title.equalsIgnoreCase(block.customConfig.getString("gui.title").replace("&", "§"))
                || title.equalsIgnoreCase(farming.customConfig.getString("gui.title").replace("&", "§"))
                || title.equalsIgnoreCase(minerals.customConfig.getString("gui.title").replace("&", "§"))
                || title.equalsIgnoreCase(miscellaneous.customConfig.getString("gui.title").replace("&", "§"))
                || title.equalsIgnoreCase(mobs.customConfig.getString("gui.title").replace("&", "§"))) {
                    XShop.openInv(p,XShopDynamicShopCore.shopType.get(p.getUniqueId()),XShopDynamicShopCore.shopPage.get(p.getUniqueId()),true);
                }
            }
        }

    }

}

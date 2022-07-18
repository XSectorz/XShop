package net.xsapi.panat.xshop.xshopdynamicshop.task;

import net.xsapi.panat.xshop.xshopdynamicshop.core.XShopDynamicShopCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class task_update extends BukkitRunnable {

    @Override
    public void run() {
        Bukkit.getConsoleSender().sendMessage("&c[XSHOP] &aAuto save is running...".replace("&", "\u00A7"));

        XShopDynamicShopCore.saveData();
        XShopDynamicShopCore.loadData();

        Bukkit.getConsoleSender().sendMessage("&c[XSHOP] &aSave complete!".replace("&", "\u00A7"));

    }

}

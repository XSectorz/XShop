package net.xsapi.panat.xshop.xshopdynamicshop.task;

import net.xsapi.panat.xshop.xshopdynamicshop.core.core;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class task_update extends BukkitRunnable {

    @Override
    public void run() {
        Bukkit.getConsoleSender().sendMessage("&c[XSHOP] &aAuto save is running...".replace("&", "\u00A7"));

        core.saveData();
        core.loadData();

        Bukkit.getConsoleSender().sendMessage("&c[XSHOP] &aSave complete!".replace("&", "\u00A7"));

    }

}

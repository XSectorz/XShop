package net.xsapi.panat.xshop.xshopdynamicshop.listeners;

import net.xsapi.panat.xshop.xshopdynamicshop.task.task_updateUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryClose implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();


    }

}

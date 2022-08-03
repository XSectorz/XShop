package net.xsapi.panat.xshop.xshopdynamicshop.listeners;

import net.xsapi.panat.xshop.xshopdynamicshop.configuration.*;
import net.xsapi.panat.xshop.xshopdynamicshop.core.XShopDynamicShopCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Iterator;

public class InventoryClose implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if(XShopDynamicShopCore.getPlayerOpenGUI().contains(p)) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(XShopDynamicShopCore.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    String title = p.getOpenInventory().getTitle().replace("&", "§");
                    boolean isClose = true;

                    if(title.equalsIgnoreCase(config.customConfig.getString("gui.gui_title").replace("&", "§"))
                            || title.equalsIgnoreCase(block.customConfig.getString("gui.title").replace("&", "§"))
                            || title.equalsIgnoreCase(farming.customConfig.getString("gui.title").replace("&", "§"))
                            || title.equalsIgnoreCase(minerals.customConfig.getString("gui.title").replace("&", "§"))
                            || title.equalsIgnoreCase(miscellaneous.customConfig.getString("gui.title").replace("&", "§"))
                            || title.equalsIgnoreCase(mobs.customConfig.getString("gui.title").replace("&", "§"))) {
                        isClose = false;
                    }

                    if(isClose) {
                        final Iterator<Player> pls = XShopDynamicShopCore.getPlayerOpenGUI().iterator();
                        while (pls.hasNext()) {
                            final Player pd = pls.next();

                            if(pd == null) {
                                pls.remove();
                            }

                            if (pd.getName().equalsIgnoreCase(e.getPlayer().getName())) {
                                pls.remove();
                            }
                        }
                    }
                }
            }, 2L);
        }

    }

}

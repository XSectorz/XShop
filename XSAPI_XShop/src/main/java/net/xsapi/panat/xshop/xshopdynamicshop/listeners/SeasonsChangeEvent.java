package net.xsapi.panat.xshop.xshopdynamicshop.listeners;

import net.xsapi.panat.xsseasons.core.SeasonsInterface;
import net.xsapi.panat.xsseasons.events.XSAPISeasonsChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SeasonsChangeEvent implements Listener {

    @EventHandler
    public void onSeasonChange(XSAPISeasonsChangeEvent e) {

        SeasonsInterface seasons = e.getSeasons();

        Bukkit.broadcastMessage("New Season is : " + seasons.getSeasonRealName());
        Bukkit.broadcastMessage("New Season name is : " + seasons.getSeason_str());


    }

}

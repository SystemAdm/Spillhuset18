package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnPlayerInteractAtEntityEvent implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        OddJob.getInstance().log("PlayerInteractAtEntityEvent");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        OddJob.getInstance().log("PlayerInteractEntityEvent");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        OddJob.getInstance().log("PlayerInteractEvent");
    }

}

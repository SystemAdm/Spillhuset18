package com.spillhuset.oddjob.Events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class OnEntityPickupItemEvent implements Listener {
    @EventHandler
    public void onPickup(EntityPickupItemEvent event){
        if (event.getEntity() instanceof Player player) {
            GameMode gameMode = player.getGameMode();
            if (gameMode.equals(GameMode.CREATIVE)) {
                event.setCancelled(true);
            }
        }
    }
}

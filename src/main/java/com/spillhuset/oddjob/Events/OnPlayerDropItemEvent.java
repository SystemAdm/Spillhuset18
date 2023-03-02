package com.spillhuset.oddjob.Events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class OnPlayerDropItemEvent implements Listener {
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        GameMode gameMode = player.getGameMode();

        if (gameMode.equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }
}

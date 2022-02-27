package com.spillhuset.oddjob.Events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class OnPlayerGameModeChangeEvent implements Listener {
    @EventHandler
    public void gameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (player.isOp() || player.hasPermission("admin")) {
            if (event.getNewGameMode() == GameMode.SURVIVAL) {
                player.setGameMode(GameMode.CREATIVE);
            }
        }
    }
}

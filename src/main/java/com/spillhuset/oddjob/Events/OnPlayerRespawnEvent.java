package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class OnPlayerRespawnEvent implements Listener {
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("gmi.sod")) {
            OddJob.getInstance().getScheduler().scheduleSyncDelayedTask(OddJob.getInstance(), () -> {
                OddJob.getInstance().getInventoryHandler().restoreOnSpawn(player);
            });
        }
    }
}

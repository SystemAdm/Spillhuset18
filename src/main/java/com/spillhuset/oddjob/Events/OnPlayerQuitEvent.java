package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;

public class OnPlayerQuitEvent implements Listener {
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        OddJob.getInstance().getTeleportManager().quit(player.getUniqueId());

        OddJob.getInstance().getPlayerManager().getScoreboard(player.getUniqueId());
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

    }
}

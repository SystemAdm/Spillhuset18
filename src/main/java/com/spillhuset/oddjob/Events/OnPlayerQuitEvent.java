package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuitEvent implements Listener {
    @EventHandler
    public void onLeaveEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("player.hidden")) {
            event.setQuitMessage("");
        } else {
            event.setQuitMessage(Plugin.leave.getString() + event.getPlayer().getDisplayName());
        }

        // Removes bossbar
        OddJob.getInstance().bossbar.removePlayer(player);

        // Removes locking tools
        OddJob.getInstance().getLocksManager().clear(player);
    }
}

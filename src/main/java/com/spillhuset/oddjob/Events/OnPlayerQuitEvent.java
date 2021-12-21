package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuitEvent implements Listener {
    @EventHandler
    public void onLeaveEvent(PlayerQuitEvent event) {
        if (event.getPlayer().hasPermission("player.hidden")) {
            event.setQuitMessage("");
        } else {
            event.setQuitMessage(Plugin.leave.getString() + event.getPlayer().getDisplayName());
        }
    }
}

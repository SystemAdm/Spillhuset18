package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnPlayerJoinEvent implements Listener {
    //TODO notify stacks
    // - auction

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        OddJob.getInstance().getPlayerManager().join(player.getUniqueId(), player.getName().toLowerCase());
        if (event.getPlayer().hasPermission("player.hidden")) {
            event.setJoinMessage("");
        } else {
            event.setJoinMessage(Plugin.join.getString() + event.getPlayer().getDisplayName());
        }

        // Clearing locks
        OddJob.getInstance().getLocksManager().clear(player);

        // Notifications from guild
        OddJob.getInstance().getGuildsManager().notify(player);

        // Econ
        OddJob.getInstance().getCurrencyManager().initiatePlayer(player.getUniqueId());
    }
}

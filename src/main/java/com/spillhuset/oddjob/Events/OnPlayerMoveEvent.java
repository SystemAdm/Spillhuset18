package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.Tool;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class OnPlayerMoveEvent implements Listener {

    private HashMap<UUID, UUID> inside = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {

        if (event.getTo() == null) {
            return;
        }
        if (event.getFrom().getChunk().getX() == event.getTo().getChunk().getX() && event.getFrom().getChunk().getZ() == event.getTo().getChunk().getZ()) {
            return;
        }


        Guild guildChunk = OddJob.getInstance().getGuildsManager().getGuildByCords(event.getTo().getChunk().getX(), event.getTo().getChunk().getZ(), event.getTo().getWorld());
        if (guildChunk == null) {
            return;
        }

        Player player = event.getPlayer();

        if (OddJob.getInstance().getPlayerManager().getInside(player.getUniqueId()) != null) {
            if (OddJob.getInstance().getPlayerManager().getInside(player.getUniqueId()).equals(guildChunk.getUuid())) {
                Tool.announce(player, guildChunk);
            }
        } else {
            OddJob.getInstance().getPlayerManager().setInside(player.getUniqueId(), guildChunk.getUuid());
            Tool.announce(player, guildChunk);
        }

        Guild guildPlayer = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
        if (guildPlayer == null) {
            return;
        }

        OddJob.getInstance().log("inside " + guildChunk.getName());
    }

}

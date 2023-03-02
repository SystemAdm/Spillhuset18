package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.Portal;
import com.spillhuset.oddjob.Utils.Tool;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class OnPlayerMoveEvent implements Listener {

    private final HashMap<UUID, UUID> inside = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        // Moving to undefined
        if (event.getTo() == null) {
            return;
        }

        Portal portal = OddJob.getInstance().getWarpsManager().getPortal(event.getTo());
        if (portal != null) {
            OddJob.getInstance().getWarpsManager().teleport(event.getPlayer(),portal.getWarp());
        }

        // Moving withing same chunk
        if (event.getFrom().getChunk().getX() == event.getTo().getChunk().getX() && event.getFrom().getChunk().getZ() == event.getTo().getChunk().getZ()) {
            return;
        }

        Player player = event.getPlayer();

        // Is chunk owned by a guild?
        Guild guildChunk = OddJob.getInstance().getGuildsManager().getGuildByCords(event.getTo().getChunk().getX(), event.getTo().getChunk().getZ(), event.getTo().getWorld());
        if (guildChunk == null) {
            Tool.announce(player, OddJob.getInstance().getGuildsManager().getGuildByZone(Zone.WILD));
            return;
        }

        // Is the chunk owned by the same guild as previous chunk?
        if (OddJob.getInstance().getPlayerManager().getInside(player.getUniqueId()) != null) {
            if (OddJob.getInstance().getPlayerManager().getInside(player.getUniqueId()).equals(guildChunk.getUuid())) {
                Tool.announce(player, guildChunk);
            }
        } else {
            OddJob.getInstance().getPlayerManager().setInside(player.getUniqueId(), guildChunk.getUuid());
            Tool.announce(player, guildChunk);
        }

        //OddJob.getInstance().log("inside " + guildChunk.getName());
    }

}

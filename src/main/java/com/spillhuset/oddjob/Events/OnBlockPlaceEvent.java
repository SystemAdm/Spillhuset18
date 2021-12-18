package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class OnBlockPlaceEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();
        Guild chunkGuild = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
        UUID playerGuild = OddJob.getInstance().getGuildsManager().getMembers().get(player.getUniqueId());

        // Zone is WILD
        if (chunkGuild == null || chunkGuild.getZone() == Zone.WILD) {
            return;
        }

        // Your own guild
        if (chunkGuild.getUuid() == playerGuild) {
            return;
        }

        if (player.isOp() || player.hasPermission("guilds.override.place")) {
            MessageManager.guilds_warn_block_placed(player,chunkGuild);
            return;
        }

        //TODO check for `trust` guilds
        event.setCancelled(true);
        MessageManager.guilds_not_allowed(player,chunkGuild);
    }
}

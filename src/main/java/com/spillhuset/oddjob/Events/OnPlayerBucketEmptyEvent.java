package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class OnPlayerBucketEmptyEvent implements Listener {
    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        Guild chunkGuild = OddJob.getInstance().getGuildsManager().getGuildByCords(chunk.getX(), chunk.getZ(), chunk.getWorld());
        Guild playerGuild = OddJob.getInstance().getGuildsManager().getGuildByMember(event.getPlayer().getUniqueId());
        if (chunkGuild == null) return;
        if (playerGuild != null && chunkGuild.getUuid().equals(playerGuild.getUuid())) return;
        event.setCancelled(true);
    }
}

package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class OnBlockFromToEvent implements Listener {

    @EventHandler
    public void onWaterFlow(BlockFromToEvent event) {
        Chunk chunk = event.getToBlock().getChunk();
        Guild chunkGuild = OddJob.getInstance().getGuildsManager().getGuildByCords(chunk.getX(), chunk.getZ(), chunk.getWorld());
        if (chunkGuild == null) return;
        event.setCancelled(true);
    }
}

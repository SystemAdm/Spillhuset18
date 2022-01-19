package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class OnBlockFromToEvent implements Listener {
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.getBlock().isLiquid()) {
            Block to = event.getToBlock();
            Block from = event.getBlock();

            Guild guildFrom = OddJob.getInstance().getGuildsManager().getGuildByChunk(from.getChunk());
            Guild guildTo = OddJob.getInstance().getGuildsManager().getGuildByChunk(to.getChunk());

            if (guildFrom != guildTo)
                event.setCancelled(true);
        }
    }
}

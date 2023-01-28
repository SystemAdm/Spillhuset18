package com.spillhuset.oddjob.Events;

import com.destroystokyo.paper.event.entity.CreeperIgniteEvent;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import java.util.ArrayList;
import java.util.List;

public class OnEntityExplodeEvent implements Listener {
    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        List<Block> replace = new ArrayList<>();
        int i = 0;
        for(Block block : event.blockList()) {
            Chunk chunk = block.getChunk();
            Guild chunkGuild = OddJob.getInstance().getGuildsManager().getGuildByCords(chunk.getX(), chunk.getZ(), chunk.getWorld());
            if (chunkGuild != null) {
                replace.add(block);
            }
        }
        event.blockList().removeAll(replace);
        Chunk chunk = event.getLocation().getChunk();
        Guild chunkGuild = OddJob.getInstance().getGuildsManager().getGuildByCords(chunk.getX(), chunk.getZ(), chunk.getWorld());
        if (chunkGuild == null) return;
        event.setCancelled(true);
        event.getEntity().remove();
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event) {
        Chunk chunk = event.getBlock().getChunk();
        Guild chunkGuild = OddJob.getInstance().getGuildsManager().getGuildByCords(chunk.getX(), chunk.getZ(), chunk.getWorld());
        if (chunkGuild == null) return;
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR,true);
    }
    @EventHandler
    public void onPrime(ExplosionPrimeEvent event) {
        Chunk chunk = event.getEntity().getLocation().getChunk();
        Guild chunkGuild = OddJob.getInstance().getGuildsManager().getGuildByCords(chunk.getX(), chunk.getZ(), chunk.getWorld());
        if (chunkGuild == null) return;
        event.setCancelled(true);
        event.getEntity().remove();
    }

    @EventHandler
    public void onIgnite(CreeperIgniteEvent event) {
        Chunk chunk = event.getEntity().getLocation().getChunk();
        Guild chunkGuild = OddJob.getInstance().getGuildsManager().getGuildByCords(chunk.getX(), chunk.getZ(), chunk.getWorld());
        if (chunkGuild == null) return;
        event.setCancelled(true);
        event.getEntity().remove();
    }
}

package com.spillhuset.oddjob.Events;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class OnEntityExplodeEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplode(TNTPrimeEvent event) {
        event.getPrimerEntity();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplode(BlockExplodeEvent event) {
        HashMap<Location, BlockData> keep = new HashMap<>();
        for (Block block : event.blockList()) {
            Chunk chunk = block.getChunk();
            Guild blockGuild = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
            if (blockGuild != null && blockGuild.getZone() != Zone.WILD) {
                event.setCancelled(true);
                keep.put(block.getLocation(),block.getBlockData());
            }

        }

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Location location : keep.keySet()) {
                    location.getBlock().setBlockData(keep.get(location));
                }
            }
        };
        runnable.runTaskLater(OddJob.getInstance(),20L);
    }
}

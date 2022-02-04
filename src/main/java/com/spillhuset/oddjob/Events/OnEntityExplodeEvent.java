package com.spillhuset.oddjob.Events;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class OnEntityExplodeEvent implements Listener {
    /**
     * Cancel Ignition
     */
    @EventHandler
    public void blockIgnite(BlockIgniteEvent event) {
        // CHECK GUILD
        Guild guild = OddJob.getInstance().getGuildsManager().getGuildByChunk(event.getBlock().getLocation().getChunk());
        if (guild != null && guild.getZone() != Zone.WILD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPrime(TNTPrimeEvent event) {
        // CHECK GUILD
        Guild guild = OddJob.getInstance().getGuildsManager().getGuildByChunk(event.getBlock().getLocation().getChunk());
        if (guild != null && guild.getZone() != Zone.WILD) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplode(EntityExplodeEvent event) {
        HashMap<Location, BlockData> keep = new HashMap<>();
        if (event.blockList().size() > 0) {
            for (Block block : event.blockList()) {
                Chunk chunk = block.getChunk();
                Guild blockGuild = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
                if (blockGuild != null && !blockGuild.getZone().equals(Zone.WILD)) {
                    keep.put(block.getLocation(), block.getBlockData());
                    block.getDrops().clear();
                }
            }
        }

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Location location : keep.keySet()) {
                    location.getBlock().setBlockData(keep.get(location), true);
                }
            }
        };
        runnable.runTaskLater(OddJob.getInstance(), 10L);
    }
}

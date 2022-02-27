package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.StructureType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class StrongholdEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStrongholdBreak(BlockBreakEvent event) {
        OddJob.getInstance().log("block_break");
        Player player = event.getPlayer();
        Location entrance = player.getWorld().locateNearestStructure(player.getLocation(), StructureType.STRONGHOLD, 100, false);
        if (entrance == null) return;

        World world = player.getWorld();
        Chunk chunk = entrance.getChunk();
        int maxX = chunk.getX() + 10;
        int minX = chunk.getX() - 10;
        int maxZ = chunk.getZ() + 10;
        int minZ = chunk.getZ() - 10;

        Chunk pChunk = player.getLocation().getChunk();
        if ((pChunk.getX() < maxX && pChunk.getX() > minX) && (pChunk.getZ() < maxZ && pChunk.getZ() > minZ)) {
            MessageManager.fortress(player);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStrongholdPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Location entrance = player.getWorld().locateNearestStructure(player.getLocation(), StructureType.STRONGHOLD, 100, false);
        if (entrance == null) return;

        World world = player.getWorld();
        Chunk chunk = entrance.getChunk();
        int maxX = chunk.getX() + 10;
        int minX = chunk.getX() - 10;
        int maxZ = chunk.getZ() + 10;
        int minZ = chunk.getZ() - 10;

        Chunk pChunk = player.getLocation().getChunk();
        if ((pChunk.getX() < maxX && pChunk.getX() > minX) && (pChunk.getZ() < maxZ && pChunk.getZ() > minZ)) {
            MessageManager.fortress(player);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStrongholdBreak(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Location entrance = player.getWorld().locateNearestStructure(player.getLocation(), StructureType.STRONGHOLD, 100, false);
        if (entrance == null) return;

        Chunk chunk = entrance.getChunk();
        int maxX = chunk.getX() + 10;
        int minX = chunk.getX() - 10;
        int maxZ = chunk.getZ() + 10;
        int minZ = chunk.getZ() - 10;

        Chunk pChunk = null;
        if (block != null) {
            pChunk = block.getChunk();

            if ((pChunk.getX() < maxX && pChunk.getX() > minX) && (pChunk.getZ() < maxZ && pChunk.getZ() > minZ)) {
                MessageManager.fortress(player);
                event.setCancelled(true);
            }
        }
    }


}

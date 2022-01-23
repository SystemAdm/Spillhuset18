package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

public class OnBlockBreakEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onStronghold(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location entrance = player.getWorld().locateNearestStructure(player.getLocation(), StructureType.STRONGHOLD, 100, false);
        if (entrance == null) return;

        World world = player.getWorld();
        Chunk chunk = entrance.getChunk();
        int maxX = chunk.getX() + 5;
        int minX = chunk.getX() - 5;
        int maxZ = chunk.getZ() + 5;
        int minZ = chunk.getZ() - 5;

        Chunk pChunk = player.getLocation().getChunk();
        if ((pChunk.getX() < maxX && pChunk.getX() > minX) && (pChunk.getZ() < maxZ && pChunk.getZ() > minZ)) {
            MessageManager.fortress(player);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        Guild chunkGuild = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
        UUID playerGuild = OddJob.getInstance().getGuildsManager().getMembers().get(player.getUniqueId());

        // <-- Private -->
        UUID ownerOfBlock = OddJob.getInstance().getLocksManager().isLocked(block);
        if (ownerOfBlock != null && ownerOfBlock.equals(player.getUniqueId())) {
            OddJob.getInstance().getLocksManager().breakLock(block);
            MessageManager.locks_broken(player);
        }

        // <-- Guild -->

        // Zone is WILD
        if (chunkGuild == null || chunkGuild.getZone() == Zone.WILD) {
            return;
        }

        // Your own guild
        if (chunkGuild.getUuid() == playerGuild) {
            return;
        }

        if (player.isOp() || player.hasPermission("guilds.override.break")) {
            MessageManager.guilds_warn_block_broken(player, chunkGuild);
            return;
        }

        //TODO check for `trust` guilds
        event.setCancelled(true);
        MessageManager.guilds_not_allowed(player, chunkGuild);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreakForMoney(BlockBreakEvent event) {
        if (!event.isCancelled()) {
            Player player = event.getPlayer();
            // Econ
            if (!player.isOp() && player.getGameMode() == GameMode.SURVIVAL) {
                OddJob.getInstance().getCurrencyManager().earnBlockBrake(player, event.getBlock());
            }
        }
    }
}

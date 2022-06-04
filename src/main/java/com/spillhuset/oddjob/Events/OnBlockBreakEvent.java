package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.Managers.WarpManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class OnBlockBreakEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        OddJob.getInstance().log("breake");

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        Guild chunkGuild = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
        UUID playerGuild = OddJob.getInstance().getGuildsManager().getMembers().get(player.getUniqueId());

        // <-- Private -->
        UUID ownerOfBlock = OddJob.getInstance().getLocksManager().isLocked(block);
        if (ownerOfBlock != null && ownerOfBlock.equals(player.getUniqueId())) {
            OddJob.getInstance().getLocksManager().breakLock(player, block);
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

        ItemStack item = event.getPlayer().getItemInUse();
        if (item != null && item.equals(WarpManager.tool())) {
            OddJob.getInstance().log("tool in hand");
            // A tool in hand
            event.setCancelled(true);
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

package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.LockUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

public class OnBlockBreakEvent implements Listener {
    @EventHandler (priority = EventPriority.NORMAL)
    public void breakGuild(BlockBreakEvent event) {
        Guild guildPlayer = OddJob.getInstance().getGuildsManager().getGuildByMember(event.getPlayer().getUniqueId());
        Chunk chunk = event.getBlock().getChunk();
        Guild guildChunk = OddJob.getInstance().getGuildsManager().getGuildByCords(chunk.getX(), chunk.getZ(), event.getPlayer().getWorld());
        // Chunk has no owning guild
        if (guildChunk == null) {
            return;
        }

        // Chunk is owned by the same guild as the player is in
        if (guildPlayer != null && guildChunk.getUuid().equals(guildPlayer.getUuid())) {
            return;
        }

        // Has permission
        if (event.getPlayer().hasPermission("guilds.override") || event.getPlayer().isOp()) {
            return;
        }

        event.setCancelled(true);
        MessageManager.guilds_owned(event.getPlayer(), guildChunk.getName());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void breakLock(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        Player player = event.getPlayer();
        OddJob.getInstance().log("B");

        // Is block lockable
        if (!OddJob.getInstance().getLocksManager().isLockable(block.getType())) {
            return;
        }

        // Is block a chest
        if (block.getType() == Material.CHEST) {
            location = LockUtil.getChestLeft(location);
        }

        // Is block a door
        if (block.getBlockData() instanceof Door) {
            location = LockUtil.getLowerLeftDoor(location);
        }

        // Is block owned
        UUID owner = OddJob.getInstance().getLocksManager().isLocked(location);
        if (owner != null) {
            if (!owner.equals(player.getUniqueId())) {
                // Cancel it!
                OddJob.getInstance().log("B-N");
                event.setCancelled(true);
                MessageManager.locks_locked(player, block.getType().name());
                return;
            }

            // Break the lock
            OddJob.getInstance().getLocksManager().breakLock(player, location);
        }
    }
    @EventHandler (priority = EventPriority.MONITOR)
    public void breakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("pay_override") || player.isOp()) return;
        OddJob.getInstance().getCurrencyManager().earnings(player.getUniqueId());
    }
}

package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.LockUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class OnBlockPlaceEvent implements Listener {
    @EventHandler
    public void onGuildPlace(BlockPlaceEvent event) {
        Guild guildPlayer = OddJob.getInstance().getGuildsManager().getGuildByMember(event.getPlayer().getUniqueId());
        Chunk chunk = event.getBlockPlaced().getChunk();
        Guild guildChunk = OddJob.getInstance().getGuildsManager().getGuildByCords(chunk.getX(), chunk.getZ(), event.getPlayer().getWorld());
        OddJob.getInstance().log("C");
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
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        block = block.getRelative(BlockFace.UP);
        Location location = block.getLocation();
        if (block.getType() == Material.CHEST) {
            location = LockUtil.getChestLeft(location);
        }
        UUID owner = OddJob.getInstance().getLocksManager().isLocked(location);
        if (owner == null) {
            return;
        }
        if (owner.equals(event.getPlayer().getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        OddJob.getInstance().log("locked");

    }
}

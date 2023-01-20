package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.LockUtil;
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

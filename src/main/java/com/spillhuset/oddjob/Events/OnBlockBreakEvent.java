package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.LocksSQL;
import com.spillhuset.oddjob.Utils.LockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

public class OnBlockBreakEvent implements Listener {
    @EventHandler
    public void breakLock(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        Player player = event.getPlayer();

        if (!OddJob.getInstance().getLocksManager().isLockable(block.getType())) {
            return;
        }

        if (block.getType() == Material.CHEST) {
            location = LockUtil.getChestLeft(location);
        }
        if (block.getBlockData() instanceof Door) {
            location = LockUtil.getLowerLeftDoor(location);
        }

        UUID owner = OddJob.getInstance().getLocksManager().isLocked(location);
        if (owner != null){
            if (!owner.equals(player.getUniqueId())) {
                event.setCancelled(true);
                MessageManager.locks_locked(player, block.getType().name());
                return;
            }
            OddJob.getInstance().getLocksManager().breakLock(player, location);
        }
    }
}

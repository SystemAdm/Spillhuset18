package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class OnPlayerInteractEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLockTool(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        /* Locking */
        if (!OddJob.getInstance().getLocksManager().lockTool.equals(event.getItem())) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!OddJob.getInstance().getLocksManager().isLockable(block.getType())) {
            return;
        }
        event.setCancelled(true);

        OddJob.getInstance().getLocksManager().lock(event.getPlayer(), block);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUnlockTool(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        /* Unlocking */
        if (!OddJob.getInstance().getLocksManager().unlockTool.equals(event.getItem())) {
            return;
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!OddJob.getInstance().getLocksManager().isLockable(block.getType())) {
            return;
        }
        event.setCancelled(true);

        OddJob.getInstance().getLocksManager().unlock(event.getPlayer(), block);
    }

    @EventHandler
    public void useItem(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        if (!OddJob.getInstance().getLocksManager().isLockable(block.getType())) {
            return;
        }
        Location location = event.getClickedBlock().getLocation();
        Player player = event.getPlayer();
        UUID owner = OddJob.getInstance().getLocksManager().isLocked(location);
        if (owner != null && !owner.equals(player.getUniqueId())) {
            MessageManager.locks_locked(player,block.getType().name());
            event.setCancelled(true);
        }
    }
}
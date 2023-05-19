package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.LockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class OnPlayerInteractEvent implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        OddJob.getInstance().log("event");

        // In trading inventory
        if (event.getView().getTitle().startsWith("Trading ")) {
            OddJob.getInstance().log("trading");
            Player player = (Player) event.getWhoClicked();
            Inventory inventory = event.getInventory();
            ItemStack itemStack = event.getWhoClicked().getItemOnCursor();

            OddJob.getInstance().getShopsManager().tradeAction(player, inventory, itemStack,event.getView());

            event.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLockTool(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        // There must be a block
        if (block == null) {
            return;
        }

        // Use of locking event
        if (!OddJob.getInstance().getLocksManager().lockTool.equals(event.getItem())) {
            return;
        }

        event.setCancelled(true);

        // Clicked the correct way?
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        // Must be lockable
        if (!OddJob.getInstance().getLocksManager().isLockable(block.getType())) {
            return;
        }

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
        event.setCancelled(true);
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!OddJob.getInstance().getLocksManager().isLockable(block.getType())) {
            return;
        }

        OddJob.getInstance().getLocksManager().unlock(event.getPlayer(), block);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAddTool(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        /* Adding */
        if (!OddJob.getInstance().getLocksManager().addTool.equals(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (OddJob.getInstance().getLocksManager().isLockable(block.getType())) {
            return;
        }

        OddJob.getInstance().getLocksManager().add(event.getPlayer(), block);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDelTool(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        /* Deleting */
        if (!OddJob.getInstance().getLocksManager().delTool.equals(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!OddJob.getInstance().getLocksManager().isLockable(block.getType())) {
            return;
        }

        OddJob.getInstance().getLocksManager().del(event.getPlayer(), block);
    }

    @EventHandler
    public void useItem(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();

        // Is it a block
        if (block == null) {
            return;
        }

        BlockData blockState = block.getBlockData();

        // Is it lockable
        if (!OddJob.getInstance().getLocksManager().isLockable(block.getType())) {
            return;
        }

        Location location = event.getClickedBlock().getLocation();
        if (blockState instanceof Door) {
            location = LockUtil.getLowerLeftDoor(location);
        } else if (block.getType() == Material.CHEST) {
            location = LockUtil.getChestLeft(location);
        }
        Player player = event.getPlayer();

        // Is it already locked
        UUID owner = OddJob.getInstance().getLocksManager().isLocked(location);
        if (player.hasPermission("locks.override")) {
            // Toggle the door
            if (blockState instanceof Door) {
                LockUtil.toggleDoor(block, player, location);
            }
            return;
        }

        // Are you the owner
        if (owner != null && !owner.equals(player.getUniqueId())) {
            // Cancel it!
            MessageManager.locks_locked(player, block.getType().name());
            event.setCancelled(true);
            return;
        }

        // Toggle the door!
        if (blockState instanceof Door) {
            LockUtil.toggleDoor(block, player, location);
        }
    }
}
package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.LockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class OnInventoryMoveEvent implements Listener {
    @EventHandler
    public void InventoryMover(InventoryMoveItemEvent event) {
        if (event.getItem().getType() == Material.AIR) {
            return;
        }
        Inventory source = event.getSource();
        Location happening = source.getLocation();
        if (happening == null) return;
        if (!OddJob.getInstance().getLocksManager().isLockable(happening.getBlock().getType())) {
            OddJob.getInstance().log("returned");
            return;
        }
        if (source.getType() == InventoryType.CHEST) {
            happening = LockUtil.getChestLeft(happening);
            OddJob.getInstance().log("chest");
        }
        if (OddJob.getInstance().getLocksManager().isLocked(happening) != null) {
            event.setCancelled(true);
            OddJob.getInstance().log("locked");
        }
    }
}
package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.LockUtil;
import com.spillhuset.oddjob.Utils.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

public class OnInventoryMoveEvent implements Listener {
    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        Inventory source = event.getSource();
        Location sourceLocation = null;
        if (source instanceof DoubleChestInventory && source.getLocation() != null) {
            sourceLocation = LockUtil.getChestLeft(source.getLocation());
        } else {
            sourceLocation = source.getLocation();
        }

        if (sourceLocation != null) {
            if (OddJob.getInstance().getLocksManager().isLocked(sourceLocation.getBlock()) != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void signChange(SignChangeEvent event) {
        String[] lines = event.getLines();
        OddJob.getInstance().log("here");
        Warp warp = null;
        if (lines.length != 0 && lines[0].equalsIgnoreCase("[warp]")) {
            if (!lines[1].isEmpty()) {
                for (String test : OddJob.getInstance().getWarpManager().getList()) {
                    if (test.equalsIgnoreCase(lines[1])) {
                        warp = OddJob.getInstance().getWarpManager().get(test);
                    }
                }
                if (warp != null) {
                    event.setLine(0, "");
                    event.setLine(1, ChatColor.GREEN + "[warp]");
                    event.setLine(2, "to: " + warp.getName() + "");
                    if (warp.hasCost()) {
                        event.setLine(3,"cost: "+ warp.getCost()+(warp.isProtected()?ChatColor.RED+"*":""));
                    }
                }
            }
        }
    }
}

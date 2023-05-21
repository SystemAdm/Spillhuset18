package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.LockUtil;
import com.spillhuset.oddjob.Utils.Menu;
import com.spillhuset.oddjob.Utils.TradeMenu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class OnInventoryMoveEvent implements Listener {
    @EventHandler
    public void inventoryMove(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryHolder holder = event.getInventory().getHolder();
        TradeMenu active = null;
        if (holder instanceof Menu) {
            for (TradeMenu trade : OddJob.getInstance().getShopsManager().tradeActive) {
                if (trade.getTarget().equals(player.getUniqueId()) || trade.getTrader().equals(player.getUniqueId())) {
                    active = trade;
                }
            }
        }
        if (active == null) return;
        if (event.getRawSlot() >= 0 && event.getRawSlot() < 9) {
            event.setCancelled(true);
            if (active.getTrader().equals(player.getUniqueId())) event.setCancelled(false);
        } else if (event.getRawSlot() >= 18 && event.getRawSlot() < 27) {
            event.setCancelled(true);
            if (active.getTarget().equals(player.getUniqueId())) event.setCancelled(false);
        } else if (event.getRawSlot() >= 9 && event.getRawSlot() < 18) {
            active.onClick(player, event.getRawSlot(), event.getClick());
            event.setCancelled(true);
        } else {
            event.setCancelled(false);
        }

    }

    @EventHandler
    public void InventoryMover(InventoryMoveItemEvent event) {
        if (event.getItem().getType() == Material.AIR) {
            return;
        }
        Inventory source = event.getSource();
        Location happening = source.getLocation();
        if (happening == null) return;
        if (!OddJob.getInstance().getLocksManager().isLockable(happening.getBlock().getType())) {
            //OddJob.getInstance().log("returned");
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
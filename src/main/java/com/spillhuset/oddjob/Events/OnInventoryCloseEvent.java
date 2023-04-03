package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.PriceList;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OnInventoryCloseEvent implements Listener {
    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        HumanEntity clicker = event.getWhoClicked();
        Inventory inventory = event.getInventory();
        if (event.getView().getTitle().equalsIgnoreCase("COMMON SHOP")) {
            event.setResult(Event.Result.DENY);
            ItemStack itemStack = clicker.getItemOnCursor();
            event.
            PriceList.valueOf()
        }

        OddJob.getInstance().log("inventory interact event");
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        OddJob.getInstance().log("inventory move item event");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        OddJob.getInstance().log("inventory click event");
    }

    @EventHandler
    public void onInventoryPickup(InventoryPickupItemEvent event) {
        OddJob.getInstance().log("inventory pickup item event");
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        OddJob.getInstance().log("inventory drag event");
    }

    @EventHandler
    public void onInventory(InventoryEvent event) {
        OddJob.getInstance().log("inventory event");
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();

        if (inventory.getType().equals(InventoryType.WORKBENCH) && player.getGameMode().equals(GameMode.CREATIVE)) {
            boolean empty = true;
            for (ItemStack itemStack : inventory.getContents()) {
                if (!itemStack.getType().isAir()) {
                    empty = false;
                }
            }
            if (!empty) {
                inventory.clear();
            }
        }
        OddJob.getInstance().getPlayerManager().removeInventory(inventory, player);
    }
}

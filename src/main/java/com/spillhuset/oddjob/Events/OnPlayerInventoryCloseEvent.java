package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OnPlayerInventoryCloseEvent implements Listener {
    @EventHandler
    public void onPlayerInventoryClose(InventoryCloseEvent event) {
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
        OddJob.getInstance().getPlayerManager().removeInventory(inventory,player);
    }
}

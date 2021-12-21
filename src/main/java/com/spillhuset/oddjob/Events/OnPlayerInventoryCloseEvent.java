package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class OnPlayerInventoryCloseEvent implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        Entity stand = OddJob.getInstance().getPlayerManager().openSpirit.get(player.getUniqueId());
        if (stand != null)  {
            OddJob.getInstance().getPlayerManager().replaceSpirit(stand, player.getUniqueId(), true);
        }
        OddJob.getInstance().getPlayerManager().openSpirit.remove(player.getUniqueId());
    }
}

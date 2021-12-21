package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;

public class OnPlayerInteractAtEntityEvent implements Listener {
    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player clicker = event.getPlayer();
        OddJob.getInstance().log("Righty tidy");
        Inventory inventory = OddJob.getInstance().getPlayerManager().getSpiritInventory(entity.getUniqueId());
        if (inventory != null) {
            OddJob.getInstance().log("Click Clack");
            clicker.openInventory(inventory);
            OddJob.getInstance().getPlayerManager().openSpirit.put(clicker.getUniqueId(), entity);
        }
    }
}

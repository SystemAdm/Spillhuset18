package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class OnPlayerDropItemEvent implements Listener {
    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (OddJob.getInstance().getLocksManager().getTools().contains(item)) {
            event.setCancelled(true);
        }
    }
}

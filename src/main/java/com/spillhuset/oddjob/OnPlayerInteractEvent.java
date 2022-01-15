package com.spillhuset.oddjob;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class OnPlayerInteractEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEvent(PlayerInteractEvent event) {
        Action action = event.getAction();
        Block clickedBlock = event.getClickedBlock();
        EquipmentSlot hand = event.getHand();
        ItemStack itemStack = event.getItem();
    }
}

package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnPlayerInteractAtEntityEvent implements Listener {
    // Right click
    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        OddJob.getInstance().log("PlayerInteractAtEntityEvent");
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (entity.getType().equals(EntityType.ARMOR_STAND)) {
            if (entity.getCustomName() != null && ChatColor.stripColor(entity.getCustomName()).startsWith("Spirit of ")) {
                OddJob.getInstance().getPlayerManager().openArmorstand(entity.getUniqueId(), player);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        OddJob.getInstance().log("PlayerInteractEntityEvent");
        if (!event.getPlayer().isOp()) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.ANVIL)) {
            if (event.getRightClicked() instanceof Villager villager) {
                villager.setPersistent(true);
                villager.setCustomName("BLACKSMITH");
                villager.setCustomNameVisible(true);
            }
        }

    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        OddJob.getInstance().log("PlayerInteractEvent");
        // Click
    }

}

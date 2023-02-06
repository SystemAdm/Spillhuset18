package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
                OddJob.getInstance().getPlayerManager().openArmorstand(entity.getUniqueId(),player);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        OddJob.getInstance().log("PlayerInteractEntityEvent");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        OddJob.getInstance().log("PlayerInteractEvent");
        // Click
    }

}

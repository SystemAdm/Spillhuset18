package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.checkerframework.checker.units.qual.A;

import java.util.UUID;

public class OnPlayerInteractAtEntityEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
        if (event.isCancelled()) return;

        /* Entity clicked */
        Entity entity = event.getRightClicked();

        /* Clicking player*/
        Player clicker = event.getPlayer();

        /* Get entities inventory */
        Inventory inventory = OddJob.getInstance().getPlayerManager().getSpiritInventory(entity.getUniqueId());
        if (inventory != null) {
            // Cancel default
            event.setCancelled(true);
            // Open inventory
            clicker.openInventory(inventory);
            OddJob.getInstance().getPlayerManager().openSpirit.put(clicker.getUniqueId(), entity);
        }

        Guild guildClicked = OddJob.getInstance().getGuildsManager().getGuildByChunk(entity.getLocation().getChunk());
        Guild guildPlayer = OddJob.getInstance().getGuildsManager().getGuildByMember(clicker.getUniqueId());
        if (guildClicked != null) {
            if (guildPlayer != null && guildPlayer == guildClicked){
                return;
            }
            event.setCancelled(true);
        }

        if (entity instanceof ArmorStand armorStand) {
            if (armorStand.getCustomName() != null && armorStand.getCustomName().startsWith("The spirit of ")) {
                Inventory spiritInv = OddJob.getInstance().getPlayerManager().getSpiritInventory(armorStand.getUniqueId());
                clicker.openInventory(spiritInv);
            }
        }
    }
}

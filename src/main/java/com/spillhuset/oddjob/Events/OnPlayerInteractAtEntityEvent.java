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
        OddJob.getInstance().log("b");
        if (event.isCancelled()) return;
        Entity entity = event.getRightClicked();
        Player clicker = event.getPlayer();
        OddJob.getInstance().log("a");

        Inventory inventory = OddJob.getInstance().getPlayerManager().getSpiritInventory(entity.getUniqueId());
        if (inventory != null) {
            OddJob.getInstance().log("Click Clack");
            event.setCancelled(true);
            clicker.openInventory(inventory);
            OddJob.getInstance().getPlayerManager().openSpirit.put(clicker.getUniqueId(), entity);
        }

        OddJob.getInstance().log("Righty tidy");

        Guild guildClicked = OddJob.getInstance().getGuildsManager().getGuildByChunk(entity.getLocation().getChunk());
        Guild guildPlayer = OddJob.getInstance().getGuildsManager().getGuildByMember(clicker.getUniqueId());
        if (guildClicked != null) {
            if (guildPlayer != null && guildPlayer == guildClicked){
                return;
            }
            event.setCancelled(true);
        }

        if (entity instanceof ArmorStand armorStand) {
            OddJob.getInstance().log("test");
            if (armorStand.getCustomName() != null && armorStand.getCustomName().startsWith("The spirit of ")) {
                Inventory spiritInv = OddJob.getInstance().getPlayerManager().getSpiritInventory(armorStand.getUniqueId());
                clicker.openInventory(spiritInv);
            }

        }
    }
}

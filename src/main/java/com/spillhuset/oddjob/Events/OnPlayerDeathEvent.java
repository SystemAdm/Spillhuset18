package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.*;

public class OnPlayerDeathEvent implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location location = player.getLocation();

        // GameMode Inventory Save On Death
        if(player.hasPermission("gmi.sod")) {
            OddJob.getInstance().getInventoryHandler().saveOnDeath(player);
            event.getDrops().clear();
            return;
        }

        World world = location.getWorld();
        if (world != null) {
            ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
            EntityEquipment equipmentPlayer = player.getEquipment();
            EntityEquipment equipmentArmorStand = armorStand.getEquipment();
            if (equipmentPlayer != null && equipmentArmorStand != null) {
                equipmentArmorStand.setArmorContents(equipmentPlayer.getArmorContents());
                Inventory inventory = Bukkit.createInventory(null,36,"Spirit of "+player.getName());
                for (int i = 0; i < 36; i ++) {
                    ItemStack is = player.getInventory().getItem(i);
                    if (is != null && is.getType() != Material.AIR) {
                        inventory.setItem(i,player.getInventory().getItem(i));
                    }
                }
                event.getDrops().clear();
                player.getInventory().clear();

                OddJob.getInstance().getPlayerManager().setSpirit(player,armorStand,inventory);
            }
        }
    }
}

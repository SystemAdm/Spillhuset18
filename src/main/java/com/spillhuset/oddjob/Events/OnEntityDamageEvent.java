package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Chunk;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class OnEntityDamageEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageByMonster(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();
        Entity damager = event.getDamager();
        if (damager instanceof Player && target instanceof ItemFrame itemFrame) {
            Guild guildClicked = OddJob.getInstance().getGuildsManager().getGuildByChunk(target.getLocation().getChunk());
            Guild guildPlayer = OddJob.getInstance().getGuildsManager().getGuildByMember(damager.getUniqueId());
            if (guildClicked != null) {
                if (guildPlayer != null && guildPlayer == guildClicked) {
                    return;
                }
                event.setCancelled(true);
            }
        }
        if (damager instanceof Player && target instanceof ArmorStand armorStand) {
            UUID stand = armorStand.getUniqueId();
            Inventory inventory = OddJob.getInstance().getPlayerManager().getSpiritInventory(stand);
            if (inventory != null) {
                OddJob.getInstance().getPlayerManager().replaceSpirit(armorStand, damager.getUniqueId(), false);
            }
        } else if (target instanceof Player && damager instanceof Monster) {
            Chunk chunk = target.getLocation().getChunk();
            Guild guild = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
            if (guild.getZone() == Zone.SAFE || guild.getZone() == Zone.GUILD) {
                event.setDamage(0);
                event.setCancelled(true);
                damager.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageByProjectile(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Entity target = event.getHitEntity();
        if (target instanceof Player) {
            Chunk chunk = target.getLocation().getChunk();
            Guild guild = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
            if (guild.getZone() == Zone.SAFE || guild.getZone() == Zone.GUILD) {
                event.setCancelled(true);
                /*projectile.getShooter();
                if (shooter != null) {
                    shooter.remove();
                }*/
            }
        }
    }
}

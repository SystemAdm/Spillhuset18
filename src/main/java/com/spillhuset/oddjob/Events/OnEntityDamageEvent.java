package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class OnEntityDamageEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageByMonster(EntityDamageByEntityEvent event) {
        Entity target = event.getEntity();
        Entity damager = event.getDamager();
        if (target instanceof Player && damager instanceof Monster) {
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
                Monster shooter = (Monster) projectile.getShooter();
                if (shooter != null) {
                    shooter.remove();
                }
            }
        }
    }
}

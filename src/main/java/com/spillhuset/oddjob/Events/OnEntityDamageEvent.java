package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class OnEntityDamageEvent implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entityTarget = event.getEntity();
        Entity entityDamage = event.getDamager();
        Guild guildTarget = null;
        Guild guildDamage = null;
        // Target is a player
        if (entityTarget.getType().equals(EntityType.PLAYER)) {
            guildTarget = OddJob.getInstance().getGuildsManager().getGuildByMember(entityTarget.getUniqueId());
            OddJob.getInstance().getPlayerManager().combat(entityTarget);
        }
        // Damager is a player
        if (entityDamage.getType().equals(EntityType.PLAYER)) {
            guildDamage = OddJob.getInstance().getGuildsManager().getGuildByMember(entityDamage.getUniqueId());
            OddJob.getInstance().getPlayerManager().combat(entityDamage);
        }
        // Target has no guild
        if (guildTarget == null) return;

        // From same guild
        if (guildDamage != null && guildDamage.getUuid().equals(guildTarget.getUuid())) {
            // Is set to friendly fire
            if (guildDamage.isFriendlyFire()) {
                return;
            }
            event.setCancelled(true);
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntityType().equals(EntityType.ARMOR_STAND) && event.getDamager().getType().equals(EntityType.PLAYER)) {
            Entity target = event.getEntity();
            Player player = (Player) event.getDamager();
            if (target.getCustomName() != null && target.getCustomName().startsWith(ChatColor.GREEN + "Spirit of ")) {
                UUID owner = OddJob.getInstance().getPlayerManager().removeArmorstand(target.getUniqueId());
                MessageManager.death_ooops(player, owner);
                return;
            }
        }

        if (event.getEntityType().equals(EntityType.PLAYER)) {
            OddJob.getInstance().getPlayerManager().combat(event.getEntity());
        }
        if (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            if (event.getDamager() instanceof Arrow arrow) {
                Chunk chunk = arrow.getLocation().getChunk();
                Guild chunkGuild = OddJob.getInstance().getGuildsManager().getGuildByCords(chunk.getX(), chunk.getZ(), chunk.getWorld());
                if (chunkGuild == null) return;
                event.setCancelled(true);
            }
        }
    }

}

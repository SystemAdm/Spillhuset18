package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class OnPlayerDeathEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location location = player.getLocation();
        Entity entity = location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        OddJob.getInstance().getPlayerManager().addSpirit(entity,player);
        event.getDrops().clear();
    }
}

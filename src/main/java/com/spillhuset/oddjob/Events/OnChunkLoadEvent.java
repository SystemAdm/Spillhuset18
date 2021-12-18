package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Managers.PlayerManager;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.UUID;

public class OnChunkLoadEvent implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity: event.getChunk().getWorld().getEntitiesByClass(ArmorStand.class)) {
            UUID uuid = entity.getUniqueId();
            if (entity.getCustomName() != null && entity.getCustomName().startsWith("The spirit of")) {
                if (!PlayerManager.spiritIgnore.contains(uuid)) {
                    if (OddJob.getInstance().getPlayerManager().getSpiritInventory(uuid) != null) {
                        PlayerManager.spiritIgnore.add(uuid);
                    } else {
                        OddJob.getInstance().getPlayerManager().removeSpirit(uuid);
                    }
                }
            }
        }
    }
}

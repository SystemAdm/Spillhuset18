package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class OnChunkLoadEvent implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        for (Entity entity : chunk.getEntities()) {
            if (entity.getType() == EntityType.ARMOR_STAND) {
                if (entity.getCustomName() != null && entity.getCustomName().startsWith(ChatColor.GREEN+"Spirit of ")) {
                    if (OddJob.getInstance().getPlayerManager().getSpirits().containsKey(entity.getUniqueId())) {
                        entity.remove();
                    }
                }
            }
        }
    }

}

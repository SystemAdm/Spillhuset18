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
        // Every entity
        for (Entity entity : chunk.getEntities()) {
            //OddJob.getInstance().log("entity");
            // If is armorstand
            if (entity.getType() == EntityType.ARMOR_STAND) {
               // OddJob.getInstance().log("armorstand");
                // If name starts with
                if (entity.getCustomName() != null && ChatColor.stripColor(entity.getCustomName()).toLowerCase().startsWith("spirit of")) {
                    //OddJob.getInstance().log("spirit");
                    // If is not active spirit
                    if (!OddJob.getInstance().getPlayerManager().getSpirits().containsKey(entity.getUniqueId())) {
                        //OddJob.getInstance().log("NoList");
                        entity.remove();
                    }
                }
            }
        }
    }

}

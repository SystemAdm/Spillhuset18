package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Chunk;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class OnEntitySpawnEvent implements Listener {
    @EventHandler
    public void spawnEvent(EntitySpawnEvent event) {
        Chunk chunk = event.getLocation().getChunk();
        Guild guild = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);

        if ((guild != null) && (event.getEntity() instanceof Monster))
            event.setCancelled(true);
    }
}

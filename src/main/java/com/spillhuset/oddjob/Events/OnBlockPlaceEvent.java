package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.Managers.WarpManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class OnBlockPlaceEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        OddJob.getInstance().log("place");
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();
        Guild chunkGuild = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
        UUID playerGuild = OddJob.getInstance().getGuildsManager().getMembers().get(player.getUniqueId());

        // Zone is WILD
        if (chunkGuild == null || chunkGuild.getZone() == Zone.WILD) {
            return;
        }

        // Your own guild
        if (chunkGuild.getUuid() == playerGuild) {
            return;
        }

        if (player.isOp() || player.hasPermission("guilds.override.place")) {
            MessageManager.guilds_warn_block_placed(player,chunkGuild);
            return;
        }

        ItemStack item = player.getItemInUse();
        if (item != null && item.equals(WarpManager.tool())) {
            OddJob.getInstance().log("tool in hand");
            // A tool in hand
            event.setCancelled(true);
        }

        //TODO check for `trust` guilds
        event.setCancelled(true);
        MessageManager.guilds_not_allowed(player,chunkGuild);
    }
}

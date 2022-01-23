package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.inventory.Inventory;

public class OnPlayerInventoryCloseEvent implements Listener {
    @EventHandler
    public void bookClose(PlayerTakeLecternBookEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getLectern().getChunk();
        Guild guildClicked = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
        Guild guildPlayer = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
        if (guildClicked != null) {
            if (guildPlayer != null && guildPlayer == guildClicked){
                return;
            }
            MessageManager.guilds_no_stealing(player);
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        Entity stand = OddJob.getInstance().getPlayerManager().openSpirit.get(player.getUniqueId());
        if (stand != null)  {
            OddJob.getInstance().getPlayerManager().replaceSpirit(stand, player.getUniqueId(), true);
        }
        OddJob.getInstance().getPlayerManager().openSpirit.remove(player.getUniqueId());
    }
}

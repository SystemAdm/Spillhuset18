package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.Managers.PlayerManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.ChunkCord;
import com.spillhuset.oddjob.Utils.Guild;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class OnPlayerMoveEvent implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClaim(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = player.getLocation().getChunk();
        Guild owner = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
        if (owner == null) {
            UUID claimer = OddJob.getInstance().getGuildsManager().autoClaim.get(player.getUniqueId());
            if (claimer != null) {
                OddJob.getInstance().getGuildsManager().claim(player, OddJob.getInstance().getGuildsManager().getGuildByUuid(claimer));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        UUID yourGuild = OddJob.getInstance().getGuildsManager().getMembers().get(uuid);
        Guild visitingGuild = OddJob.getInstance().getGuildsManager().getGuildByZone(Zone.WILD);

        boolean notify = false;
        if (event.getTo() != null) {
            Chunk chunk = event.getTo().getChunk();
            visitingGuild = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
            if (visitingGuild == null)
                visitingGuild = OddJob.getInstance().getGuildsManager().getGuildByZone(Zone.WILD);
            if (!PlayerManager.playerTracker.containsKey(uuid)) {
                PlayerManager.playerTracker.put(uuid, new ChunkCord(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ(), visitingGuild.getUuid()));
                notify = true;
            } else {
                ChunkCord cords = PlayerManager.playerTracker.get(uuid);
                if (chunk.getX() != cords.getX()) {
                    cords.setX(chunk.getX());
                }
                if (chunk.getZ() != cords.getZ()) {
                    cords.setZ(chunk.getZ());
                }
                if (chunk.getWorld().getUID() != cords.getWorld()) {
                    cords.setWorld(chunk.getWorld().getUID());
                }
                if (visitingGuild.getUuid() != cords.getGuild()) {
                    cords.setGuild(visitingGuild.getUuid());
                    notify = true;
                }
                PlayerManager.playerTracker.put(uuid, cords);
            }
        }
        if (notify) {
            if (visitingGuild.getUuid() == yourGuild) {
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_PURPLE + "Home sweet home"));
            } else {
                TextComponent text;
                Zone zone = visitingGuild.getZone();
                switch (zone) {
                    case WAR, SAFE, WILD -> text = new TextComponent(zone.getColoredString());
                    default -> text = new TextComponent(zone.getColoredString(visitingGuild.getName()));
                }
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
            }
        }
    }
}

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class OnMoveEvent implements Listener {


    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        UUID yourGuild = OddJob.getInstance().getGuildsManager().getMembers().get(uuid);
        UUID visitingGuild = null;
        boolean notify = false;
        if (event.getTo() != null) {
            Chunk chunk = event.getTo().getChunk();
            visitingGuild = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
            if (!PlayerManager.tracker.containsKey(uuid)) {
                PlayerManager.tracker.put(uuid, new ChunkCord(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ(), visitingGuild));
                notify = true;
            } else {
                ChunkCord cords = PlayerManager.tracker.get(uuid);
                if (chunk.getWorld().getUID() == cords.getWorldUuid()) {
                    if (!((chunk.getX() == cords.getX() && chunk.getZ() == cords.getZ()) || (visitingGuild == cords.getVisitingGuild()))) {
                        notify = true;
                    }
                }
                PlayerManager.tracker.put(uuid,cords);
            }
        }

        if (notify) {
            if (visitingGuild == yourGuild) {
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_PURPLE + "Home sweet home"));
            } else {
                Guild guild;
                TextComponent text;
                guild = OddJob.getInstance().getGuildsManager().getGuildByUuid(visitingGuild);
                if (visitingGuild != null) {
                    Zone zone = guild.getZone();
                    switch (zone) {
                        case WAR, SAFE, WILD -> text = new TextComponent(zone.getColoredString());
                        default -> {
                            text = new TextComponent(zone.getColoredString(guild.getName()));
                        }
                    }
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
                } else {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Zone.WILD.getColoredString()));
                }

            }
        }
    }
}

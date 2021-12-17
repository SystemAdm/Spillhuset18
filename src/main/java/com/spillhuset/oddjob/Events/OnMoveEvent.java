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

import java.util.UUID;

public class OnMoveEvent implements Listener {


    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        UUID yourGuild = OddJob.getInstance().getGuildsManager().getMembers().get(uuid);
        Guild visitingGuild = OddJob.getInstance().getGuildsManager().getGuildByZone(Zone.WILD);

        boolean notify = false;
        if (event.getTo() != null) {
            Chunk chunk = event.getTo().getChunk();
            visitingGuild = OddJob.getInstance().getGuildsManager().getGuildByChunk(chunk);
            if (!PlayerManager.tracker.containsKey(uuid)) {
                OddJob.getInstance().log("none found");
                PlayerManager.tracker.put(uuid, new ChunkCord(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ(), visitingGuild.getUuid()));
                notify = true;
            } else {
                ChunkCord cords = PlayerManager.tracker.get(uuid);
                if ((chunk.getX() != cords.getX() || chunk.getZ() != cords.getZ() || chunk.getWorld().getUID() != cords.getWorld())) {
                    OddJob.getInstance().log("change fuck");
                    cords.setX(chunk.getX());
                    cords.setZ(chunk.getZ());
                    cords.setWorld(chunk.getWorld().getUID());
                    cords.setGuild(visitingGuild.getUuid());
                }
                if (visitingGuild.getUuid() != cords.getGuild()) {
                    notify = true;
                }
                PlayerManager.tracker.put(uuid, cords);
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
                    default -> {
                        text = new TextComponent(zone.getColoredString(visitingGuild.getName()));
                    }
                }
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
            }
        }
    }
}

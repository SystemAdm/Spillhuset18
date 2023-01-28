package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.PlayerSQL;
import com.spillhuset.oddjob.Utils.OddPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.protocol.packet.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class PlayerManager {
    private final HashMap<UUID, OddPlayer> players;
    private final HashMap<UUID, Scoreboard> scoreboards = new HashMap<>();
    private final HashMap<UUID, UUID> inside = new HashMap<>();
    private final HashMap<UUID,BukkitTask> combat = new HashMap<>();

    public PlayerManager() {
        players = PlayerSQL.load();
    }

    public OddPlayer get(UUID player) {
        return players.get(player);
    }

    public OddPlayer get(String player) {
        for (UUID uuid : players.keySet()) {
            if (players.get(uuid).getName().equals(player)) {
                return players.get(uuid);
            }
        }
        return null;
    }

    public void add(UUID player) {
        players.put(player, PlayerSQL.get(player));
    }

    public void incBoughtHomes(UUID uniqueId) {
        players.get(uniqueId).incBoughtHomes();
    }

    public void feedOne(CommandSender sender) {
        Player player = (Player) sender;
        player.setFoodLevel(20);
        MessageManager.essentials_feed_self(sender);
    }

    public void feedAll(CommandSender sender) {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setFoodLevel(20);
            MessageManager.essentials_feed(player);
            players.add(player.getName());
        }
        MessageManager.essentials_feed_all(sender,players);
    }

    public void feedMany(String[] args, CommandSender sender) {
        List<String> players = new ArrayList<>();
        for (String name : args) {
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                player.setFoodLevel(20);
                MessageManager.essentials_feed(player);
                players.add(player.getName());
            }
        }
        MessageManager.essentials_feed_many(sender,players);
    }

    public void feedOne(String arg, CommandSender sender) {
        Player player = Bukkit.getPlayer(arg);
        if (player != null) {
            player.setFoodLevel(20);
            MessageManager.essentials_feed(player);
            MessageManager.essentials_feed_one(sender,player.getName());
        }
    }

    public void healOne(CommandSender sender) {
        Player player = (Player) sender;
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        MessageManager.essentials_heal_self(sender);
    }

    public void healAll(CommandSender sender) {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != null) {
                player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                MessageManager.essentials_healed(player);
                players.add(player.getName());
            }
        }
        MessageManager.essentials_heal_all(sender,players);
    }

    public void healOne(String arg, CommandSender sender) {
        Player player = Bukkit.getPlayer(arg);
        if (player != null) {
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
            MessageManager.essentials_healed(player);
            MessageManager.essentials_heal_one(sender,player.getName());
        }
    }

    public void healMany(String[] args, CommandSender sender) {
        List<String> players = new ArrayList<>();
        for (String name:args) {
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                MessageManager.essentials_healed(player);
                players.add(player.getName());
            }
        }
        MessageManager.essentials_heal_many(sender,players);
    }

    public Collection<OddPlayer> getAll() {
        return players.values();
    }

    public List<String> listAll() {
        List<String> list = new ArrayList<>();
        for (OddPlayer oddPlayer : players.values()) {
            list.add(oddPlayer.getName());
        }
        return list;
    }

    public void setScoreboard(UUID uniqueId, Scoreboard scoreboard) {
        scoreboards.put(uniqueId, scoreboard);
    }

    public Scoreboard getScoreboard(UUID uniqueId) {
        return scoreboards.remove(uniqueId);
    }

    public UUID getInside(UUID player) {
        return inside.get(player);
    }

    public void setInside(UUID player, UUID guild) {
        inside.put(player, guild);
    }

    public void combat(Entity entity) {
        if (entity instanceof Player player) {
            OddJob.getInstance().log("UUID in: "+player.getUniqueId());
            if (combat.get(player.getUniqueId()) == null) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(ChatColor.RED+"In combat"));
            }
            combat.remove(player.getUniqueId());
            combat.put(player.getUniqueId(),Bukkit.getScheduler().runTaskLaterAsynchronously(OddJob.getInstance(), () -> removeCombat(player.getUniqueId()),200));
        }
    }

    private void removeCombat(UUID uniqueId) {
        combat.remove(uniqueId);
        OddJob.getInstance().log("UUID out: "+uniqueId);
        Player player = Bukkit.getPlayer(uniqueId);
        if (player != null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(ChatColor.GREEN+"Out of combat"));
        }
    }
}

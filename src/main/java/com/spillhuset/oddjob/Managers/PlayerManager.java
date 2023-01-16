package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.SQL.PlayerSQL;
import com.spillhuset.oddjob.Utils.OddPlayer;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class PlayerManager {
    private final HashMap<UUID, OddPlayer> players;
    private final HashMap<UUID, Scoreboard> scoreboards = new HashMap<>();
    private final HashMap<UUID, UUID> inside = new HashMap<>();

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
    }

    public void feedAll() {
    }

    public void feedMany(String[] args, CommandSender sender) {
    }

    public void feedOne(String arg, CommandSender sender) {
    }

    public void healOne(CommandSender sender) {
    }

    public void healAll() {
    }

    public void healOne(String arg, CommandSender sender) {
    }

    public void healMany(String[] args, CommandSender sender) {
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
        inside.put(player,guild);
    }
}

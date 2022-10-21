package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.SQL.PlayerSQL;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.command.CommandSender;

import java.util.*;

public class PlayerManager {
    private final HashMap<UUID, OddPlayer> players;

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
        for (OddPlayer oddPlayer:players.values()) {
            list.add(oddPlayer.getName());
        }
        return list;
    }
}

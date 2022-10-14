package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.SQL.PlayerSQL;
import com.spillhuset.oddjob.Utils.OddPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerManager {
    private final HashMap<UUID,OddPlayer> players;
    public PlayerManager() {
        players = new HashMap<>();
    }
    public OddPlayer get(UUID player) {
        return players.get(player);
    }

    public void add(UUID player) {
        players.put(player,PlayerSQL.get(player));
    }

    public void remove(UUID player) {
        players.remove(player);
    }
}

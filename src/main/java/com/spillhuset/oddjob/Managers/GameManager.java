package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.SQL.GameSQL;
import com.spillhuset.oddjob.Utils.Game;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class GameManager {
    private HashMap<UUID, Game> games;
    public GameManager() {
        load();
    }

    public void load() {
        games = GameSQL.load();
    }

    @Nullable
    public UUID in(UUID player) {
        for (Game game :games.values()) {
            if (game.in(player)) return game.getUuid();
        }
        return null;
    }

    public void died(UUID game, UUID target, UUID damage) {
    }

    public void save() {
        GameSQL.save(games);
    }
}

package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Utils.Game;

import java.util.HashMap;
import java.util.UUID;

public class GameManager {
    /**
     * UUID Player | UUID Game
     */
    private final HashMap<UUID, UUID> playerGame = new HashMap<>();
    /**
     * UUID Game | Game
     */
    private final HashMap<UUID, Game> games = new HashMap<>();

    public UUID in(UUID player) {
        return playerGame.get(player);
    }

    public Game get(UUID game) {
        return games.get(game);
    }
}

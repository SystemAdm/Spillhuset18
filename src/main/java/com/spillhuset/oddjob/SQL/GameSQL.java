package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;
import com.spillhuset.oddjob.Utils.Game;

import java.util.HashMap;
import java.util.UUID;

public class GameSQL extends MySQLManager {
    public static HashMap<UUID, Game> load() {
        HashMap<UUID,Game> games = new HashMap<>();
        return games;
    }

    public static void save(HashMap<UUID, Game> games) {
    }
}

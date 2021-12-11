package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Changed;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.PlayerSQL;
import com.spillhuset.oddjob.Utils.OddPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerManager {
    static HashMap<UUID, OddPlayer> oddPlayers = new HashMap<>();

    public List<String> listString() {
        OddJob.getInstance().log("listing");
        List<String> list = new ArrayList<>();
        OddJob.getInstance().log("size:" + oddPlayers.size());
        for (OddPlayer oddPlayer : oddPlayers.values()) {
            OddJob.getInstance().log("item-player");
            list.add(oddPlayer.getName());
        }
        return list;
    }

    /**
     * Saving to database
     *
     * @param oddPlayer OddPlayer or Everyone
     */
    public void save(OddPlayer oddPlayer) {
        if (oddPlayer == null) {
            PlayerSQL.save(oddPlayers);
        } else {
            PlayerSQL.save(oddPlayer);
        }
    }

    /**
     * Loading a joining player to the memory
     *
     * @param uuid UUID of the Player
     * @param name String name of the Player
     */
    public void join(UUID uuid, String name) {
        OddPlayer oddPlayer = PlayerSQL.get(uuid);
        OddJob.getInstance().log("got-player");
        Long joined = System.currentTimeMillis() / 1000;
        OddJob.getInstance().log("joined " + joined);
        if (oddPlayer == null) {
            OddJob.getInstance().log("added");
            HistoryManager.add(uuid, Changed.first, "", String.valueOf(joined));
            oddPlayers.put(uuid, new OddPlayer(uuid, name, joined));
        } else {
            OddJob.getInstance().log("loaded");
            HistoryManager.add(uuid, Changed.name, oddPlayer.getName(), name);
            oddPlayers.put(uuid, new OddPlayer(uuid, name));
        }

        HistoryManager.add(uuid, Changed.joined, "", String.valueOf(joined));
    }

    /**
     * Loads a Player from the database
     *
     * @param uuid UUID of the Player
     */
    public void load(UUID uuid) {
        PlayerSQL.load(uuid);
    }

    /**
     * Loads a Player from the database
     *
     * @param name String name of the Player
     * @return Boolean
     */
    public boolean load(String name) {
        boolean load = false;
        OddPlayer oddPlayer = PlayerSQL.get(name);
        if (oddPlayer != null) {
            oddPlayers.put(oddPlayer.getUuid(), oddPlayer);
            load = true;
        }
        return load;
    }

    /**
     * Gets and loads a Player from the database
     *
     * @param name String name of the Player
     * @return OddPlayer
     */
    public static OddPlayer get(String name) {
        for (UUID uuid : oddPlayers.keySet()) {
            OddPlayer oddPlayer = oddPlayers.get(uuid);
            if (oddPlayer.getName().equalsIgnoreCase(name)) {
                return oddPlayer;
            }
        }
        return PlayerSQL.get(name);
    }

    public static OddPlayer get(UUID uuid) {
        OddPlayer oddPlayer = oddPlayers.get(uuid);
        if (oddPlayer != null) return oddPlayer;
        return PlayerSQL.get(uuid);
    }
}

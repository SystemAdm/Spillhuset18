package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.SQL.ArenaSQL;
import com.spillhuset.oddjob.Utils.Arena;
import org.bukkit.World;

import java.util.HashMap;
import java.util.UUID;

public class ArenaManager {
    /**
     * Player UUID | Arena name
     */
    private final HashMap<UUID, String> inside = new HashMap<>();
    private final HashMap<UUID, String> arenaEdit = new HashMap<>();
    private HashMap<String, Arena> arenas = new HashMap<>();

    /**
     * Returns UUID of Arena, if inside one
     *
     * @param uniqueId UUID Player
     * @return String Arena name
     */
    public String inArena(UUID uniqueId) {
        return inside.get(uniqueId);
    }

    public void died(String arenaUUID, UUID targetUUID, UUID damageUUID) {
    }

    public void create(String name, World world, UUID playerUUID) {
        if (arenas.containsKey(name)) {
            // exists
        } else {
            arenas.put(name, new Arena(name, world));
            // created
            arenaEdit.put(playerUUID, name);
        }
    }

    public void save(UUID playerUUID) {
        String name = arenaEdit.get(playerUUID);
        arenaEdit.remove(playerUUID);
        save(name);
    }

    private void save(String name) {
        ArenaSQL.save(arenas.get(name));
        // Saved
    }
}

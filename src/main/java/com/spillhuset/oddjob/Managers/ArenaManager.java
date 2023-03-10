package com.spillhuset.oddjob.Managers;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.Region;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.ArenaSQL;
import com.spillhuset.oddjob.Utils.Arena;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ArenaManager {
    private HashMap<Chunk, Arena> chunks = new HashMap<>();
    private HashMap<UUID, Arena> arenas;

    public void create() {

    }

    public void create(String name, Player player, Region region) {
        List<BlockVector2> chunks = new ArrayList<>();
        boolean error = false;

        // New Arena
        Arena arena = new Arena(name, player.getWorld());
        // Find the chunks
        BlockVector2 ch = region.getChunks().iterator().next();

        int maxX = ch.getX();
        int maxZ = ch.getZ();
        int minX = ch.getX();
        int minZ = ch.getZ();

        // Check world
        World world = Bukkit.getWorld(player.getWorld().getUID());
        if (world == null) return;

        Guild guild = OddJob.getInstance().getGuildsManager().getGuildByZone(Zone.ARENA);

        // Check if chunk is owned by a guild
        for (BlockVector2 chunk : region.getChunks()) {
            int x = chunk.getBlockX();
            int z = chunk.getBlockZ();
            maxX = Math.max(x, maxX);
            minX = Math.min(x, minX);
            maxZ = Math.max(z, maxZ);
            minZ = Math.min(z, minZ);
            if (OddJob.getInstance().getGuildsManager().getGuildByCords(x, z, player.getWorld()) == null) {
                chunks.add(chunk);
            } else {
                error = true;
            }
        }
        if (error) {
            OddJob.getInstance().log("area not unguilded");
            return;
        }

        // Claim the chunks
        for (BlockVector2 vector2 : chunks) {
            Chunk chunk = world.getChunkAt(vector2.getX(), vector2.getZ());
            OddJob.getInstance().getGuildsManager().claim(chunk, guild);
            this.chunks.put(chunk, arena);
        }

        // Save
        arenas.put(arena.getUuid(), arena);
        ArenaSQL.save(arena);
    }
}

package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    private final UUID id;
    private List<UUID> queue = new ArrayList<>();
    private List<UUID> inGame = new ArrayList<>();
    private List<UUID> alive = new ArrayList<>();
    private int maxPlayers = 10;
    private int teams = 1;
    private int playersPerTeam = 1;
    private Location lobby;

    private Game() {
        this.id = UUID.randomUUID();
    }

    public void addQueue(UUID uuid) {
        queue.add(uuid);
    }

    public void enable() {
    }

    public void disable() {
    }

    public void start(boolean force) {
    }

    public void stop(boolean force) {
    }

    public void prepare() {
        int size = Math.min(queue.size() - 1, maxPlayers);
        for (int i = size; i >= 0; i--) {
            UUID target = queue.get(i);
            OddJob.getInstance().getTeleportManager().request(target, lobby);
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(OddJob.getInstance(), () -> {

        }, 0, 600);
    }
}

package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    private final NamespacedKey key;
    private final UUID id;
    private final List<UUID> queue = new ArrayList<>();
    private final List<UUID> inGame = new ArrayList<>();
    private final List<UUID> alive = new ArrayList<>();
    private final int maxPlayers = 10;
    private final int teams = 1;
    private final int playersPerTeam = 1;
    private Location lobby;
    private BukkitTask timer;

    private Game() {
        this.id = UUID.randomUUID();
        this.key = new NamespacedKey(OddJob.getInstance(),"game-"+this.id);
    }

    public void addQueue(UUID uuid) {
        queue.add(uuid);
    }

    public void enable() {
    }

    public void disable() {
    }

    public void start(boolean force) {
        timer.cancel();
        AtomicInteger i = new AtomicInteger(10);
        timer = Bukkit.getScheduler().runTaskTimer(OddJob.getInstance(), () -> {
            BossBar bossBar = Bukkit.getBossBar(key);
            if (i.decrementAndGet() == 0){
                start(false);
            }
        }, 0, 20);
    }

    public void stop(boolean force) {
    }

    public void prepare() {
        // Lowest number of size of queue or max players
        int size = Math.min(queue.size() - 1, maxPlayers);

        // Teleport the queuing players to the lobby
        for (int i = size; i >= 0; i--) {
            UUID target = queue.get(i);
            OddJob.getInstance().getTeleportManager().request(target, lobby);
            inGame.add(target);
        }

        // Start time till start
        AtomicInteger i = new AtomicInteger(10);
        timer = Bukkit.getScheduler().runTaskTimer(OddJob.getInstance(), () -> {
            if (i.decrementAndGet() == 0){
                start(false);
            }
        }, 0, 20);
    }
    public boolean in(UUID uuid) {
        return inGame.contains(uuid);
    }
    public int getMaxPlayers() {
        return maxPlayers;
    }

}

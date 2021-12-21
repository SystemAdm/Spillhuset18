package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.CountdownSpeed;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class TeleportManager {
    HashMap<UUID, BukkitTask> timers = new HashMap<>();
    private HashMap<UUID, UUID> requests = new HashMap<>();
    private HashMap<UUID, BukkitTask> requestsReset = new HashMap<>();


    public void teleport(Player player, Location location, Plugin plugin) {
        teleport(player, location, plugin, CountdownSpeed.normal);
    }

    public void teleport(Player player, Location location, Plugin plugin, CountdownSpeed speed) {
        if (speed == CountdownSpeed.instant) {
            player.teleport(location);
        } else {
            timers.put(player.getUniqueId(), new BukkitRunnable() {
                int i = 10;

                @Override
                public void run() {
                    if (!player.isOnline()) cancel();
                    if (PlayerManager.isFrozen(player.getUniqueId())) {
                        cancel();
                    }
                    if (PlayerManager.inCombat(player.getUniqueId())) {
                        MessageManager.teleports_in_combat(player);
                        cancel();
                    }
                    if (i > 0) {
                        MessageManager.teleports_countdown(i, player);
                    } else {
                        player.teleport(location);
                        cancel();
                    }
                    i--;
                }
            }.runTaskTimer(OddJob.getInstance(), speed.getTimer(), 10L));
        }
    }

    public void cancel(UUID uuid) {
        if (timers.containsKey(uuid)) {
            timers.get(uuid).cancel();
            timers.remove(uuid);
        }
    }

    public void deny(UUID destination, UUID requester) {
        if (requests.get(requester) == destination) {
            if (requestsReset.containsKey(requester)) requestsReset.get(requester).cancel();
            removeRequests(requester);
        }
    }

    public void removeRequests(UUID requester) {
        requests.remove(requester);
        requestsReset.remove(requester);
    }

    public void request(Player requester, String target) {
        Player destination = Bukkit.getPlayer(target);
        if (destination != null) {
            OddPlayer oddDestination = OddJob.getInstance().getPlayerManager().get(destination.getUniqueId());
            boolean blacklist = oddDestination.getBlackList().contains(requester.getUniqueId());
            boolean denied = oddDestination.getDenyTpa();
            boolean whitelist = oddDestination.getWhiteList().contains(requester.getUniqueId());

            if (blacklist || (denied && !whitelist)) {
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        MessageManager.teleports_request_denied(requester, destination);
                    }
                }.runTaskLater(OddJob.getInstance(),40);

                return;
            }

            if (requests.get(requester.getUniqueId()) == destination.getUniqueId()) {
                MessageManager.teleports_request_already_sent(requester, destination);
                return;
            }

            requestsReset.get(requester.getUniqueId()).cancel();
            //TODO CostManager.transaction(Account.POCKET,requester.getUniqueId(), Plu.TELEPORT_REQUEST);
            requests.put(requester.getUniqueId(), destination.getUniqueId());
            requestsReset.put(requester.getUniqueId(), new BukkitRunnable() {

                @Override
                public void run() {
                    if (requestsReset.containsKey(requester.getUniqueId())) {
                        removeRequests(requester.getUniqueId());
                        requestsReset.remove(requester.getUniqueId());
                        if (!destination.isOnline()) {
                            MessageManager.teleports_destination_offline(requester, destination);
                            cancel();
                            return;
                        }

                        if (!requester.isOnline()) {
                            MessageManager.teleports_requester_offline(requester, destination);
                            cancel();
                            return;
                        }

                        MessageManager.teleports_timed_out(requester, destination);
                        cancel();
                    }
                }
            }.runTaskLater(OddJob.getInstance(), 1200L));
        }
    }
}

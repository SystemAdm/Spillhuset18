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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeleportManager {
    private final HashMap<UUID, BukkitTask> timers = new HashMap<>();
    private final HashMap<UUID, UUID> requests = new HashMap<>();
    private final HashMap<UUID, BukkitTask> requestsReset = new HashMap<>();


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

    public void removeRequests(UUID requester) {
        requests.remove(requester);
        requestsReset.remove(requester);
    }

    public void request(Player requester, String target) {
        if (requests.containsKey(requester.getUniqueId())) {
            Player old = Bukkit.getPlayer(requests.get(requester.getUniqueId()));
            MessageManager.teleports_request_changing(requester,old);
        }
        Player destination = Bukkit.getPlayer(target);
        if (destination != null) {
            OddPlayer oddDestination = OddJob.getInstance().getPlayerManager().get(destination.getUniqueId());
            boolean blacklist = oddDestination.getBlackList().contains(requester.getUniqueId());
            boolean denied = oddDestination.getDenyTpa();
            boolean whitelist = oddDestination.getWhiteList().contains(requester.getUniqueId());

            if (blacklist || (denied && !whitelist)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        MessageManager.teleports_request_denied(requester, destination);
                    }
                }.runTaskLater(OddJob.getInstance(), 40);

                return;
            }
            OddJob.getInstance().log("b"+(destination == requester));
            if (requests.get(requester.getUniqueId()) == destination.getUniqueId()) {
                MessageManager.teleports_request_already_sent(requester, destination);
                return;
            }
            BukkitTask reset = requestsReset.get(requester.getUniqueId());
            if (reset != null) reset.cancel();
            //TODO CostManager.transaction(Account.POCKET,requester.getUniqueId(), Plu.TELEPORT_REQUEST);
            requests.put(requester.getUniqueId(), destination.getUniqueId());
            requestsReset.put(requester.getUniqueId(), new BukkitRunnable() {

                @Override
                public void run() {
                    if (requestsReset.containsKey(requester.getUniqueId())) {
                        OddJob.getInstance().log("c"+(destination == requester));
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
                        OddJob.getInstance().log("d"+(destination == requester));
                        MessageManager.teleports_timed_out(requester, destination);
                        cancel();
                    }
                }
            }.runTaskLater(OddJob.getInstance(), 1200L));
            MessageManager.teleports_request_sent(destination,requester);
        }
    }

    public void accept(Player destination) {
        List<UUID> requestList = new ArrayList<>();
        for (UUID requester : requests.keySet()) {
            if (requests.get(requester) == destination.getUniqueId()) {
                requestList.add(requester);
            }
        }

        if (requestList.size() == 1) {
            accept(destination, Bukkit.getPlayer(requestList.get(0)));
        } else if (requestList.size() > 1) {
            MessageManager.teleports_requests_more(destination, requestList);
        } else {
            MessageManager.teleports_request_none(destination);
        }
    }

    public void deny(Player destination) {
        List<UUID> requestList = new ArrayList<>();
        for (UUID requester : requests.keySet()) {
            if (requests.get(requester) == destination.getUniqueId()) {
                requestList.add(requester);
            }
        }

        if (requestList.size() == 1) {
            deny(destination, Bukkit.getPlayer(requestList.get(0)));
        } else if (requestList.size() > 1) {
            MessageManager.teleports_requests_more(destination, requestList);
        } else {
            MessageManager.teleports_request_none(destination);
        }
    }

    public void accept(Player destination, String requesterName) {
        Player requester = Bukkit.getPlayer(requesterName);
        if (requester == null) {
            MessageManager.errors_find_player(Plugin.teleport, requesterName, destination);
            return;
        }
        if (requests.get(requester.getUniqueId()) == destination.getUniqueId()) {
            accept(destination, requester);
            return;
        }
        MessageManager.teleports_request_no_request(destination, requester);
    }

    public void deny(Player destination, String requesterName) {
        Player requester = Bukkit.getPlayer(requesterName);
        if (requester == null) {
            MessageManager.errors_find_player(Plugin.teleport, requesterName, destination);
            return;
        }
        if (requests.get(requester.getUniqueId()) == destination.getUniqueId()) {
            deny(destination, requester);
            return;
        }
        MessageManager.teleports_request_no_request(destination, requester);
    }

    public void accept(Player destination, Player requester) {
        MessageManager.teleports_request_accepted(destination, requester);
        removeRequests(requester.getUniqueId());
        teleport(requester, destination.getLocation(), Plugin.teleport);
    }

    public void deny(Player destination, Player requester) {
        MessageManager.teleports_request_denied(destination, requester);
        removeRequests(requester.getUniqueId());
    }
}

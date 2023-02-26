package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.TeleportType;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class TeleportManager {
    HashMap<UUID, BukkitTask> teleports = new HashMap<>();
    /**
     * UUID Destination (initiator)
     * UUID Target (requesting to)
     */
    HashMap<UUID, UUID> requests = new HashMap<>();

    public void accept(Player target, @Nullable UUID requesterUUID) {
        // test = request
        for (UUID test : requests.keySet()) {
            // request match
            if (test.equals(requesterUUID)) {
                // hit = target
                UUID hit = requests.get(test);
                if (hit.equals(target.getUniqueId())) {
                    // target online ?
                    Player requester = Bukkit.getPlayer(requesterUUID);
                    if (requester == null) {
                        MessageManager.errors_find_player(Plugin.teleports, OddJob.getInstance().getPlayerManager().get(requesterUUID).getName(), target);
                        return;
                    }
                    // teleport
                    teleport(requester, target.getLocation(), TeleportType.player);
                    MessageManager.teleport_request_accepted(target, requester);
                    requests.remove(requesterUUID);
                }
            }
        }
    }

    public void deny(Player target, @Nullable UUID requesterUUID) {
        // test = request
        for (UUID test : requests.keySet()) {
            // request match
            if (test.equals(requesterUUID)) {
                // hit = target
                UUID hit = requests.get(test);
                if (hit.equals(target.getUniqueId())) {
                    // target online ?
                    Player requester = Bukkit.getPlayer(requesterUUID);
                    if (requester == null) {
                        MessageManager.errors_find_player(Plugin.teleports, OddJob.getInstance().getPlayerManager().get(requesterUUID).getName(), target);
                        return;
                    }
                    // teleport
                    MessageManager.teleport_request_denied(target, requester);
                    requests.remove(requesterUUID);
                }
            }
        }
    }

    /**
     * @param requester requester
     * @param target    target
     */
    public void request(Player requester, String target) {
        UUID requesterUUID = requester.getUniqueId();
        // Is target online?
        Player targetPlayer = Bukkit.getPlayer(target);
        if (targetPlayer == null) {
            MessageManager.errors_find_player(Plugin.teleports, target, requester);
            return;
        }
        // Is target in DB?
        OddPlayer oddTarget = OddJob.getInstance().getPlayerManager().get(targetPlayer.getUniqueId());
        UUID targetUUID = oddTarget.getUuid();
        if (targetUUID == null) {
            MessageManager.errors_find_player(Plugin.teleports, target, requester);
            return;
        }

        // Is requester blacklisted?
        if (oddTarget.getBlackList().contains(requesterUUID)) {
            MessageManager.errors_find_player(Plugin.teleports, target, requester);
            return;
        }

        // Is requester whitelisted?
        if (oddTarget.getDenyTpa()) {
            if (!oddTarget.getWhiteList().contains(requesterUUID)) {
                MessageManager.errors_find_player(Plugin.teleports, target, requester);
                return;
            }
        }

        // send
        requests.put(requesterUUID, targetUUID);
        MessageManager.teleport_request_sent(requester, targetPlayer);
    }

    public void teleport(OddPlayer oddPlayer, Location location) {
        Player player = Bukkit.getPlayer(oddPlayer.getUuid());
        if (player != null) {
            teleport(player, location, TeleportType.player);
        }
    }

    public void teleport(Player player, Location location, TeleportType type) {
        if (teleports.containsKey(player.getUniqueId())) return;
        if (OddJob.getInstance().getPlayerManager().inCombat(player.getUniqueId())) return;
        AtomicInteger i = new AtomicInteger(10);
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 250, 10));
        teleports.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(OddJob.getInstance(), () -> {
            if (OddJob.getInstance().getPlayerManager().inCombat(player.getUniqueId())) cancel(player.getUniqueId());
            i.set(i.get() - 1);
            if (i.get() == 0) {
                MessageManager.teleport(player);
                player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                cancel(player.getUniqueId());
                teleports.remove(player.getUniqueId());
                return;
            }
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.DARK_GREEN + "Teleporting in " + ChatColor.WHITE + i));

            //MessageManager.teleport(player,i.get());
        }, 20, 20));
    }

    private void cancel(UUID uuid) {
        teleports.get(uuid).cancel();
        teleports.remove(uuid);
    }

    public void quit(UUID uniqueId) {
        requests.remove(uniqueId);
    }
}

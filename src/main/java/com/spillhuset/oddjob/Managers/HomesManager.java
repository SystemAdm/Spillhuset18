package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.HomesSQL;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HomesManager {

    public List<String> getList(UUID uuid) {
        return HomesSQL.getList(uuid);
    }

    public void buy(Player player) {
        OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(player.getUniqueId());
        int bought = oddPlayer.getBoughtHomes();

        Plu plu = Plu.PLAYER_HOMES;

        double price = (bought * plu.getValue()) * plu.getMultiplier();

        if (!OddJob.getInstance().getCurrencyManager().checkBank(player.getUniqueId(), price)) {
            MessageManager.homes_cant_afford(player);
            return;
        }

        OddJob.getInstance().getCurrencyManager().subBank(player.getUniqueId(), price);
        OddJob.getInstance().getPlayerManager().incBoughtHomes(player.getUniqueId());
    }

    public void add(CommandSender sender, OddPlayer target, String name, Location location) {
        if (ConfigManager.getBoolean("plugins.guilds")) {
            UUID world = location.getWorld().getUID();
            int x = location.getChunk().getX();
            int z = location.getChunk().getZ();
            // TODO inside guild?
        }

        int setHomes = HomesSQL.getList(target.getUuid()).size();
        int maxHomes = target.getMaxHomes();

        if (setHomes >= maxHomes) {
            MessageManager.homes_max_reached(name, target, sender);
            return;
        }

        if (HomesSQL.get(target.getUuid(), name) != null) {
            MessageManager.homes_exists(name, target, sender);
            return;
        }

        HomesSQL.add(target.getUuid(), location, name);
        MessageManager.homes_successfully_added(name, target, sender);
    }

    public void addGuild(Guild guild, String home, Player player) {
        Location location = player.getLocation();

        int setHomes = HomesSQL.getList(guild.getUuid()).size();
        int maxHomes = guild.getBoughtHomes();

        if (setHomes >= maxHomes) {
            MessageManager.guilds_homes_max_reached(player);
            return;
        }
        if (HomesSQL.get(guild.getUuid(),home) != null) {
            MessageManager.guilds_homes_exists(player,home);
            return;
        }
        HomesSQL.add(guild.getUuid(),location,home);
        MessageManager.guilds_homes_successfully_added(guild,home);
    }

    public void sendList(CommandSender sender, OddPlayer target) {
        MessageManager.homes_send_list(sender, getList(target.getUuid()), target.getMaxHomes());
    }

    public void change(CommandSender sender, OddPlayer target, String name, Location location) {
        if (HomesSQL.get(target.getUuid(), name) != null) {
            MessageManager.homes_exists(name, target, sender);
            return;
        }
        if (ConfigManager.getBoolean("plugins.guilds")) {
            UUID world = location.getWorld().getUID();
            int x = location.getChunk().getX();
            int z = location.getChunk().getZ();
            // TODO inside guild?
        }
        HomesSQL.change(target.getUuid(), name, location);
        MessageManager.homes_successfully_changed(sender, name);
    }

    public void rename(CommandSender sender, OddPlayer target, String nameOld, String nameNew) {
        if (HomesSQL.get(target.getUuid(), nameNew) != null) {
            MessageManager.homes_exists(nameNew, target, sender);
            return;
        }
        HomesSQL.rename(target.getUuid(), nameOld, nameNew);
        MessageManager.homes_successfully_renamed(sender, nameOld, nameNew);
    }

    public void del(CommandSender sender, OddPlayer target, String name) {
        if (HomesSQL.get(target.getUuid(), name) == null) {
            MessageManager.homes_no_name(sender, name);
            return;
        }
        HomesSQL.delete(target.getUuid(), name);
        MessageManager.homes_successfully_deleted(sender, name);
    }

    public void teleport(CommandSender sender, OddPlayer destinationPlayer, String destinationName) {
        if (HomesSQL.get(destinationPlayer.getUuid(), destinationName) == null) {
            MessageManager.homes_no_name(sender, destinationName);
            return;
        }
        Location location = HomesSQL.get(destinationPlayer.getUuid(), destinationName);
        if (ConfigManager.getBoolean("plugin.teleport")) {
            OddJob.getInstance().getTeleportManager().teleport(destinationPlayer, location);
        } else {
            Player player = Bukkit.getPlayer(destinationPlayer.getUuid());
            if (player != null) {
                player.teleport(location);
            }
        }
    }


}

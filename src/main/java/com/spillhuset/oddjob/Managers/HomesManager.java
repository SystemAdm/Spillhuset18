package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.Enums.TeleportType;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.HomesSQL;
import com.spillhuset.oddjob.SQL.PlayerSQL;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.Location;
import org.bukkit.World;
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

        if (!OddJob.getInstance().getCurrencyManager().has(player.getUniqueId(), Account.bank, price)) {
            MessageManager.homes_cant_afford(player);
            return;
        }

        OddJob.getInstance().getCurrencyManager().sub(player.getUniqueId(), Account.bank, price);
        OddJob.getInstance().getPlayerManager().incBoughtHomes(player.getUniqueId());
    }

    public void add(CommandSender sender, OddPlayer target, String name, Location location) {
        if (ConfigManager.getBoolean("plugins.guilds")) {
            World world = location.getWorld();
            int x = location.getChunk().getX();
            int z = location.getChunk().getZ();

            if (OddJob.getInstance().getGuildsManager().getGuildByCords(x, z, world) != null) {
                MessageManager.homes_inside(sender, name);
                return;
            }
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
        int maxHomes = guild.getMaxHomes();

        if (setHomes >= maxHomes) {
            MessageManager.guilds_homes_max_reached(player);
            return;
        }
        if (HomesSQL.get(guild.getUuid(), home) != null) {
            MessageManager.guilds_homes_exists(player, home);
            return;
        }
        HomesSQL.add(guild.getUuid(), location, home);
        MessageManager.guilds_homes_successfully_added(guild, home);
    }

    public void sendList(CommandSender sender, OddPlayer target) {
        MessageManager.homes_send_list(sender, getList(target.getUuid()), target.getMaxHomes());
    }

    public void change(CommandSender sender, OddPlayer target, String name, Location location) {
        if (HomesSQL.get(target.getUuid(), name) == null) {
            MessageManager.homes_not_exists(name, target, sender);
            return;
        }
        if (ConfigManager.getBoolean("plugins.guilds")) {
            World world = location.getWorld();
            int x = location.getChunk().getX();
            int z = location.getChunk().getZ();

            if (OddJob.getInstance().getGuildsManager().getGuildByCords(x, z, world) != null) {
                MessageManager.homes_inside(sender, name);
                return;
            }
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

    public void teleport(Player sender, OddPlayer destinationPlayer, String destinationName) {
        if (HomesSQL.get(destinationPlayer.getUuid(), destinationName) == null) {
            MessageManager.homes_no_name(sender, destinationName);
            return;
        }
        Location location = HomesSQL.get(destinationPlayer.getUuid(), destinationName);
        if (ConfigManager.getBoolean("plugin.currency") && (!sender.isOp() || !sender.hasPermission("currency.override"))) {
            CurrencyManager currencyManager = OddJob.getInstance().getCurrencyManager();
            double price = Plu.PLAYER_HOMES_TELEPORT.getValue();
            if (currencyManager.has(sender.getUniqueId(), Account.pocket, price)) {
                MessageManager.insufficient_funds(sender);
                return;
            }
            currencyManager.sub(sender.getUniqueId(), Account.pocket, price);

        }
        if (ConfigManager.getBoolean("plugin.teleport") && (!sender.isOp() || !sender.hasPermission("currency.override"))) {
            OddJob.getInstance().getTeleportManager().teleport(sender, location, TeleportType.player);
        } else {
            sender.teleport(location);
        }
    }


    public int getMax(UUID uniqueId) {
        return PlayerSQL.get(uniqueId).getMaxHomes();
    }

    public int getCurrent(UUID uniqueId) {
        return HomesSQL.getList(uniqueId).size();
    }

    public void delGuild(Player player, Guild guild, String home) {
        HomesSQL.delete(guild.getUuid(), home);
        MessageManager.guilds_homes_deleted(player, home);
    }

    public void changeGuild(Player player, Guild guild, String home) {
        HomesSQL.change(guild.getUuid(), home, player.getLocation());
        MessageManager.guilds_homes_relocated(player, home);
    }

    public void renameGuild(Player player, Guild guild, String oldName, String newName) {
        if (HomesSQL.get(guild.getUuid(), oldName) == null) {
            MessageManager.guilds_homes_not_exists(player, oldName);
            return;
        }
        if (HomesSQL.get(guild.getUuid(), newName) != null) {
            MessageManager.guilds_homes_exists(player, oldName);
            return;
        }
        HomesSQL.rename(guild.getUuid(), oldName, newName);
        MessageManager.guilds_homes_renamed(player, oldName, newName);
    }

    public void teleportGuild(Player player, Guild guild, String home) {
        if (HomesSQL.get(guild.getUuid(), home) == null) {
            MessageManager.guilds_homes_not_exists(player, home);
            return;
        }
        Location location = HomesSQL.get(guild.getUuid(), home);
        if (ConfigManager.getBoolean("plugin.currency")) {
            CurrencyManager currencyManager = OddJob.getInstance().getCurrencyManager();
            double price = Plu.GUILDS_HOMES_TELEPORT.getValue();
            if (currencyManager.has(player.getUniqueId(), Account.pocket, price)) {
                MessageManager.insufficient_funds(player);
                return;
            }
            currencyManager.sub(player.getUniqueId(), Account.pocket, price);

        }
        if (ConfigManager.getBoolean("plugin.teleport")) {
            OddJob.getInstance().getTeleportManager().teleport(player, location, TeleportType.guild);
        } else {
            if (player != null) {
                player.teleport(location);
            }
        }
    }
}

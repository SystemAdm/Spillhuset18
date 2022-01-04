package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.HomesSQL;
import com.spillhuset.oddjob.Utils.Managers;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public class HomesManager extends Managers {

    public boolean del(@Nonnull CommandSender sender, @Nonnull OddPlayer target, @Nonnull String name) {
        UUID uuid = target.getUuid();

        if (!HomesSQL.exists(name, uuid)) {
            MessageManager.homes_not_found(sender, name);
            return false;
        }

        /* Deleting */
        if (!HomesSQL.delete(uuid, name)) {
            MessageManager.errors_something_somewhere(Plugin.homes, sender);
            return false;
        }

        MessageManager.homes_del_success(sender, name, getList(uuid).size(), getMax(uuid));
        return true;
    }

    /**
     * Adds a Home to the database
     *
     * @param sender   CommandSender Command executor
     * @param target   Player to set Home for
     * @param name     String name of the Home
     * @param location Location of the Home
     * @return Boolean
     */
    public boolean add(@Nonnull CommandSender sender, @Nonnull OddPlayer target, @Nonnull String name, @Nonnull Location location) {
        UUID uuid = target.getUuid();
        // TODO Check if is inside a guild

        /* Check existing */
        if (HomesSQL.exists(name, uuid)) {
            MessageManager.homes_name_already_exist(name, sender);
            return false;
        }

        /* Check limit */
        int count = getCount(uuid);
        int max = getMax(uuid);
        if (count >= max) {
            MessageManager.homes_limit_reached(sender, count, max);
            return false;
        }

        /* Make transaction */
        if (currency) {
            if (!CostManager.transaction(Account.pocket, uuid, Plu.HOMES_SET)) {
                MessageManager.errors_currency_expensive(Plugin.homes, sender);
                return false;
            }
        }

        /* Adding */
        if (!HomesSQL.add(uuid, location, name)) {
            MessageManager.errors_something_somewhere(Plugin.homes, sender);
            return false;
        }

        MessageManager.homes_set_success(sender, name, getList(uuid).size(), getMax(uuid));
        return true;
    }

    /**
     * Returns the number of homes assigned to a UUID
     *
     * @param uuid UUID owner of homes
     * @return Integer number of homes
     */
    private int getCount(@Nonnull UUID uuid) {
        return getList(uuid).size();
    }

    /**
     * Returns a list of String names of the homes owned by the UUID
     *
     * @param uuid UUID owner of homes
     * @return List of String with names of homes
     */
    public List<String> getList(@Nonnull UUID uuid) {
        return HomesSQL.getList(uuid);
    }

    public Location find(@Nonnull OddPlayer player, @Nonnull String name) {
        return HomesSQL.get(player.getUuid(), name);
    }

    public void teleport(CommandSender sender, OddPlayer target, String name) {
        Location location = HomesSQL.get(target.getUuid(), name);
        if (!teleport) {
            Player player = Bukkit.getPlayer(target.getUuid());
            if (player != null && player.isOnline())
                player.teleport(location);
            MessageManager.teleports_to_home(sender, name);
        }
    }

    public int getMax(@Nonnull UUID uuid) {
        int playerManager = OddJob.getInstance().getPlayerManager().get(uuid).getMaxHomes();
        int configManager = ConfigManager.get("homes.ladder.default");
        return playerManager + configManager;
    }

    public void rename(UUID guild, String oldName, String newName) {
        HomesSQL.rename(guild, oldName, newName);
    }

    public void rename(CommandSender sender, OddPlayer target, String nameOld, String nameNew) {
        if (HomesSQL.rename(target.getUuid(), nameOld, nameNew)) {
            MessageManager.homes_name_changed(sender, nameOld, nameNew);
        }

    }

    public void sendList(CommandSender sender, Player target) {
        MessageManager.homes_list(sender, OddJob.getInstance().getHomeManager().getList(target.getUniqueId()), target, getCount(target.getUniqueId()), getMax(target.getUniqueId()));
    }

    public void change(CommandSender sender, OddPlayer target, String name, Location location) {
        if (HomesSQL.change(target.getUuid(), name, location)) {
            MessageManager.homes_location_changed(sender, name);
        }
    }

    public void change(Player player, UUID guild, String name) {
        HomesSQL.change(guild, name, player.getLocation());
    }

    public void addGuild(Player player, UUID uuid, String name) {
        /* Check existing */
        if (HomesSQL.exists(name, uuid)) {
            MessageManager.guilds_homes_name_already_exist(player, name);
            return;
        }

        /* Check limit */
        int count = getCount(uuid);
        int max = OddJob.getInstance().getGuildsManager().getGuildByUuid(uuid).getMaxHomes();
        if (count >= max) {
            MessageManager.homes_limit_reached(player, count, max);
            return;
        }

        if (currency) {
            if (!CostManager.transaction(Account.guild, uuid, Plu.GUILDS_HOMES)) {
                MessageManager.errors_currency_expensive(Plugin.homes, player);
                return;
            }
        }

        /* Adding */
        if (!HomesSQL.add(uuid, player.getLocation(), name)) {
            MessageManager.errors_something_somewhere(Plugin.homes, player);
            return;
        }

        MessageManager.homes_set_success(player, name, getList(uuid).size(), max);
        return;
    }
}

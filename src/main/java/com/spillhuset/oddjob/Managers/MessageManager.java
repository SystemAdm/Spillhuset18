package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MessageManager {
    private static final ChatColor cInfo = ChatColor.LIGHT_PURPLE;
    private static final ChatColor cSuccess = ChatColor.GREEN;
    private static final ChatColor cDanger = ChatColor.RED;
    private static final ChatColor cGuild = ChatColor.BLUE;
    private static final ChatColor cPlayer = ChatColor.GOLD;
    private static final ChatColor cAccount = ChatColor.YELLOW;
    private static final ChatColor cValue = ChatColor.GRAY;
    private static final ChatColor cList = ChatColor.WHITE;

    private static void danger(Plugin plugin, CommandSender sender, String message) {
        sender.sendMessage(cDanger + message);
    }

    private static void success(Plugin plugin, CommandSender sender, String message) {
        sender.sendMessage(cSuccess + message);
    }

    private static void success(Plugin plugin, Guild guild, String message) {
        HashMap<UUID, UUID> members = OddJob.getInstance().getGuildsManager().getMembers();
        for (UUID uuid : members.keySet()) {
            if (members.get(uuid).equals(guild.getUuid())) {
                Player player = Bukkit.getPlayer(members.get(uuid));
                if (player != null) {
                    player.sendMessage(cSuccess + message);
                }
            }
        }
    }

    private static void info(Plugin plugin, CommandSender sender, String message) {
        sender.sendMessage(cInfo + message);
    }

    private static void list(CommandSender sender, String title, List<String> list) {
        sender.sendMessage(ChatColor.AQUA + title);
        for (int i = 0; i < list.size(); i++) {
            sender.sendMessage((i + 1) + ".) " + list.get(i));
        }
        sender.sendMessage(ChatColor.GRAY + "___________________________");
    }

    /* Homes start */
    public static void homes_info(CommandSender sender, int used, int max, double price) {
        Player player = (Player) sender;
        String text = cInfo + "You have used " + cValue + used + cInfo + " homes of " + cValue + max;
        String next = cInfo + "Based on default: " + cValue + ConfigManager.getInt("homes.default");
        String next_next = cInfo + "Previous bought: " + cValue + OddJob.getInstance().getPlayerManager().get(player.getUniqueId()).getBoughtHomes();
        if (ConfigManager.getBoolean("plugin.currency")) text += cInfo + ", next home will cost " + cValue + price;

        sender.sendMessage(next);
        sender.sendMessage(next_next);
        sender.sendMessage(text);

    }

    public static void homes_cant_afford(CommandSender sender) {
        sender.sendMessage("You can't afford more homes");
    }

    public static void homes_exists(String name, OddPlayer target, CommandSender sender) {
        danger(Plugin.homes, sender, "The " + name + " has already been used");
    }

    public static void homes_not_exists(String name, OddPlayer target, CommandSender sender) {
        danger(Plugin.homes, sender, "The " + name + " does not exist");
    }

    public static void homes_max_reached(String name, OddPlayer target, CommandSender sender) {
        danger(Plugin.homes, sender, "Maximum number of homes reached");
    }

    public static void homes_successfully_added(String name, OddPlayer target, CommandSender sender) {
        success(Plugin.homes, sender, "Home " + name + " successfully set");
    }

    public static void homes_send_list(CommandSender sender, List<String> list, int max) {
        list(sender, "List of homes " + list.size() + "/" + max, list);
    }

    public static void homes_no_name(CommandSender sender, String destinationName) {
        danger(Plugin.homes, sender, "No homes found with the name " + destinationName);
    }

    public static void homes_successfully_renamed(CommandSender sender, String nameOld, String nameNew) {
        success(Plugin.homes, sender, "Successfully changed " + nameOld + " to " + nameNew);
    }

    public static void homes_successfully_deleted(CommandSender sender, String name) {
        success(Plugin.homes, sender, "Successfully deleted " + name);
    }

    public static void homes_successfully_changed(CommandSender sender, String name) {
        success(Plugin.homes, sender, "Successfully relocated " + name);
    }
    /* Homes end */

    public static void sendSyntax(Plugin plugin, String toString, CommandSender sender) {
        sender.sendMessage("Valid subcommands are: " + toString);
    }

    /* Errors start */
    public static void errors_denied_console(Plugin plugin, CommandSender sender) {
    }

    public static void errors_denied_op(Plugin plugin, CommandSender sender) {
    }

    public static void errors_denied_players(Plugin plugin, CommandSender sender) {
    }

    public static void errors_too_many_args(Plugin plugin, CommandSender sender) {
        danger(plugin, sender, "Too many arguments");
    }

    public static void errors_denied_others(Plugin plugin, CommandSender sender) {
    }

    public static void errors_something_somewhere(Plugin plugin, CommandSender sender) {
    }

    public static void errors_too_few_args(Plugin plugin, CommandSender sender) {
        danger(plugin, sender, "Too few arguments");
    }

    public static void errors_find_player(Plugin plugin, String targetName, CommandSender sender) {
        danger(plugin, sender, "Sorry, we can't find player with name " + ChatColor.GOLD + targetName);
    }
    /* Errors end*/

    /* Guilds start */
    public static void guilds_not_associated(CommandSender sender) {
        danger(Plugin.guilds, sender, "You are not associated with any guild");
    }

    public static void guilds_already_associated(CommandSender sender, String name) {
        danger(Plugin.guilds, sender, "You are already associated with the guild " + name);
    }

    public static void auctions_not_area(Player seller) {
    }

    public static void guilds_not_found(CommandSender sender, String arg) {
    }

    public static void guilds_disband_info(CommandSender sender) {
        info(Plugin.guilds, sender, "You are about to this band your guild.");
        info(Plugin.guilds, sender, "Please confirm the action with " + cValue + "/guilds disband confirm");
    }

    public static void guilds_list(Plugin plugin, CommandSender sender, List<String> list, int i) {
    }
    /* Guilds end */

    public static void currency_transferred(CommandSender sender, String sender_account, String sender_name, boolean sender_guild, String receiver_account, String receiver_name, boolean receiver_guild, double value) {
        if (sender.getName().equalsIgnoreCase(sender_name) && sender.getName().equalsIgnoreCase(receiver_name))
            success(Plugin.currency, sender, "You've transferred " + cValue + value + cSuccess + " from " + cAccount + sender_account + cSuccess + " to " + cAccount + receiver_account);
        else if (sender.getName().equalsIgnoreCase(sender_name))
            success(Plugin.currency, sender, "You've transferred " + cValue + value + cSuccess + " from " + cAccount + sender_account + cSuccess + " to " + ((receiver_guild) ? cGuild : cPlayer) + receiver_name + " " + cAccount + receiver_account);
        else if (sender.getName().equalsIgnoreCase(receiver_name))
            success(Plugin.currency, sender, "You've transferred " + cValue + value + cSuccess + " from " + ((sender_guild) ? cGuild : cPlayer) + sender_name + " " + cAccount + sender_account + cSuccess + " to " + cAccount + receiver_account);
        else
            success(Plugin.currency, sender, "You've transferred " + cValue + value + cSuccess + "from " + cAccount + sender_account + cSuccess + " to " + ((sender_guild) ? cGuild : cPlayer) + receiver_name + " " + cAccount + receiver_account);
    }

    public static void invalidNumber(Plugin plugin, CommandSender sender, String t) {
    }

    public static void warps_successfully_added(Player player, String name) {
        success(Plugin.warps, player, "Successfully added warp " + name);
    }

    public static void warps_successfully_deleted(Player player, String name) {
        success(Plugin.warps, player, "Successfully deleted warp " + name);
    }

    public static void warps_send_list(CommandSender sender, HashMap<UUID, Warp> warps) {
        List<String> list = new ArrayList<>();
        for (Warp warp : warps.values()) {
            String name = warp.getName();
            if (ConfigManager.getBoolean("plugin.currency") && warp.hasCost()) {
                name += " cost=`" + cValue + warp.getCost() + cList + "`";
            }
            if (warp.isProtected()) {
                name += ChatColor.RED + " *";
            }
            list.add(name);
        }
        list(sender, "List of warps (" + warps.size() + ")", list);
    }

    public static void warps_protected(Player player, String name) {
        danger(Plugin.warps, player, "The warp `" + cValue + name + cDanger + "` is protected");
    }

    public static void warps_cant_afford(Player player, String name, double cost) {
        danger(Plugin.warps, player, "You can't afford `" + cValue + cost + cDanger + "` to use this warp");
    }

    public static void warps_exists(CommandSender sender, String nameNew) {
        danger(Plugin.warps, sender, "A warp named `" + cValue + nameNew + cDanger + "` does already exists");
    }

    public static void warps_successfully_renamed(CommandSender sender, String nameOld, String nameNew) {
        success(Plugin.warps, sender, "Successfully rename warp `" + cValue + nameOld + cSuccess + "` to `" + cValue + nameNew + cSuccess + "`");
    }

    public static void warps_not_exists(CommandSender player, String name) {
        danger(Plugin.warps, player, "A warp named doesn't exists");
    }

    public static void warps_successfully_relocated(Player player, String name) {
        success(Plugin.warps, player, "Successfully relocated `" + cValue + name + cSuccess + "` to your position");
    }

    public static void warps_successfully_cost(CommandSender sender, String name, double value) {
        success(Plugin.warps, sender, "Successfully set cost on `" + cValue + name + cSuccess + "` to `" + cValue + value + cSuccess + "`");
    }

    public static void warps_successfully_passwd(CommandSender sender, String name, String passwd) {
        success(Plugin.warps, sender, "Successfully set password on `" + cValue + name + cSuccess + "` to `" + cValue + passwd + cSuccess + "`");
    }

    public static void locks_already_locked(Player player) {
        danger(Plugin.locks, player, "This block is already locked");
    }

    public static void locks_successfully_locked(Player player, String name) {
        success(Plugin.locks, player, "You have successfully locked `" + cValue + name + cSuccess + "`");
    }

    public static void locks_not_locked(Player player, String name) {
        danger(Plugin.locks, player, "This " + name + " is not locked");
    }

    public static void locks_successfully_unlocked(Player player, String name) {
        success(Plugin.locks, player, "You have successfully unlocked " + name);
    }

    public static void locks_owned(Player player, String name) {
        danger(Plugin.locks, player, "This " + name + " is owned by someone else");
    }

    public static void locks_broken(Player player, String name) {
        info(Plugin.locks, player, "Lock on " + name + " broken");
    }

    public static void locks_locked(Player player, String name) {
        danger(Plugin.locks, player, "This " + name + " is locked!");
    }

    public static void guilds_exists(CommandSender sender, String name) {
        danger(Plugin.guilds, sender, "Guild named " + name + " does already exists");
    }

    public static void guilds_request_already_sent(Player player, Guild guild) {
        info(Plugin.guilds, player, "You have already sent a request to join " + guild.getName());
    }

    public static void guilds_invited_only(Player player, String name) {
        info(Plugin.guilds, player, "The guild " + name + " is set to invited only");
    }

    public static void guilds_welcome(Player player, Guild guild) {
        success(Plugin.guilds, player, "Welcome to the guild " + guild.getName());
    }

    public static void guilds_invite_already_sent(OddPlayer target, Guild guild) {
        Player player = Bukkit.getPlayer(target.getUuid());
        info(Plugin.guilds, player, "You have already sent a request to join " + guild.getName());
    }

    public static void guilds_invite_denied(Player player, Guild guild) {
        danger(Plugin.guilds, player, "You do not have sufficient privileges in the guild " + guild.getName());
    }

    public static void guilds_privileges(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        danger(Plugin.guilds, player, "Insufficient privileges in the guild");
    }

    public static void guilds_successfully_set_open(Player player, boolean setOpen) {
        success(Plugin.guilds, player, "Successfully changed guilds open for all, to: " + setOpen);
    }

    public static void guilds_successfully_set_name(CommandSender sender, String name) {
        success(Plugin.guilds, sender, "Successfully changed guilds name, to: " + name);
    }

    public static void guilds_homes_max_reached(CommandSender sender) {
        danger(Plugin.homes, sender, "Maximum number of homes reached");
    }

    public static void guilds_homes_exists(CommandSender sender, String name) {
        danger(Plugin.homes, sender, "The " + name + " has already been used");
    }

    public static void guilds_homes_successfully_added(Guild guild, String name) {
        success(Plugin.homes, guild, "Home " + name + " successfully set");
    }


    public static void locks_added(Player sender, String name) {
        success(Plugin.locks, sender, "Successfully added " + name);
    }

    public static void locks_deleted(Player sender, String name) {
        success(Plugin.locks, sender, "Successfully removed " + name);
    }

    public static void insufficient_funds(CommandSender sender) {
        danger(Plugin.currency, sender, "Insufficient funds.");
    }

    public static void currency_holding(Player sender, double pocket, double bank) {
        info(Plugin.currency, sender, "You are currently holding `" + pocket + "` in your `"+ Account.pocket.name()+"`, and `" + bank + "` in your bank account");
    }

    public static void errors_number(Plugin plugin, String value, CommandSender sender) {
        danger(plugin, sender, "`" + value + "` is an invalid number");
    }

    public static void plugin(CommandSender sender, String plugin, String enabled) {
        sender.sendMessage(plugin + ": " + enabled);
    }

    public static void essentials_join(CommandSender sender, double pocket, double bank, int max, int current) {
        info(Plugin.currency, sender, "Your current balance at bank: `" + cValue + bank + cInfo + "`; in pocket `" + cValue + pocket + cInfo + "`");
        info(Plugin.homes, sender, "You have set `" + cValue + current + cInfo + "` homes of `" + cValue + max + cInfo + "` available");
    }

    public static void guild_join(CommandSender sender, Guild guild, Role role, double bank, boolean hasHome) {
        info(Plugin.guilds, sender, "You are a `" + cValue + role.name() + cInfo + "` of the guild `" + cGuild + guild.getName() + cInfo + "`");
        String set = (hasHome) ? "been set" : "not been set yet";
        info(Plugin.guilds, sender, "The guild has `" + cValue + bank + cInfo + "` in their account. Home has " + cValue + set);
    }

    public static void currency_invalid_account(CommandSender sender, String arg) {
        danger(Plugin.currency, sender, "Invalid account");
    }

    public static void currency_paid(CommandSender sender, String name, double value) {
        success(Plugin.currency, sender, "Successfully paid `" + cValue + value + cSuccess + "` to `" + cPlayer + name + cSuccess + "`");
    }

    public static void warps_withdraw(CommandSender sender, double cost) {
        success(Plugin.warps, sender, "Successfully withdraw of `" + cValue + cost + cSuccess + "` from your `" + cAccount + "pocket" + cSuccess + "`");
    }

    public static void guilds_created(CommandSender sender, String name) {
        success(Plugin.guilds, sender, "Successfully created a new guild " + cGuild + name);
    }

    public static void guilds_disband_success(CommandSender sender) {
        success(Plugin.guilds, sender, "You have successfully disbanded your guild");
    }

    public static void guilds_role_needed(CommandSender sender, Guild guild, Role role) {
        danger(Plugin.guilds, sender, "You need to contact your guild " + role + " to claim the chunk for you.");
    }

    public static void guilds_claimed_you(CommandSender sender) {
        danger(Plugin.guilds, sender, "This chunk is already claimed to your guild");
    }

    public static void guilds_claimed(Guild claimed, CommandSender sender) {
        danger(Plugin.guilds, sender, "This chunk is already claimed to " + cGuild + claimed.getName());
    }

    public static void guilds_claimed_connected(CommandSender sender) {
        danger(Plugin.guilds, sender, "Claims must be connected");
    }

    public static void guilds_claimed_near(CommandSender sender) {
        danger(Plugin.guilds, sender, "There are guilds too near this chunk to be claimed");
    }

    public static void guilds_claiming(CommandSender sender, Chunk chunk, Guild guild) {
        success(Plugin.guilds, sender, "You have successfully claimed x:" + cValue + chunk.getX() + cSuccess + ",z:" + cValue + chunk.getZ() + cSuccess + " to " + cGuild + guild.getName());
    }

    public static void guilds_claiming_outpost(CommandSender sender, Chunk chunk, Guild guild) {
        success(Plugin.guilds, sender, "You have successfully claimed an outpost x:" + cValue + chunk.getX() + cSuccess + ",z:" + cValue + chunk.getZ() + cSuccess + " to " + cGuild + guild.getName());
    }

    public static void guilds_claiming_max_reached(CommandSender sender) {
        danger(Plugin.guilds, sender, "The maximal number of claims reached, please buy more to extend your area");
    }

    public static void guilds_info_other(CommandSender sender, Guild guild, String master, int count, int claims) {
        info(Plugin.guilds, sender, "Name: " + cGuild + guild.getName());
        info(Plugin.guilds, sender, "Role Master: " + cPlayer + master);
        info(Plugin.guilds, sender, "Members count: " + cValue + count);
        info(Plugin.guilds, sender, "Open to join: " + cValue + guild.isOpen());
        info(Plugin.guilds, sender, "Claims: " + cValue + claims);
        info(Plugin.guilds, sender, "Outposts: " + cValue + guild.getUsedOutposts());
    }

    public static void guilds_info_your(CommandSender sender, Guild guild, String master, String mods, String members, int homes, int claims) {
        info(Plugin.guilds, sender, "Name: " + cGuild + guild.getName());
        info(Plugin.guilds, sender, "Role Master: " + cPlayer + master);
        info(Plugin.guilds, sender, "Role Mods: " + cValue + mods);
        info(Plugin.guilds, sender, "Role Members: " + cValue + members);
        info(Plugin.guilds, sender, "Homes set: " + cValue + homes + cInfo + "/" + cValue + guild.getMaxHomes());
        info(Plugin.guilds, sender, "Open to join: " + cValue + guild.isOpen());
        info(Plugin.guilds, sender, "Invited Only: " + cValue + guild.isInvited_only());
        info(Plugin.guilds, sender, "FriendlyFire: " + cValue + guild.isFriendlyFire());
        info(Plugin.guilds, sender, "Claims: " + cValue + claims + cInfo + "/" + cValue + guild.getMaxClaims());
        info(Plugin.guilds, sender, "Outposts: " + cValue + guild.getUsedOutposts() + cInfo + "/" + cValue + guild.getUsedOutposts());
    }
}

package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Notify;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Response;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.OddPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class MessageManager {
    static ChatColor cValue = ChatColor.GRAY;
    static ChatColor cDanger = Notify.danger.getColor();
    static ChatColor cPlayer = ChatColor.GOLD;
    static ChatColor cPermission = ChatColor.DARK_GRAY;
    static ChatColor cInfo = Notify.info.getColor();
    static ChatColor cSuccess = Notify.success.getColor();
    static ChatColor cGuild = ChatColor.GOLD;
    static ChatColor cWarning = Notify.warning.getColor();

    private static void syntax(Plugin plugin, CommandSender sender, String message) {
        String prefixed = plugin.getString() + Notify.info.getColor() + message;
        sender.sendMessage(prefixed);
    }

    private static void guild_notify(Guild guild, Notify notify, String message) {
        Response response = Response.valueOf(OddJob.getInstance().getConfig().getString(Plugin.guilds.name() + ".response", "CHAT"));
        String prefixed = Plugin.guilds.getString() + notify.getColor() + message;
        for (UUID member : OddJob.getInstance().getGuildsManager().getMembers().keySet()) {
            UUID test = OddJob.getInstance().getGuildsManager().getMembers().get(member);
            if (test == guild.getUuid()) {
                Player player = Bukkit.getPlayer(member);
                if (player != null && player.isOnline()) {
                    if (response == Response.ACTIONBAR) {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(prefixed));
                    } else {
                        player.sendMessage(prefixed);
                    }
                }
            }
        }
    }

    private static void notify(Plugin plugin, CommandSender sender, Notify notify, String message) {
        Response response = Response.valueOf(OddJob.getInstance().getConfig().getString(plugin.name() + ".response", "CHAT"));
        String prefixed = plugin.getString() + notify.getColor() + message;
        if ((response == Response.ACTIONBAR) && (sender instanceof Player player)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(prefixed));
        } else {
            sender.sendMessage(prefixed);
        }
    }

    private static void message(Plugin plugin, CommandSender sender, Notify notify, String message) {
        String prefixed = plugin.getString() + notify.getColor() + message;
        sender.sendMessage(prefixed);
    }

    private static void message(Plugin plugin, CommandSender sender, Notify notify, TextComponent textComponent) {
        TextComponent prefixed = new TextComponent(plugin.getString() + notify.getColor());
        prefixed.addExtra(textComponent);
        sender.spigot().sendMessage(prefixed);
    }

    public static void homes_name_already_exist(String name, CommandSender sender) {
        notify(Plugin.homes, sender, Notify.danger, "A home with the name " + cValue + name + cDanger + " is already defined.");
    }

    public static void errors_too_many_args(Plugin plugin, CommandSender sender) {
        notify(plugin, sender, Notify.danger, "Too many arguments.");
    }

    public static void errors_too_few_args(Plugin plugin, CommandSender sender) {
        notify(plugin, sender, Notify.danger, "Too few arguments.");
    }

    public static void sendSyntax(Plugin plugin, String string, CommandSender sender) {
        syntax(plugin, sender, "Valid subcommands are: " + string);
    }

    public static void errors_find_player(Plugin plugin, String name, CommandSender sender) {
        notify(plugin, sender, Notify.danger, "Player with the name " + cPlayer + name + cDanger + " is nowhere to be found.");
    }

    public static void errors_permission_denied(Plugin plugin, CommandSender sender, String permission) {
        notify(plugin, sender, Notify.danger, "Missing permission: " + cPermission + permission);
    }

    public static void errors_denied_console(Plugin plugin, CommandSender sender) {
        notify(plugin, sender, Notify.danger, "Console is denied");
    }

    public static void errors_denied_op(Plugin plugin, CommandSender sender) {
        notify(plugin, sender, Notify.danger, "Op is denied");
    }

    public static void errors_denied_players(Plugin plugin, CommandSender sender) {
        notify(plugin, sender, Notify.danger, "Players are denied");
    }

    public static void errors_denied_others(Plugin plugin, CommandSender sender) {
        notify(plugin, sender, Notify.danger, "Others are denied");
    }

    public static void errors_something_somewhere(Plugin plugin, CommandSender sender) {
        notify(plugin, sender, Notify.danger, "Something somewhere, went terribly wrong!");
    }

    public static void homes_list(CommandSender sender, List<String> list, Player target, int count, int max) {
        list(Plugin.homes, sender, Notify.info, list, target, count, max);
    }

    public static void list(Plugin plugin, CommandSender sender, Notify notify, List<String> list, Player target, int count, int max) {
        if (target == null) {
            if (list.size() == 0) {
                list(plugin, sender, notify, "There are no " + cValue + plugin.name());
            } else if (list.size() == 1) {
                list(plugin, sender, notify, list.get(0));
            } else {
                list(plugin, sender, notify, "List of " + cValue + plugin.name() + cInfo + " " + cValue + count + cInfo + "/" + cValue + max);
                list(plugin, sender, notify, "-----------------------------------------");
                for (int i = 0; i < list.size(); i++) {
                    list(plugin, sender, notify, cValue + "" + (i + 1) + cInfo + ".) " + cValue + list.get(i) + cInfo);
                }
            }
        } else {
            if (list.size() == 0) {
                list(plugin, sender, notify, cPlayer + target.getDisplayName() + cInfo + " have none " + cValue + plugin.name() + cInfo + " assigned");
            } else if (list.size() == 1) {
                list(plugin, sender, notify, cPlayer + target.getDisplayName() + cInfo + " has only assigned " + cValue + list.get(0) + cInfo);
            } else {
                list(plugin, sender, notify, "List of " + cValue + plugin.name() + cInfo + " by " + cPlayer + target.getDisplayName() + cInfo + " " + cValue + count + cInfo + "/" + cValue + max);
                list(plugin, sender, notify, "-----------------------------------------");
                for (int i = 0; i < list.size(); i++) {
                    list(plugin, sender, notify, cValue + "" + (i + 1) + cInfo + ".) " + cValue + list.get(i) + cInfo);
                }
            }
        }
        list(plugin, sender, notify, "-----------------------------------------");
    }

    public static void list(Plugin plugin, CommandSender sender, Notify notify, String message) {
        String prefixed = plugin.getString() + notify.getColor() + message;
        sender.sendMessage(prefixed);
    }

    public static void homes_set_success(CommandSender sender, String name, int size, int max) {
        notify(Plugin.homes, sender, Notify.success, "Successfully set " + cValue + name + cSuccess + ", you have now " + cValue + size + cSuccess + "/" + cValue + max + cSuccess + " " + Plugin.homes.name());
    }

    public static void errors_no_subcommands(Plugin plugin, CommandSender sender) {
        notify(plugin, sender, Notify.danger, "There is no suitable subcommands for you!");
    }

    public static void errors_currency_expensive(Plugin plugin, CommandSender sender) {
        notify(plugin, sender, Notify.danger, "Too expensive");
    }

    public static void teleports_to_home(CommandSender sender, String name) {
        notify(Plugin.teleports, sender, Notify.success, "Teleporting home to " + cValue + name);
    }

    public static void homes_not_found(CommandSender sender, String name) {
        notify(Plugin.homes, sender, Notify.danger, "No home named " + cValue + name + cDanger + " was found");
    }

    public static void homes_no_name(CommandSender sender, String name) {
        notify(Plugin.homes, sender, Notify.warning, "No name given, using default " + cValue + name);
    }

    public static void homes_del_success(CommandSender sender, String name, int size, int max) {
        notify(Plugin.homes, sender, Notify.success, "Successfully deleted " + cValue + name + cSuccess + ", you have now " + cValue + size + cSuccess + "/" + cValue + max + cSuccess + " " + Plugin.homes.name());
    }

    public static void homes_limit_reached(CommandSender sender, int count, int max) {
        message(Plugin.homes, sender, Notify.danger, "Maximum count of " + cValue + "homes" + cDanger + " reached");
        message(Plugin.homes, sender, Notify.info, "You have added " + cValue + count + cInfo + " of " + cValue + max + cInfo + " allowed");
    }


    public static void homes_name_changed(CommandSender sender, String nameOld, String nameNew) {
        notify(Plugin.homes, sender, Notify.success, "Name of the home " + cValue + nameOld + cSuccess + " changed to " + cValue + nameNew);
    }

    public static void homes_location_changed(CommandSender sender, String name) {
        notify(Plugin.homes, sender, Notify.success, "Location of " + cValue + name + cSuccess + " has been changed");
    }

    public static void guilds_name_already_exists(String name, CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "There is already a guild named " + cGuild + name);
    }

    public static void guilds_already_associated(CommandSender sender, String name) {
        notify(Plugin.guilds, sender, Notify.danger, "You are already associated with " + cGuild + name);
    }

    public static void guilds_list(Plugin plugin, CommandSender sender, List<String> list, int page) {
        Notify notify = Notify.info;
        double size = ConfigManager.isSet("guilds.default.list") ? ConfigManager.getInt("guilds.default.list") : 10;
        int items = list.size();
        double pages = Math.floor(items / size) + 1;

        if (items == 0) {
            list(plugin, sender, notify, "There are " + cValue + "0" + notify.getColor() + " registered guilds");
        } else if (items == 1) {
            list(plugin, sender, notify, "There is " + cValue + "1" + notify.getColor() + " registered guild");
            list(plugin, sender, notify, "-----------------------------------------");
            list(plugin, sender, notify, cValue + "" + (1) + notify.getColor() + ".) " + cValue + list.get(0));
        } else {

            list(plugin, sender, notify, "Listing guilds, page " + cValue + page + notify.getColor() + " of " + cValue + pages);
            list(plugin, sender, notify, "-----------------------------------------");
            for (int i = 0; i < list.size(); i++) {
                list(plugin, sender, notify, cValue + "" + (i + 1) + notify.getColor() + ".) " + cValue + list.get(i));
            }
        }
        list(plugin, sender, notify, "-----------------------------------------");
    }

    public static void guilds_info(CommandSender sender, Guild guild, OddPlayer guildMaster, List<OddPlayer> pending, List<OddPlayer> invites, List<String> members) {
        Notify notify = Notify.info;
        Plugin plugin = Plugin.guilds;
        list(plugin, sender, notify, "Info about: " + cGuild + guild.getName());
        list(plugin, sender, notify, "-----------------------------------------");
        list(plugin, sender, notify, "UUID: " + cValue + guild.getUuid());
        list(plugin, sender, notify, "Name: " + cValue + guild.getName());
        if (guildMaster != null) {
            list(plugin, sender, notify, "GuildMaster: " + cPlayer + guildMaster.getName());
        }
        list(plugin, sender, notify, "Assigned members: " + cValue + members.size());
        list(plugin, sender, notify, "Pending players: " + cValue + pending.size());
        list(plugin, sender, notify, "Invited players: " + cValue + invites.size());
        list(plugin, sender, notify, "Open to join: " + cValue + guild.isOpen());
        list(plugin, sender, notify, "Claims: " + cValue + guild.getClaims() + cInfo + "/" + cValue + guild.getMaxClaims());
        list(plugin, sender, notify, "Homes set: " + cValue + guild.getHomes() + cInfo + "/" + cValue + guild.getMaxHomes());
        list(plugin, sender, notify, "Spawn mobs: " + cValue + guild.isSpawnMobs());
        list(plugin, sender, notify, "Invited only: " + cValue + guild.isInvited_only());
        list(plugin, sender, notify, "Friendly fire: " + cValue + guild.isFriendlyFire());
    }

    public static void guilds_not_found(CommandSender sender, String arg) {
        notify(Plugin.guilds, sender, Notify.danger, "We can't find a guild named " + cValue + arg);
    }

    public static void guilds_claim_already(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "You have already claimed this chunk.");
    }

    public static void guilds_claims_by_else(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "This chunk is claimed by another guild.");
    }

    public static void guilds_claims_claimed(Guild guild, Chunk chunk) {
        guild_notify(guild, Notify.success, "The guild have successfully claimed this chunk: X=" + cValue + chunk.getX() + cSuccess + " Z=" + cValue + chunk.getZ() + cSuccess + ".");
    }

    public static void guilds_need_permission(CommandSender sender, Role role) {
        notify(Plugin.guilds, sender, Notify.danger, "You are not qualified to serve this command! You need to have to " + cValue + role.name() + cDanger + " role");
    }

    public static void guilds_created(CommandSender sender, String name) {
        notify(Plugin.guilds, sender, Notify.success, "You have successfully created a new guild named " + cGuild + name);
    }

    public static void guilds_not_associated(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "You are not associated with any guild");
    }

    public static void guilds_warn_block_placed(CommandSender sender, Guild guild) {
        notify(Plugin.guilds, sender, Notify.warning, "You are placing blocks inside " + cGuild + guild.getName());
    }

    public static void guilds_warn_block_broken(CommandSender sender, Guild guild) {
        notify(Plugin.guilds, sender, Notify.warning, "You are breaking blocks inside " + cGuild + guild.getName());
    }

    public static void guilds_not_allowed(CommandSender sender, Guild guild) {
        notify(Plugin.guilds, sender, Notify.warning, "You are not worthy of this action inside " + cGuild + guild.getName());
    }

    public static void guilds_warn_empty_bucket(CommandSender sender, Guild guild) {
        notify(Plugin.guilds, sender, Notify.warning, "You are emptying a bucket inside " + cGuild + guild.getName());
    }

    public static void death1200(CommandSender sender) {
        message(Plugin.deaths, sender, Notify.warning, "Blocks and tools lost by death will is at own risk, and may disappear over time or be stolen. Lost or stolen things will NOT be refunded! Be careful next time.");
        notify(Plugin.deaths, sender, Notify.warning, "Unretreived items will disappear in 20 minutes");
    }

    public static void death600(CommandSender sender) {
        notify(Plugin.deaths, sender, Notify.warning, "Unretreived items will disappear in 10 minutes");
    }

    public static void death60(CommandSender sender) {
        notify(Plugin.deaths, sender, Notify.warning, "Unretreived items will disappear in 1 minutes");
    }

    public static void death10(int i, CommandSender sender) {
        notify(Plugin.deaths, sender, Notify.warning, "Unretreived items will disappear in " + i + " seconds");
    }

    public static void death0(CommandSender sender) {
        notify(Plugin.deaths, sender, Notify.danger, "You were too late. All the things are lost!");
    }

    public static void spiritFoundOther(CommandSender owner) {
        notify(Plugin.deaths, owner, Notify.danger, "Someone found your loot!");
    }

    public static void spiritFound(CommandSender finder, @Nullable OfflinePlayer owner) {
        if (owner != null)
            notify(Plugin.deaths, finder, Notify.success, "You found the loot from " + cPlayer + owner.getName() + cSuccess + "!");
    }

    public static void spiritFoundSelf(CommandSender finder, boolean looted) {
        if (looted) {
            notify(Plugin.deaths, finder, Notify.success, "Congrats! You found your own loot before anyone else.");
        } else {
            notify(Plugin.deaths, finder, Notify.success, "Ops! You broke it!.");
        }
    }

    public static void guilds_claim_nope(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "Chunk is not claimed by any guild!");
    }

    public static void guilds_set_spawn_success(CommandSender sender, Location location, Guild guild) {
        guild_notify(guild, Notify.success, "New spawn set by " + cPlayer + sender.getName() + cSuccess + " x=" + cValue + location.getBlockX() + cSuccess + " y=" + cValue + location.getBlockY() + cSuccess + " z=" + cValue + location.getBlockZ());
    }

    public static void guilds_set_spawn_error_in_chunk(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "Location must be in a Chunk owned by the guild");
    }

    public static void guilds_error_role(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "Wrong role in the guild");
    }

    public static void guilds_spawn_not_set(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "No guild spawn set.");
    }

    public static void teleports_in_combat(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "Aborted, in combat!");
    }

    public static void teleports_countdown(int i, CommandSender sender) {
        notify(Plugin.teleports, sender, Notify.info, "Teleporting in " + cValue + i);
    }

    public static void teleports_destination_offline(Player requester, Player destination) {
        notify(Plugin.teleports, requester, Notify.danger, "Cancelled, " + cPlayer + destination.getName() + cDanger + " is offline");
    }

    public static void teleports_requester_offline(Player requester, Player destination) {
        notify(Plugin.teleports, destination, Notify.danger, "Cancelled, " + cPlayer + requester.getName() + cDanger + " is offline");
    }

    public static void teleports_timed_out(Player requester, Player destination) {
        notify(Plugin.teleports, requester, Notify.danger, "Cancelled, " + cPlayer + destination.getName() + cDanger + " has not responded.");
        notify(Plugin.teleports, destination, Notify.danger, "Cancelled, request from " + cPlayer + requester.getName() + cDanger + " has timed out");
    }

    public static void teleports_request_denied(Player destination, Player requester) {
        notify(Plugin.teleports, requester, Notify.danger, "Cancelled, " + cPlayer + destination.getName() + cDanger + " has rejected your request");
        notify(Plugin.teleports, destination, Notify.danger, "You have denied the teleport request from " + cPlayer + destination.getName());
    }

    public static void teleports_request_already_sent(Player requester, Player destination) {
        notify(Plugin.teleports, requester, Notify.warning, "Request to " + cPlayer + destination.getName() + cWarning + " has already been sent");
    }

    public static void teleports_request_accepted(Player destination, Player requester) {
        notify(Plugin.teleports, requester, Notify.success, "Your teleport request to " + cPlayer + destination.getName() + cSuccess + " has been accepted");
        notify(Plugin.teleports, destination, Notify.info, "You have accepted " + cPlayer + requester.getName() + cInfo + "'s teleport request");
    }

    public static void teleports_request_no_request(Player destination, Player requester) {
        notify(Plugin.teleports, destination, Notify.warning, "You have no teleport requests from " + cPlayer + requester.getName());
    }

    public static void teleports_request_none(Player destination) {
        notify(Plugin.teleports, destination, Notify.warning, "You have " + cValue + 0 + cWarning + " teleport requests waiting");
    }

    public static void teleports_requests_more(Player destination, List<UUID> requestList) {
        notify(Plugin.teleports, destination, Notify.warning, "You have " + cValue + requestList.size() + cWarning + " teleport requests waiting");
    }

    public static void teleports_request_sent(Player destination, Player requester) {
        notify(Plugin.teleports, requester, Notify.success, "You have sent a teleport request to " + cPlayer + destination.getName());
        notify(Plugin.teleports, destination, Notify.info, "You have recieved a request from " + cPlayer + requester.getName() + cInfo + " to teleport to you");
        TextComponent text = new TextComponent("To answer the request, click here: ");
        TextComponent accept = new TextComponent("[accept]");
        accept.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleport accept " + requester.getName()));
        text.addExtra(accept);
        TextComponent deny = new TextComponent("[deny]");
        deny.setColor(net.md_5.bungee.api.ChatColor.RED);
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleport deny " + requester.getName()));
        text.addExtra(deny);
        message(Plugin.teleports, destination, Notify.info, text);
    }

    public static void teleports_request_changing(Player requester, @Nullable Player old) {
        if (old != null && old.isOnline())
            notify(Plugin.teleports, old, Notify.danger, "Cancelled, " + cPlayer + requester.getName() + cDanger + " has changed direction");
        notify(Plugin.teleports, requester, Notify.danger, "Cancelled, changing direction");
    }

    public static void guilds_invite_sent(CommandSender sender, OddPlayer oddPlayer, Guild guild) {
        Player target = Bukkit.getPlayer(oddPlayer.getUuid());
        notify(Plugin.guilds, sender, Notify.success, "Player " + cPlayer + oddPlayer.getName() + cSuccess + " has been invited to join the guild");
        if (target != null && target.isOnline())
            notify(Plugin.guilds, target, Notify.info, "You have been invited to join " + cGuild + guild.getName());
        guild_notify(guild, Notify.info, cPlayer + oddPlayer.getName() + cInfo + " has been invited to the guild");
    }

    public static void guilds_already_pending_this(CommandSender sender, OddPlayer oddPlayer) {
        notify(Plugin.guilds, sender, Notify.danger, "Player " + cPlayer + oddPlayer.getName() + cDanger + " has already has a pending request to join the guild");
    }

    public static void guilds_already_invited(CommandSender sender, OddPlayer oddPlayer) {
        notify(Plugin.guilds, sender, Notify.warning, "Player " + cPlayer + oddPlayer.getName() + cWarning + " has already been invited to another guild");
    }

    public static void guilds_already_invited_this(CommandSender sender, OddPlayer oddPlayer) {
        notify(Plugin.guilds, sender, Notify.danger, "Player " + cPlayer + oddPlayer.getName() + cDanger + " has already been invited to the guild");
    }

    public static void guilds_pending_removed(UUID uuid) {
        Player target = Bukkit.getPlayer(uuid);
        if (target != null && target.isOnline())
            notify(Plugin.guilds, target, Notify.info, "All of your pending request to join guilds are now removed");
    }

    public static void guilds_invites_removed(UUID uuid) {
        Player target = Bukkit.getPlayer(uuid);
        if (target != null && target.isOnline())
            notify(Plugin.guilds, target, Notify.info, "All of your invites to join guilds are now removed");
    }

    public static void teleports_another_to_you(CommandSender teleportDestination, CommandSender teleportSource) {
        notify(Plugin.guilds, teleportDestination, Notify.info, "You are haunted by " + cPlayer + teleportSource.getName());
    }

    public static void teleports_you_to_another(CommandSender teleportDestination, CommandSender teleportSource) {
        notify(Plugin.guilds, teleportDestination, Notify.info, "All of your pending request to join guilds are now removed");
    }

    public static void teleports_another_to_another(CommandSender teleportDestination, CommandSender teleportSource) {
        notify(Plugin.guilds, teleportDestination, Notify.info, "All of your pending request to join guilds are now removed");
    }

    public static void guilds_claims_too_many(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "You have claimed too many chunks");
    }

    public static void guilds_invited_only(CommandSender sender, Guild guild) {
        notify(Plugin.guilds, sender, Notify.danger, "The " + cGuild + guild.getName() + cDanger + " is set to " + cValue + "invited only");
    }

    public static void guilds_already_pending_join(CommandSender sender, Guild guild) {
        notify(Plugin.guilds, sender, Notify.danger, "You have already requested to join the " + cGuild + guild.getName());
    }

    public static void guilds_already_invited_join(CommandSender sender, Guild guild) {
        notify(Plugin.guilds, sender, Notify.danger, "You were already invited to join the " + cGuild + guild.getName());
    }

    public static void guilds_pending_set(CommandSender sender, Guild guild) {
        notify(Plugin.guilds, sender, Notify.info, "You have now requested to join the " + cGuild + guild.getName());
        guild_notify(guild, Notify.info, cPlayer + sender.getName() + cInfo + " wants to join the guild");
    }

    public static void guilds_no_invites(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "You have no invitation to any guild");
    }

    public static void guilds_no_pending(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "The guild has no pending request to join");
    }

    public static void guilds_more_invites(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "You have more than one guild invites");
    }

    public static void guilds_more_pending(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "There are more than one pending request to join");
    }

    public static void guilds_no_invites_from(CommandSender sender, String name) {
        notify(Plugin.guilds, sender, Notify.danger, "There is no invitation to join " + cValue + name);
    }

    public static void guilds_no_pending_from(CommandSender sender, String name) {
        notify(Plugin.guilds, sender, Notify.danger, "There is no pending request to join from " + cValue + name);
    }

    public static void insufficient_funds_guild(CommandSender sender, double sum, Guild guild) {
        notify(Plugin.currency, sender, Notify.warning, "The " + cGuild + guild.getName() + cWarning + " can't afford " + cValue + sum);
    }

    public static void guilds_bought_homes(CommandSender sender, Guild guild, int count, int maxHomes) {
        guild_notify(guild, Notify.success, "You are ready to create an outpost. " + cValue + count + cSuccess + " of " + cValue + maxHomes);
    }

    public static void guilds_bought_claims(CommandSender sender, Guild guild, int count, int maxClaims) {
        guild_notify(guild, Notify.success, "We have more land to claim; " + cValue + count + cSuccess + "/" + cValue + maxClaims);
    }

    public static void guilds_no_homes_name(CommandSender sender, String name) {
        notify(Plugin.guilds, sender, Notify.danger, "The guild can't find any home named " + cValue + name);
    }

    public static void guilds_home_relocated(CommandSender sender, Guild guild, String name) {
        guild_notify(guild, Notify.success, "Relocation of " + cValue + name + cSuccess + " was successful");
    }

    public static void guilds_home_renamed(CommandSender sender, Guild guild, String oldName, String newName) {
        guild_notify(guild, Notify.success, "Rename of " + cValue + oldName + cSuccess + " was successfully changed to " + cValue + newName);
    }

    public static void guilds_homes_name_already_exist(CommandSender sender, String name) {
        notify(Plugin.homes, sender, Notify.danger, "A home with the name " + cValue + name + cDanger + " is already defined.");
    }

    public static void guilds_set_open_success(Guild guild, boolean open) {
        guild_notify(guild, Notify.success, "Successfully set the guild to open=" + cValue + open);
    }

    public static void guilds_joined_open_guild(Player player, Guild guild) {
        guild_notify(guild, Notify.success, "The guild is open to join; " + cPlayer + player.getName() + cSuccess + " successfully joined.");
    }

    public static void errors_name(Plugin plugin, Player player, String name) {
        notify(plugin, player, Notify.danger, "Invalid name " + cValue + name);
    }

    public static void guilds_set_name_success(Guild guild, String name) {
        guild_notify(guild, Notify.success, "Successfully changed name of the guild to " + cGuild + name);
    }

    public static void guilds_disbanded(Guild guild) {
        guild_notify(guild, Notify.warning, "The guild has been disbanded, you are now free!");
    }

    public static void guilds_disband(CommandSender sender) {
        message(Plugin.guilds, sender, Notify.warning, "You are about to disband the guild. To confirm yor action, retype the command including the name of the guild.");
    }

    public static void guilds_leave_last(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.warning, "The guild can't be empty, since you are the last, you must use " + cValue + "/guilds disband" + cWarning + " to perform this action.");
    }

    public static void guilds_leave_more(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.warning, "There is more people left in the guild, please consider giving the " + cValue + "Master" + cWarning + " role to someone else before leaving");
    }

    public static void guilds_left(CommandSender sender, Guild guild) {
        guild_notify(guild, Notify.warning, "Your guildy " + cPlayer + sender.getName() + cWarning + " left the guild");
    }

    public static void guilds_leave(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.warning, "You left the guild!");
    }

    public static void guilds_notify_pending(CommandSender sender, List<OddPlayer> pending) {
        boolean plural = pending.size() != 1;
        StringBuilder builder = new StringBuilder();
        for (OddPlayer oddPlayer : pending) {
            builder.append(cPlayer).append(oddPlayer.getName()).append(cInfo).append(",");
        }
        if (!builder.isEmpty()) {
            builder.deleteCharAt(builder.lastIndexOf(","));
        }
        message(Plugin.guilds, sender, Notify.info, "Your guild " + (plural ? "have" : "has") + " " + cValue + pending.size() + cInfo + " pending requests to join the guild: " + builder);
    }

    public static void guilds_pending_welcome(OddPlayer oddPlayer, Guild guild) {
        notify(Plugin.guilds, Bukkit.getPlayer(oddPlayer.getUuid()), Notify.success, "Welcome to the " + cGuild + guild.getName() + cSuccess + "!");
    }

    public static void guilds_pending_welcome_guild(OddPlayer oddPlayer, Guild guild) {
        guild_notify(guild, Notify.success, "Please be nice to " + cPlayer + oddPlayer.getName() + cSuccess + " as `hen` is the new meat in the guild!");
    }


    public static void guilds_declined_pending(Guild guild, OddPlayer oddPlayer) {
        guild_notify(guild, Notify.warning, "Pending request to join the guild from " + cPlayer + oddPlayer.getName() + cWarning + " was " + cValue + "declined" + cWarning + "!");
        notify(Plugin.guilds, Bukkit.getPlayer(oddPlayer.getUuid()), Notify.danger, "Your request to join " + cGuild + guild.getName() + cDanger + " was " + cValue + "declined" + cDanger + "!");
    }

    public static void guilds_invite_declined(CommandSender sender, OddPlayer oddPlayer) {
    }

    public static void guilds_max_homes_reached(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "Sky is the limit, and you reached it.");
    }

    public static void guilds_homes_inside(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "Must be inside your head.");
    }

    public static void invalidNumber(Plugin plugin, CommandSender sender, String format) {
        notify(plugin, sender, Notify.danger, "Invalid number format: " + cValue + format);
    }

    public static void warps_error_password(CommandSender sender, String name) {
        notify(Plugin.warps, sender, Notify.danger, "Invalid password for warp: " + cValue + name);
    }

    public static void warps_added(CommandSender sender, String name) {
        notify(Plugin.warps, sender, Notify.success, "Warp added: " + cValue + name);
    }

    public static void warps_exists(CommandSender sender, String name) {
        notify(Plugin.warps, sender, Notify.danger, "Warp " + cValue + name + cDanger + " already exists");
    }

    public static void warps_not_exists(CommandSender sender, String name) {
        notify(Plugin.warps, sender, Notify.danger, "Warp " + cValue + name + cDanger + " does not exists");
    }

    public static void warps_deleted(CommandSender sender, String name) {
        notify(Plugin.warps, sender, Notify.warning, "Warp " + cValue + name + cDanger + " deleted");
    }

    public static void warps_list(CommandSender sender, List<String> warps) {
        list(Plugin.warps, sender, Notify.info, warps, sender instanceof Player player ? player : null, warps.size(), warps.size());
        list(Plugin.warps, sender, Notify.info, ChatColor.RED + "*" + ChatColor.RESET + "=Password protected; "
                + ChatColor.BLUE + "$" + ChatColor.RESET + "=Not free");
    }

    public static void guilds_claims_nearby(CommandSender sender, String near) {
        notify(Plugin.guilds, sender, Notify.warning, "To near another guild " + cGuild + near);
    }

    public static void guilds_claims_connected(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "Claims must be connected.");
    }

    public static void currency_account_not_found(CommandSender sender, String name) {
        notify(Plugin.currency, sender, Notify.danger, "Can't find account " + cValue + name);
    }

    public static void currency_added(CommandSender sender, String uuid, String account, double value) {
        notify(Plugin.currency, sender, Notify.success, "We added some chips to " + uuid + " account " + account + ":  " + value);
    }

    public static void currency_auto(CommandSender sender, double value) {
        message(Plugin.currency, sender, Notify.info, "You have been mining a lot lately, and earned " + cValue + value + cInfo + " chips");
    }

    public static void guilds_world_disallowed(CommandSender sender, String name) {
        notify(Plugin.guilds, sender, Notify.danger, "You can't claim land in " + cValue + name);
    }

    public static void errors_guild_type(CommandSender sender, String type) {
        notify(Plugin.guilds, sender, Notify.danger, "Can't find guild type: " + cValue + type);
    }

    public static void guilds_errors_set_area(CommandSender sender) {
        notify(Plugin.guilds, sender, Notify.danger, "Error while setting");
    }

    public static void locks_only_world(CommandSender sender) {
        notify(Plugin.locks, sender, Notify.danger, "You are only able to lock blocks a " + cValue + "world");
    }

    public static void locks_inside_another_guild(CommandSender sender, Guild guildChunk) {
        notify(Plugin.locks, sender, Notify.danger, "This block is inside a chunk owned by " + cGuild + guildChunk.getName());
    }

    public static void locks_already_locked_you(CommandSender sender) {
        notify(Plugin.locks, sender, Notify.warning, "You have already locked this block.");
    }

    public static void locks_already_locked_someone(CommandSender sender) {
        notify(Plugin.locks, sender, Notify.danger, "Block is locked by someone else.");
    }

    public static void locks_now_locked(CommandSender sender) {
        notify(Plugin.locks, sender, Notify.success, "Block is now locked!");
    }

    public static void locks_broken(CommandSender sender) {
        notify(Plugin.locks, sender, Notify.warning, "Lock broken!");
    }

    public static void locks_owned(CommandSender sender) {
        notify(Plugin.locks, sender, Notify.danger, "This block is owned by someone else!");
    }

    public static void locks_owned_by(CommandSender sender, OddPlayer oddPlayer) {
        notify(Plugin.locks, sender, Notify.info, "Lock is owned by " + cPlayer + oddPlayer.getName());
    }

    public static void shops_sell(CommandSender sender, Material material, int count, double amount) {
        notify(Plugin.shops, sender, Notify.success, "You have sold " + cValue + count + cSuccess + "x " + cValue + material.name() + cSuccess + " for " + cValue + amount);
    }

    public static void currency_show_player(CommandSender sender, double pocket, double bank) {
        message(Plugin.currency, sender, Notify.info, "You have " + cValue + pocket + cInfo + " in your " + cValue + "pocket" + cInfo + " and " + cValue + bank + cInfo + " in your " + cValue + "bank" + cInfo + " account");
    }

    public static void currency_show_guild(CommandSender sender, Guild guild, double bank) {
        message(Plugin.currency, sender, Notify.info, "Your guild " + cGuild + guild.getName() + cInfo + " has " + cValue + bank + cInfo + " in the " + cValue + "bank" + cInfo + " account");
    }

    public static void insufficient_funds(CommandSender sender, double sum) {
        notify(Plugin.currency, sender, Notify.warning, "You can't afford " + cValue + sum);
    }

    public static void homes_bought(CommandSender sender, int maxHomes, double sum) {
        notify(Plugin.homes, sender, Notify.success, "You have successfully bought one more " + cValue + "home" + cSuccess + " slot, you have now a total of " + cValue + maxHomes + cSuccess + ", paid " + cValue + sum);
    }

    public static void auctions_not_area(CommandSender sender) {
    }

    public static void guilds_no_stealing(CommandSender sender) {
        notify(Plugin.guilds,sender,Notify.danger,"You can't steal this book!");
    }

    public static void shops_inside(CommandSender sender) {
        notify(Plugin.shops,sender,Notify.warning,"You have to be inside a shopping area.");
    }

    public static void fortress(CommandSender sender) {
        notify(Plugin.guilds,sender,Notify.danger,ChatColor.BOLD+""+"Area 51 is closed.");
    }

    public static void errors_chunk_is_owned(CommandSender sender, String name) {
        notify(Plugin.homes,sender,Notify.danger,ChatColor.BOLD+""+"Chunk is claimed by "+cGuild+name);
    }

    public static void currency_subbed(CommandSender sender, String toString, String name, double value) {
    }

    public static void locks_nope(CommandSender sender) {
        notify(Plugin.locks,sender,Notify.warning,"Object is not locked");
    }
}

package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.Warp;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
    private static final ChatColor cItem = ChatColor.BLUE;

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
        sender.sendMessage(cInfo + title);
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
            if (sender.isOp()) {
                name += ChatColor.RESET+" "+warp.getUUID();
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
        info(Plugin.guilds, player, "You have already sent a request to join " + cGuild + guild.getName());
    }

    public static void guilds_invited_only(Player player, String name) {
        info(Plugin.guilds, player, "The guild " + name + " is set to invited only");
    }

    public static void guilds_welcome(Player player, Guild guild) {
        success(Plugin.guilds, player, "Welcome to the guild " + guild.getName());
    }

    public static void guilds_invite_already_sent(OddPlayer target, Guild guild) {
        Player player = Bukkit.getPlayer(target.getUuid());
        if (player != null) {
            info(Plugin.guilds, player, "You have already sent a request to join " + guild.getName());
        }
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
        info(Plugin.currency, sender, "You are currently holding " + cValue+pocket+cInfo + " in your " + cAccount+Account.pocket.name() +cInfo+ ", and " +cValue+ bank +cInfo+ " in your "+cAccount+Account.bank.name());
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
        info(Plugin.guilds, sender, "This chunk is already claimed to your guild");
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

    public static void guilds_unClaiming(CommandSender sender, Chunk chunk, Guild guild) {
        success(Plugin.guilds, sender, "You have successfully unClaimed x:" + cValue + chunk.getX() + cSuccess + ",z:" + cValue + chunk.getZ() + cSuccess + " to " + cGuild + guild.getName());
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

    public static void guilds_invited_to(OddPlayer target, Guild guild) {
        Player player = Bukkit.getPlayer(target.getUuid());
        if (player == null) return;
        success(Plugin.guilds, player, "You have been invited to join the guild " + cGuild + guild.getName());
    }

    public static void guilds_invited_to_guild(OddPlayer target, Guild guild) {
        for (UUID uuid : guild.getMembers(guild.getUuid())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                success(Plugin.guilds, player, cPlayer + target.getName() + cSuccess + " has been invited to the guild");
            }
        }
    }

    public static void guilds_pending_invites(Player player, List<UUID> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (UUID uuid : list) {
            Guild guild = OddJob.getInstance().getGuildsManager().getGuild(uuid);
            if (guild != null) {
                stringBuilder.append(guild.getName()).append(", ");
            }
        }
        info(Plugin.guilds, player, "You are wanted by guilds: " + cGuild + stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(", ")));
    }

    public static void guilds_invitation_not_found(CommandSender sender) {
        danger(Plugin.guilds, sender, "No invitation found");
    }

    public static void guilds_invited_welcome(Guild guild, Player target) {
        for (UUID uuid : guild.getMembers(guild.getUuid())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                success(Plugin.guilds, player, cPlayer + target.getName() + cSuccess + " has accepted the guild invitation");
            }
        }
    }

    public static void guilds_left(Player player, Guild guild) {
        success(Plugin.guilds, player, "You have successfully left the guild " + cGuild + guild.getName());
        for (UUID uuid : guild.getMembers(guild.getUuid())) {
            Player member = Bukkit.getPlayer(uuid);
            if (member != null) {
                success(Plugin.guilds, member, cPlayer + player.getName() + cSuccess + " has left the guild");
            }
        }
    }

    public static void guilds_request(Player player, Guild guild) {
        MessageManager.info(Plugin.guilds, player, "Request to join the guild " + cGuild + guild.getName() + cInfo + " is sent");
        for (UUID uuid : guild.getMembers(guild.getUuid())) {
            Player member = Bukkit.getPlayer(uuid);
            if (member != null) {
                info(Plugin.guilds, member, cPlayer + player.getName() + cInfo + " wants to join your guild");
            }
        }
    }

    public static void guilds_pending_requests(Player player, List<UUID> pending) {
        StringBuilder stringBuilder = new StringBuilder();
        for (UUID uuid : pending) {
            Guild guild = OddJob.getInstance().getGuildsManager().getGuild(uuid);
            if (guild != null) {
                stringBuilder.append(guild.getName()).append(" ");
            }
        }
        info(Plugin.guilds, player, "You still want to join the guilds: " + cGuild + stringBuilder);
    }

    public static void guilds_pending(Player player, List<UUID> pending) {
        StringBuilder stringBuilder = new StringBuilder();
        for (UUID uuid : pending) {
            stringBuilder.append(OddJob.getInstance().getPlayerManager().get(uuid).getName()).append(", ");
        }
        info(Plugin.guilds, player, "" + cPlayer + stringBuilder + cInfo + " want to join your guild");
    }

    public static void guilds_pending_not_found(CommandSender sender) {
        danger(Plugin.guilds, sender, "No requests found");
    }

    public static void guilds_pending_welcome(Guild guild, OddPlayer target) {
        for (UUID uuid : guild.getMembers(guild.getUuid())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (player.getUniqueId().equals(target.getUuid())) {
                    success(Plugin.guilds, player, "You have been accepted into the guild " + cGuild + guild.getName());
                } else
                    success(Plugin.guilds, player, cPlayer + target.getName() + cSuccess + " has been accepted into the guild");
            }
        }
    }

    public static void guilds_invited_denied(Player target, Guild guild) {
        for (UUID uuid : guild.getMembers(guild.getUuid())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                info(Plugin.guilds, player, cPlayer + target.getName() + cInfo + " declined the invitation to join the guild");
            }
        }
        danger(Plugin.guilds, target, "You have declined the invitation to join the guild " + cGuild + guild.getName());

    }

    public static void guilds_pending_denied(OddPlayer target, Guild guild) {
        for (UUID uuid : guild.getMembers(guild.getUuid())) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                info(Plugin.guilds, player, "The request from " + cPlayer + target.getName() + cInfo + " to join the guild has been declined");
            }
        }
        Player player = Bukkit.getPlayer(target.getUuid());
        if (player != null)
            danger(Plugin.guilds, player, "Your request to join the guild " + cGuild + guild.getName() + cDanger + " has been declined");
    }

    public static void guilds_homes_info(CommandSender sender, Guild guild, List<String> homes) {
        info(Plugin.guilds, sender, "The guild have used " + cValue + homes.size() + cInfo + " of a max " + cValue + guild.getMaxHomes());
        info(Plugin.guilds, sender, "To by more use the command `" + cValue + "/guilds buy homes" + cInfo + "`");
    }

    public static void guilds_to_buy(CommandSender sender, Guild guild) {
        double claim = Plu.GUILDS_CLAIMS.getValue() * (guild.getBoughtClaims() + 1 * Plu.GUILDS_CLAIMS.getMultiplier());
        double home = Plu.GUILDS_HOMES.getValue() * (guild.getBoughtHomes() + 1 * Plu.GUILDS_HOMES.getMultiplier());
        double outpost = Plu.GUILDS_OUTPOST.getValue() * (guild.getBoughtOutposts() + 1 * Plu.GUILDS_OUTPOST.getMultiplier());
        info(Plugin.guilds, sender, "You can buy `" + cValue + "claims" + cInfo + "`, next will cost " + cValue + claim);
        info(Plugin.guilds, sender, "You can buy `" + cValue + "homes" + cInfo + "`, next will cost " + cValue + home);
        info(Plugin.guilds, sender, "You can buy `" + cValue + "outpost" + cInfo + "`, next will cost " + cValue + outpost);
    }

    public static void essentials_feed_self(CommandSender sender) {
        success(Plugin.essentials, sender, "You have feed yourself");
    }

    public static void essentials_feed(CommandSender sender) {
        info(Plugin.essentials, sender, "You have been feed");
    }

    public static void essentials_feed_all(CommandSender sender, List<String> players) {
        essentials_feed_many(sender, players);
    }

    public static void essentials_feed_many(CommandSender sender, List<String> players) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String name : players) {
            stringBuilder.append(cPlayer).append(name).append(cSuccess).append(", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(", "));
        success(Plugin.essentials, sender, "You have feed: " + stringBuilder);
    }

    public static void essentials_feed_one(CommandSender sender, String name) {
        success(Plugin.essentials, sender, "You have feed: " + cPlayer + name);
    }

    public static void essentials_heal_self(CommandSender sender) {
        success(Plugin.essentials, sender, "You have healed yourself");
    }

    public static void essentials_healed(CommandSender sender) {
        info(Plugin.essentials, sender, "You have been feed");
    }

    public static void essentials_heal_all(CommandSender sender, List<String> players) {
        essentials_heal_many(sender, players);
    }

    public static void essentials_heal_many(CommandSender sender, List<String> players) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String name : players) {
            stringBuilder.append(cPlayer).append(name).append(cSuccess).append(", ");
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(", "));
        success(Plugin.essentials, sender, "You have healed: " + stringBuilder);
    }

    public static void essentials_heal_one(CommandSender sender, String name) {
        success(Plugin.essentials, sender, "You have healed: " + cPlayer + name);
    }

    public static void guilds_buy_homes(CommandSender sender, double price, boolean has) {
        info(Plugin.guilds, sender, "You are about to use " + cValue + price + cInfo + " to increase the amount of available homes for your guild.");
        if (has) {
            success(Plugin.guilds, sender, "Your guild has enough funding, please use `" + cValue + "/guilds buy homes confirm" + cSuccess + "` to purchase");
        } else {
            danger(Plugin.guilds, sender, "Your guild has not enough funding, please transfer to guild account: `" + cValue + "/transfer <bank|pocket> guild <amount>" + cDanger + "`");
        }
    }

    public static void guilds_homes_bought(CommandSender sender, double price, int maxHomes) {
        success(Plugin.guilds, sender, "You have successfully increased the maximal number of homes for the guild to " + cValue + maxHomes + cSuccess + ", next purchase will cost you " + cValue + price);
        info(Plugin.guilds, sender, "To use your new home, use the command `" + cValue + "/guilds homes add <name>" + cInfo + "`");
    }

    public static void guilds_homes_deleted(CommandSender sender, String home) {
        success(Plugin.guilds, sender, "Home `" + cValue + home + cSuccess + "` deleted from the guild");
    }

    public static void guilds_homes_relocated(CommandSender sender, String home) {
        success(Plugin.guilds, sender, "Home `" + cValue + home + cSuccess + "` for the guild changed");
    }

    public static void guilds_homes_must_be(CommandSender sender) {
        danger(Plugin.guilds, sender, "Home must be set within your guild claims");
    }

    public static void guilds_homes_not_exists(CommandSender sender, String oldName) {
        danger(Plugin.guilds, sender, "The guild has no home named `" + cValue + oldName + cDanger + "`");
    }

    public static void guilds_homes_renamed(CommandSender sender, String oldName, String newName) {
        success(Plugin.guilds, sender, "Home `" + cValue + oldName + cSuccess + "` changed name to `" + cValue + newName + cSuccess + "`");
    }

    public static void teleport(CommandSender sender, int i) {
        info(Plugin.teleports, sender, "Teleporting in " + cValue + i);
    }

    public static void teleport(CommandSender sender) {
        info(Plugin.teleports, sender, "Teleporting " + cValue + "now");
    }

    public static void teleport_request_sent(Player requester, Player targetPlayer) {
        info(Plugin.teleports, targetPlayer, "Player " + targetPlayer.getName() + " requesting to be teleported to your position.");

        TextComponent accept = new TextComponent(ChatColor.GREEN + "ACCEPT");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleports accept " + requester.getUniqueId()));

        TextComponent deny = new TextComponent(ChatColor.RED + "DENY");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/teleports deny " + requester.getUniqueId()));

        accept.addExtra(" or ");
        accept.addExtra(deny);
        targetPlayer.spigot().sendMessage(ChatMessageType.CHAT, accept);

        info(Plugin.teleports, requester, "Request sent to " + cPlayer + targetPlayer.getName());
    }

    public static void teleport_request_accepted(Player target, Player requester) {
        success(Plugin.teleports, target, "You have accepted the teleport request from " + cPlayer + requester.getName() + cSuccess + ", and will be here shortly");
        success(Plugin.teleports, requester, cPlayer + target.getName() + cSuccess + " has accepted your teleport request");
    }

    public static void teleport_request_denied(Player target, Player requester) {
        success(Plugin.teleports, target, "You have denied the teleport request from " + cPlayer + requester.getName());
        danger(Plugin.teleports, requester, cPlayer + target.getName() + cDanger + " has denied your teleport request");
    }

    public static void shops_price_sell(CommandSender sender, ItemStack item, double normal, int amount, double price, double temp) {
        info(Plugin.shops, sender, "One of " + cItem + item.getType().name() + cInfo + " can be sold for " + cValue + normal + cInfo + ", the whole stack of " + cValue + amount + cInfo + " can be sold for " + cValue + price + cInfo + ", next item can be sold for " + cValue + temp);
    }

    public static void shops_price_buy(CommandSender sender, ItemStack item, double normal, int amount, double price, double temp) {
        info(Plugin.shops, sender, "One of " + cItem + item.getType().name() + cInfo + " can be bought for " + cValue + normal + cInfo + ", the whole stack of " + cValue + amount + cInfo + " can be bought for " + cValue + price + cInfo + ", next item can be bought for " + cValue + temp);
    }

    public static void shops_not_sellable(CommandSender sender, ItemStack item) {
        danger(Plugin.shops, sender, cInfo + item.getType().name() + cDanger + " is not sellable");
    }

    public static void shops_sold_info(CommandSender sender, @NotNull ItemStack item, double normal, int amount, double price, double temp) {
        success(Plugin.shops, sender, "You have sold " + cValue + amount + cSuccess + " " + cItem + item.getType().name() + cSuccess + " for " + cValue + price + cSuccess + ", next item is sold for " + cValue + temp);
    }

    public static void shops_bought_info(CommandSender sender, @NotNull ItemStack item, double normal, int amount, double price, double temp) {
        success(Plugin.shops, sender, "You have bought " + cValue + amount + cSuccess + " " + cItem + item.getType().name() + cSuccess + " for " + cValue + price + cSuccess + ", next item is sold for " + cValue + temp);
    }

    public static void guilds_owned(CommandSender sender, String name) {
        danger(Plugin.guilds, sender, "This chunk is owned by " + cGuild + name);
    }

    public static void shops_material_not_found(CommandSender sender, String material) {
        danger(Plugin.shops, sender, "Item " + cItem + material + cDanger + " not found, check your spelling");
    }

    public static void death_timer(UUID owner, int i) {
        Player player = Bukkit.getPlayer(owner);
        String string = "";
        switch (i) {
            case 1200 -> {
                string = "20 min";
            }
            case 600 -> {
                string = "10 min";
            }
            case 300 -> {
                string = "5 min";
            }
            case 60 -> {
                string = "1 min";
            }
            default -> {
                string = i + " sec";
            }
        }
        if (player != null) {
            if (i == 1200) info(Plugin.deaths, player, "Your spirit is leaving this world in " + cValue + string);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(cInfo + "Your spirit is leaving this world in " + cValue + string));
        }
    }

    public static void death_ooops(@NotNull Player player, UUID owner) {
        if (player.getUniqueId().equals(owner)) {
            danger(Plugin.deaths, player, "You broke your f***ing spirit");
        } else {
            OddPlayer target = OddJob.getInstance().getPlayerManager().get(owner);
            if (target != null) {
                Player targetPlayer = Bukkit.getPlayer(target.getUuid());
                if (targetPlayer != null) {
                    danger(Plugin.deaths, targetPlayer, "Some motherf***er brok your spirit, you are doomed");
                }
                info(Plugin.deaths, player, "You broke the spirit of " + cPlayer + target.getName() + cInfo + ", may them rest in peace");
            }
        }
    }

    public static void death_lucky(Player player) {
        success(Plugin.deaths,player,"Lucky bastard! Next time, don't die.");
    }

    public static void death_bastard(Player player, Player owner) {
        if (owner != null) {
            danger(Plugin.deaths,owner,"This time you were a complete bastard.");
            success(Plugin.deaths,player,"Now you got this fu**er!");
        }
    }

    public static void homes_inside(CommandSender sender, String name) {
        danger(Plugin.homes,sender,"Home "+cValue+name+cDanger+" is inside a chunk owned by a guild");
    }

    public static void shops_trade_cancelled(CommandSender sender, @NotNull OddPlayer target) {
        info(Plugin.shops,sender,"Trade with "+cPlayer+target.getName()+" cancelled");
    }

    public static void shops_trade_changed(CommandSender sender, @NotNull Player player, @NotNull OddPlayer target) {
        info(Plugin.shops,sender,"Trade request changed from "+cPlayer+target.getName()+cInfo+" to "+cPlayer+player.getName());
    }

    public static void shops_trade_aborted(CommandSender sender, @NotNull OddPlayer target) {
        Player player = Bukkit.getPlayer(target.getUuid());
        if (player != null) {
            info(Plugin.shops,player,"Trade request from "+cPlayer+sender.getName()+ cInfo+" has been aborted");
        }
    }

    public static void shops_trade_created(CommandSender sender, Player player) {
        Player trader = (Player) sender;
        info(Plugin.shops,sender,"Trade request sent to "+cPlayer+player.getName());

        TextComponent accept = new TextComponent(ChatColor.GREEN+"Accept");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/trade accept "+trader.getUniqueId()));

        TextComponent deny = new TextComponent(ChatColor.RED+"Deny");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/trade deny "+trader.getUniqueId()));

        accept.addExtra(" or ");
        accept.addExtra(deny);
        info(Plugin.shops,player,"Trade request from "+cPlayer+trader.getName());
        player.spigot().sendMessage(accept);
    }

    public static void currency_added(CommandSender sender, @NotNull OddPlayer oddPlayer, @NotNull Account account, double value) {
        success(Plugin.currency,sender,"Successfully add "+cValue+value+cSuccess+" to "+cPlayer+oddPlayer.getName()+cSuccess+"s "+cValue+account.name());
    }

    public static void currency_subbed(CommandSender sender, @NotNull OddPlayer oddPlayer, @NotNull Account account, double value) {
        success(Plugin.currency,sender,"Successfully subtracted "+cValue+value+cSuccess+" from "+cPlayer+oddPlayer.getName()+cSuccess+"s "+cValue+account.name());
    }

    public static void currency_payday(OddPlayer oddPlayer, double value) {
        Player player = Bukkit.getPlayer(oddPlayer.getUuid());
        if (player != null) {
            info(Plugin.currency,player,"Payday is here! You've got "+cValue+value+cInfo+" to your "+cAccount+Account.bank.name()+cInfo+" account");
        }
    }

    public static void currency_added(CommandSender sender, Account account, double value) {
        success(Plugin.currency,sender,"Successfully add "+cValue+value+cSuccess+" to your "+cValue+account.name());
    }

    public static void currency_subbed(CommandSender sender, Account account, double value) {
        success(Plugin.currency,sender,"Successfully subtracted "+cValue+value+cSuccess+" from your "+cValue+account.name());
    }
}

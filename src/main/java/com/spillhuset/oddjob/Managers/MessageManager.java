package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Notify;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Response;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageManager {
    static ChatColor cValue = ChatColor.GRAY;
    static ChatColor cDanger = Notify.danger.getColor();
    static ChatColor cPlayer = ChatColor.GOLD;
    static ChatColor cPermission = ChatColor.DARK_GRAY;
    static ChatColor cInfo = Notify.info.getColor();
    static ChatColor cSuccess = Notify.success.getColor();
    static ChatColor cGuild = ChatColor.GOLD;

    private static void syntax(Plugin plugin, CommandSender sender, Notify notify, String message) {
        String prefixed = plugin.getString() + notify.getColor() + message;
        sender.sendMessage(prefixed);
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
        syntax(plugin, sender, Notify.info, "Valid subcommands are: " + string);
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
        list(Plugin.homes, sender, Notify.info, list, target,count,max);
    }

    public static void list(Plugin plugin, CommandSender sender, Notify notify, List<String> list, Player target, int count, int max) {
        if (list.size() == 0) {
            list(plugin, sender, notify, cPlayer + target.getDisplayName() + cInfo + " have none " + cValue + plugin.name() + cInfo + " assigned");
        } else if (list.size() == 1) {
            list(plugin, sender, notify, cPlayer + target.getDisplayName() + cInfo + " has only assigned " + cValue + list.get(0) + cInfo);
        } else {
            list(plugin, sender, notify, "List of " + cValue + plugin.name() + cInfo + " by " + cPlayer + target.getDisplayName() + cInfo+" "+cValue+count+cInfo+"/"+cValue+max);
            list(plugin, sender, notify, "-----------------------------------------");
            for (int i = 0; i < list.size(); i++) {
                list(plugin, sender, notify, cValue + "" + (i + 1) + cInfo + ".) " + cValue + list.get(i) + cInfo);
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
        notify(Plugin.teleport, sender, Notify.success, "Teleporting home to " + cValue + name);
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
        notify(Plugin.homes,sender,Notify.success,"Name of the home "+cValue+nameOld+cSuccess+" changed to "+cValue+nameNew);
    }

    public static void homes_location_changed(CommandSender sender, String name, Location location, OddPlayer target) {
        notify(Plugin.homes,sender,Notify.success,"Location of "+cValue+name+cSuccess+" has been changed");
    }

    public static void guilds_name_already_exists(String name, CommandSender sender) {
        notify(Plugin.guilds,sender,Notify.danger,"There is already a guild named "+cGuild+name);
    }
}

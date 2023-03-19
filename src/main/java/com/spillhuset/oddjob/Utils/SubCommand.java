package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.ConfigManager;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {
    public List<SubCommand> subCommands = new ArrayList<>();

    public abstract boolean denyConsole();

    public abstract boolean denyOp();

    public abstract Plugin getPlugin();

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract String getPermission();

    public abstract int minArgs();

    public abstract int maxArgs();

    public abstract int depth();

    public abstract boolean noGuild();

    public abstract boolean needGuild();

    public abstract Role guildRole();

    public abstract void getCommandExecutor(CommandSender sender, String[] args);

    public abstract List<String> getTabCompleter(CommandSender sender, String[] args);

    public Role getRole(CommandSender sender) {
        if (sender instanceof Player player) {
            Role role = OddJob.getInstance().getGuildsManager().getRoles().get(player.getUniqueId());
            if (role == null) return Role.Guest;
            return OddJob.getInstance().getGuildsManager().getRoles().get(player.getUniqueId());
        }
        return Role.Guest;
    }

    public boolean can(CommandSender sender, boolean others, boolean response) {
        if (denyConsole() && !(sender instanceof Player)) {
            if (response) MessageManager.errors_denied_console(getPlugin(), sender);
            return false;
        }
        if (denyOp() && sender.isOp()) {
            if (response) MessageManager.errors_denied_op(getPlugin(), sender);
            return false;
        }
        if (sender instanceof Player && others && !sender.hasPermission(getPermission() + ".others")) {
            if (response) MessageManager.errors_denied_others(getPlugin(), sender);
            return false;
        }
        if (sender instanceof Player && (!sender.hasPermission(getPermission()) && !sender.isOp())) {
            if (response) MessageManager.errors_denied_players(getPlugin(), sender);
            return false;
        }
        return true;
    }

    /**
     * Check the number of arguments, if it is within the range, returns true.
     *
     * @param sender     CommandSender
     * @param argsLength Integer; length of args
     * @return Boolean
     */
    public boolean argsLength(CommandSender sender, int argsLength) {
        if (maxArgs() != 0 && argsLength > maxArgs()) {
            MessageManager.errors_too_many_args(getPlugin(), sender);
            return false;
        }
        if (argsLength < minArgs()) {
            MessageManager.errors_too_few_args(getPlugin(), sender);
            return false;
        }
        return true;
    }

    public void finder(CommandSender sender, String[] args) {
        OddJob.getInstance().log("args: "+args.length +" depth: "+depth());
        if (args.length >= depth()) {
            OddJob.getInstance().log("subs: "+subCommands.size());
            for (SubCommand subCommand : subCommands) {
                OddJob.getInstance().log("sub: "+subCommand.getName()+" arg: "+args[depth()]);
                if (args.length >= depth() && subCommand.getName().equalsIgnoreCase(args[depth()])) {
                    OddJob.getInstance().log("hit");
                    if (subCommand.can(sender, false, true)) {
                        OddJob.getInstance().log("perm");
                        subCommand.getCommandExecutor(sender, args);
                        return;
                    }
                    OddJob.getInstance().log("!perm");
                }
            }
        }
        MessageManager.sendSyntax(getPlugin(), list(sender), sender);
    }

    public String list(CommandSender sender) {
        StringBuilder stringBuilder = new StringBuilder();
        for (SubCommand subCommand : subCommands) {
            if (can(sender, false, false)) {
                stringBuilder.append(subCommand.getName()).append(",");
            }
        }
        if (!stringBuilder.isEmpty()) stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        return stringBuilder.toString();
    }

    public boolean hasGuild(CommandSender sender, boolean response) {
        if (sender instanceof Player player) {
            Guild guild = (ConfigManager.getBoolean("plugin.guilds")) ? OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId()) : null;
            if (needGuild() && guild == null) {
                if (response) MessageManager.guilds_not_associated(sender);
                return false;
            } else if (noGuild() && guild != null) {
                if (response) MessageManager.guilds_already_associated(sender, guild.getName());
                return false;
            } else {
                return true;
            }
        }
        if (response) MessageManager.guilds_not_associated(sender);
        return false;
    }

}

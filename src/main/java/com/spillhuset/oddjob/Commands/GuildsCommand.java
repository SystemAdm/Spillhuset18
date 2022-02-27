package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuildsCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public GuildsCommand() {
        subCommands.add(new GuildsBuyCommand());
        subCommands.add(new GuildsCreateCommand());
        subCommands.add(new GuildsInfoCommand());
        subCommands.add(new GuildsListCommand());
        subCommands.add(new GuildsClaimCommand());
        subCommands.add(new GuildsUnclaimCommand());
        subCommands.add(new GuildsSetCommand());
        subCommands.add(new GuildsHomesCommand());
        subCommands.add(new GuildsInviteCommand());
        subCommands.add(new GuildsJoinCommand());
        subCommands.add(new GuildsLeaveCommand());
        subCommands.add(new GuildsAcceptCommand());
        subCommands.add(new GuildsDenyCommand());
        subCommands.add(new GuildsDisbandCommand());
    }

    @Override
    public boolean denyConsole() {
        return false;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public boolean canOthers() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.guilds;
    }

    @Override
    public String getPermission() {
        return "guilds";
    }

    @Override
    public int minArgs() {
        return 0;
    }

    @Override
    public int maxArgs() {
        return 0;
    }

    @Override
    public int depth() {
        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("add")) {
            Player w = (Player) sender;
            List<String> worlds = OddJob.getInstance().getConfig().getStringList("homes.world");
            worlds.add(w.getLocation().getWorld().getUID().toString());
            OddJob.getInstance().getConfig().set("homes.world", worlds);
            OddJob.getInstance().getConfig().set("guilds.world", worlds);
            OddJob.getInstance().saveConfig();
        }

        if (!can(sender, false, true)) {
            return true;
        }

        if (!argsLength(sender, args.length)) {
            return true;
        }

        if (sender instanceof Player player && args.length == depth()) {
            OddJob.getInstance().getGuildsManager().info(player);
            return true;
        }

        if (args.length > 0) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getName().equalsIgnoreCase(args[0])) {
                    if (subCommand.can(sender, false, true)) {
                        subCommand.getCommandExecutor(sender, args);
                        return true;
                    }
                }
            }
        }
        MessageManager.sendSyntax(getPlugin(), list(sender), sender);


        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            // Can use 'self' command
            if (can(sender, false, false)) {
                // Require guild
                if (
                        (((subCommand.needGuild() && subCommand.hasGuild(sender, false))&& (subCommand.guildRole() != null && subCommand.getRole(sender) == subCommand.guildRole())) || !subCommand.needGuild()) ||
                                ((subCommand.noGuild() && !subCommand.hasGuild(sender, false)) || !subCommand.noGuild())
                ) {
                    if (subCommand.getName().equalsIgnoreCase(args[depth()])) {
                        return subCommand.getTabCompleter(sender, args);
                    } else if (args[depth()].isEmpty() || subCommand.getName().startsWith(args[depth()])) {
                        list.add(subCommand.getName());
                    }
                }
            }
        }
        return list;
    }
}

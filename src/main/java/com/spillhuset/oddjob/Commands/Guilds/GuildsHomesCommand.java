package com.spillhuset.oddjob.Commands.Guilds;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.GuildInterface;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuildsHomesCommand extends SubCommand implements GuildInterface {

    public GuildsHomesCommand() {
        subCommands.add(new GuildsHomesAddCommand());
        subCommands.add(new GuildsHomesRelocateCommand());
        subCommands.add(new GuildsHomesRenameCommand());
        subCommands.add(new GuildsHomesTeleportCommand());
        subCommands.add(new GuildsHomesDeleteCommand());
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
    public Plugin getPlugin() {
        return Plugin.guilds;
    }

    @Override
    public String getName() {
        return "homes";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/guilds homes <name>";
    }

    @Override
    public String getPermission() {
        return "guilds.homes";
    }

    @Override
    public int minArgs() {
        return 1;
    }

    @Override
    public int maxArgs() {
        return 4;
    }

    @Override
    public int depth() {
        return 1;
    }

    @Override
    public boolean noGuild() {
        return false;
    }

    @Override
    public boolean needGuild() {
        return true;
    }

    @Override
    public Role guildRole() {
        return null;
    }

    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        if (!can(sender, false, true)) {
            return;
        }
        if (!argsLength(sender, args.length)) {
            return;
        }
        if (!hasGuild(sender, true)) {
            return;
        }

        if (args.length == depth()) {
            Player player = (Player) sender;
            Guild guild = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
            if (guild == null) {
                MessageManager.guilds_not_associated(sender);
                return;
            }
            List<String> homes = OddJob.getInstance().getHomesManager().getList(guild.getUuid());
            MessageManager.guilds_homes_info(sender, guild, homes);
            return;
        }

        finder(sender, args);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        Player player = (Player) sender;
        Guild guild = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
        if (guild == null) return new ArrayList<>();
        for (String name : guild.listHomes()) {
            if (args.length == depth() + 1 && (args[depth()].isEmpty() || name.toLowerCase().startsWith(args[depth()].toLowerCase()))) {
                list.add(name);
            }
        }
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args.length > depth()) {
                if (name.equalsIgnoreCase(args[depth()])) {
                    return subCommand.getTabCompleter(sender, args);
                }
                if (args[depth()].isEmpty() || name.startsWith(args[depth()])) {
                    list.add(name);
                }
            }
        }

        return list;
    }
}

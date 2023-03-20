package com.spillhuset.oddjob.Commands.Guilds;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuildsHomesDeleteCommand extends SubCommand {
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
        return "del";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/guilds homes delete <name>";
    }

    @Override
    public String getPermission() {
        return "guilds.homes";
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public int maxArgs() {
        return 3;
    }

    @Override
    public int depth() {
        return 2;
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
        return Role.Master;
    }

    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        if (!argsLength(sender, args.length)) {
            return;
        }

        if (!can(sender, false, true)) {
            return;
        }
        String name = "home";
        Player player = (Player) sender;
        if (args.length == 3) {
            name = args[2];
        }
        OddJob.getInstance().getGuildsManager().homesRemove(player, name);
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
        return list;
    }
}

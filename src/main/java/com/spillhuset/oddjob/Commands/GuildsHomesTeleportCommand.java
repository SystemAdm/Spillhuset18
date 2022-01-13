package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuildsHomesTeleportCommand extends SubCommand {
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
        return "teleport";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/guilds homes teleport <name>";
    }

    @Override
    public String getPermission() {
        return "guilds";
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
    public void getCommandExecutor(CommandSender sender, String[] args) {
        if (!argsLength(sender, args.length)) {
            return;
        }
        if (!can(sender, false, true)) {
            return;
        }
        String name = "home";
        if (args.length == 3) {
            name = args[2];
        }
        Player player = (Player) sender;
        OddJob.getInstance().getGuildsManager().homeTeleport(player, name);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (sender instanceof Player player && args.length == 3) {
            Guild guild = OddJob.getInstance().getGuildsManager().getGuildByUuid(player.getUniqueId());
            for (String name: guild.listHomes(guild.getUuid())) {
                if (args[2].isEmpty() || name.startsWith(args[2])) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}

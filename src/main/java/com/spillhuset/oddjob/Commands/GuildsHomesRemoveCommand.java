package com.spillhuset.oddjob.Commands;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GuildsHomesRemoveCommand extends SubCommand {
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
        return "remove";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/guilds homes remove <name>";
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
        Player player = (Player) sender;
if (args.length == 3) {
    name = args[2];
}
        OddJob.getInstance().getGuildsManager().homeRemove(player, name);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return null;
    }
}

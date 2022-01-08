package com.spillhuset.oddjob.Commands;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GuildsHomesCommand extends SubCommand {
    private final List<SubCommand> subCommands = new ArrayList<>();
    public GuildsHomesCommand() {
        subCommands.add(new GuildsHomesAddCommand());
        subCommands.add(new GuildsHomesRelocateCommand());
        subCommands.add(new GuildsHomesRenameCommand());
        subCommands.add(new GuildsHomesTeleportCommand());
        subCommands.add(new GuildsHomesRemoveCommand());
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
        return null;
    }

    @Override
    public String getPermission() {
        return "guilds";
    }

    @Override
    public int minArgs() {
        return 1;
    }

    @Override
    public int maxArgs() {
        return 2;
    }

    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            String name = "home";
            if (args.length == 2 && !args[1].equals("")) {
                name = args[1];
            }
            OddJob.getInstance().getGuildsManager().home(player, name);
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return null;
    }
}

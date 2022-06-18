package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Arena;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ArenaCreateCommand extends SubCommand {
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
        return Plugin.arena;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create a new Arena";
    }

    @Override
    public String getSyntax() {
        return "/arena create <name>";
    }

    @Override
    public String getPermission() {
        return "arena.admin";
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public int maxArgs() {
        return 4;
    }

    @Override
    public int depth() {
        return 0;
    }

    @Override
    public boolean noGuild() {
        return false;
    }

    @Override
    public boolean needGuild() {
        return false;
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

        Arena arena = OddJob.getInstance().getArenaManager().arena.get(args[1]);
        WorldType worldType = WorldType.getByName(args[2]);
        World.Environment environment = World.Environment.valueOf(args[3]);
        if (arena == null) {
            OddJob.getInstance().getArenaManager().createArena(args[1], worldType, environment);
            sender.sendMessage("Created!");
            return;
        }
        sender.sendMessage("Exists");
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        OddJob.getInstance().log("Test");
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            return list;
        } else if (args.length == 3) {
            for (WorldType type : WorldType.values()) {
                if (args[2].isEmpty() || type.getName().startsWith(args[2])) {
                    list.add(type.getName());
                }
            }
        } else if (args.length == 4) {
            for (World.Environment environment : World.Environment.values()) {
                if (args[3].isEmpty() || environment.name().startsWith(args[3])) {
                    list.add(environment.name());
                }
            }
        }
        return list;
    }
}

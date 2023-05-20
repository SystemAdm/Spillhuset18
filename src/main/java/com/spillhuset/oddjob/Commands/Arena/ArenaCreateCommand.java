package com.spillhuset.oddjob.Commands.Arena;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Arena;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        return 1;
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
        if (!can(sender,false,true)) return;
        if (!argsLength(sender,args.length)) return;

        // arena create <name>
        WorldType worldType = WorldType.NORMAL;
        for (WorldType w : WorldType.values()) {
            if (w.getName().equalsIgnoreCase(args[3])) {
                worldType = w;
            }
        }
        World.Environment environment = World.Environment.NORMAL;
        for (World.Environment e: World.Environment.values()) {
            if (e.name().equalsIgnoreCase(args[2])) {
                environment = e;
            }
        }
        OddJob.getInstance().getArenaManager().create(sender,args[1],environment,worldType);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            list.add("<name>");
        }
        if (args.length == 3){
            for (World.Environment e : World.Environment.values()) {
                if (args[2].isEmpty() || e.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                    list.add(e.name());
                }
            }
        }
        if (args.length == 4) {
            for (WorldType e : WorldType.values()) {
                if (args[3].isEmpty() || e.name().toLowerCase().startsWith(args[3].toLowerCase())) {
                    list.add(e.name());
                }
            }
        }
        return list;
    }
}

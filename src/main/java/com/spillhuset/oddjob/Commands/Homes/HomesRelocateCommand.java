package com.spillhuset.oddjob.Commands.Homes;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.HomesSQL;
import com.spillhuset.oddjob.Utils.ListInterface;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomesRelocateCommand extends SubCommand implements ListInterface {
    @Override
    public boolean denyConsole() {
        return true;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.homes;
    }

    @Override
    public String getName() {
        return "relocate";
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
        return "homes.relocate";
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
        if (!argsLength(sender, args.length)) {
            return;
        }

        if (!can(sender, false, true)) {
            return;
        }
        // homes change <name>
        // homes change <player> <name>
        String name = "home";
        OddPlayer target = null;

        if (args.length == 2 && sender instanceof Player player) {
            target = OddJob.getInstance().getPlayerManager().get(player.getUniqueId());
            name = args[1].toLowerCase();
        }
        if (args.length == 3) {
            target = OddJob.getInstance().getPlayerManager().get(args[1]);
            name = args[2].toLowerCase();
        }

        if (target != null && sender instanceof Player player) {
            OddJob.getInstance().getHomesManager().change(sender, target, name, player.getLocation());
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        // homes change <name>
        // homes change <player> <name>
        List<String> list = new ArrayList<>();
        if (can(sender, true, false)) {
            if (args.length == 3) {
                UUID uuid = OddJob.getInstance().getPlayerManager().get(args[1]).getUuid();
                if (uuid != null) {
                    for (String home : HomesSQL.getList(uuid)) {
                        if (args[2].isEmpty() || home.toLowerCase().startsWith(args[2].toLowerCase())) {
                            list.add(home);
                        }
                    }
                }
            } else if (args.length == 2) {
                for (String name : OddJob.getInstance().getPlayerManager().listAll()) {
                    if (args[1].isEmpty() || name.toLowerCase().startsWith(args[1].toLowerCase())) {
                        list.add(name);
                    }
                }
            }
        }
        if (args.length == 2) {
            if (sender instanceof Player player) {
                for (String home : HomesSQL.getList(player.getUniqueId())) {
                    if (args[1].isEmpty() || home.toLowerCase().startsWith(args[1].toLowerCase())) {
                        list.add(home);
                    }
                }
            }
        }
        return list;
    }
}

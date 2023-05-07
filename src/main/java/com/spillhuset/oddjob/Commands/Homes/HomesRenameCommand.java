package com.spillhuset.oddjob.Commands.Homes;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.HomesSQL;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomesRenameCommand extends SubCommand {

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
        return Plugin.homes;
    }

    @Override
    public String getName() {
        return "rename";
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
        return "homes";
    }

    @Override
    public int minArgs() {
        return 3;
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
        if (!argsLength(sender, args.length)) {
            return;
        }

        if (!can(sender, false, true)) {
            return;
        }
        // homes rename <old> <new>
        // homes rename <player> <old> <new>
        String nameOld = "old";
        String nameNew = "new";
        OddPlayer target = null;

        if (args.length == 3 && sender instanceof Player player) {
            target = OddJob.getInstance().getPlayerManager().get(player.getUniqueId());
            nameOld = args[1].toLowerCase();
            nameNew = args[2].toLowerCase();
        }
        if (args.length == 4) {
            target = OddJob.getInstance().getPlayerManager().get(args[1]);
            nameOld = args[2].toLowerCase();
            nameNew = args[3].toLowerCase();
        }

        if (target != null) {
            OddJob.getInstance().getHomesManager().rename(sender, target, nameOld, nameNew);
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        // homes rename <old> <new>
        // homes rename <player> <old> <new>
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
            } else {
                list.add("<new_home_name>");
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
        } else if (args.length == 3) {
            list.add("<new_home_name>");
        }
        return list;
    }
}

package com.spillhuset.oddjob.Commands.Homes;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.HomesSQL;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomesTeleportCommand extends SubCommand {
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
        return "teleport";
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
        return 1;
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
        // homes tp <player> [name]
        // homes tp [name]
        OddPlayer destinationPlayer = null;
        String destinationName = "home";

        if (sender instanceof Player player) {
            if (args.length == 3 && can(sender, true, true)) {
                destinationName = args[2];
                destinationPlayer = OddJob.getInstance().getPlayerManager().get(args[1]);
            } else if (args.length == 2) {
                destinationPlayer = OddJob.getInstance().getPlayerManager().get(player.getUniqueId());
                destinationName = args[1];
            }
            if (destinationPlayer == null) {
                MessageManager.errors_find_player(getPlugin(), args[1], sender);
            } else {
                OddJob.getInstance().getHomesManager().teleport(player, destinationPlayer, destinationName);
            }
            return;
        }
        MessageManager.errors_find_player(getPlugin(), args[1], sender);

    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
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

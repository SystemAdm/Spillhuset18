package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Changed;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.HistoryManager;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.ListInterface;
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
        return 2;
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
        if (!can(sender, false, true)) {
            return;
        }

        if (!argsLength(sender, args.length)) {
            return;
        }
        // homes tp <player> [name]
        // homes tp [name]
        OddPlayer destinationPlayer;
        String destinationName = "home";
        if (sender instanceof Player) {
            destinationPlayer = OddJob.getInstance().getPlayerManager().get(((Player) sender).getUniqueId());
            if (args.length == 1) {
                MessageManager.homes_no_name(sender, destinationName);
            }
            if (args.length == 2) {
                destinationName = args[1];
            } else if (args.length == 3) {
                destinationName = args[2];
                destinationPlayer = OddJob.getInstance().getPlayerManager().get(args[1]);
            }
        } else {
            MessageManager.errors_denied_console(getPlugin(), sender);
            return;
        }
        if (destinationPlayer == null) {
            MessageManager.errors_find_player(getPlugin(), args[1], sender);
            return;
        }

        OddJob.getInstance().debug(getPlugin(), "teleport", destinationPlayer.getDisplayName() + " " + destinationName);
        OddJob.getInstance().getHomeManager().teleport(sender, destinationPlayer, destinationName);
        HistoryManager.add(destinationPlayer.getUuid(), Changed.homes_teleported, "", destinationName);

    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (can(sender, true, false)) {
            if (args.length == 2) {
                ListInterface.playerList(list, args[1]);
            } else if (args.length == 3) {
                UUID uuid = OddJob.getInstance().getPlayerManager().get(args[1]).getUuid();
                if (uuid != null) {
                    ListInterface.listHomes(list, uuid, args[2]);
                }
            }
        }
        if (args.length == 2) {
            if (sender instanceof Player player) {
                ListInterface.listHomes(list, player.getUniqueId(), args[1]);
            }
        }
        return list;
    }
}

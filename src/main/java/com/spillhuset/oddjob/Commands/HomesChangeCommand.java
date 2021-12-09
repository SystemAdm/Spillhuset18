package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Changed;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.HistoryManager;
import com.spillhuset.oddjob.Managers.PlayerManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.ListInterface;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomesChangeCommand extends SubCommand implements ListInterface {
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
        return "change";
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
        // homes change <name>
        // homes change <player> <name>
        String name = "home";
        OddPlayer target = null;

        if (args.length == 2 && sender instanceof Player player) {
            target = PlayerManager.get(player.getUniqueId());
            name = args[1].toLowerCase();
        }
        if (args.length == 3) {
            target = PlayerManager.get(args[1]);
            name = args[2].toLowerCase();
        }

        if (target != null && sender instanceof Player player) {
            HistoryManager.add(target.getUuid(), Changed.homes_changed, name, "");
            OddJob.getInstance().getHomeManager().change(sender, target, name, player.getLocation());
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        // homes change <name>
        // homes change <player> <name>
        List<String> list = new ArrayList<>();
        if (can(sender, true, false)) {
            if (args.length == 2) {
                ListInterface.playerList(list,args[1]);
            } else if (args.length == 3) {
                UUID uuid = PlayerManager.get(args[1]).getUuid();
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

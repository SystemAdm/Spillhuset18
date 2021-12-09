package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Changed;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.HistoryManager;
import com.spillhuset.oddjob.Managers.MessageManager;
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

public class HomesAddCommand extends SubCommand implements ListInterface {
    @Override
    public boolean denyConsole() {
        return true;
    }

    @Override
    public boolean denyOp() {
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.homes;
    }

    @Override
    public String getName() {
        return "add";
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
        return "homes.add";
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
    public void getCommandExecutor(CommandSender sender, String[] args) {
        if (!can(sender, false, true)) {
            return;
        }
        if (!argsLength(sender, args.length)) {
            return;
        }

        OddPlayer target;
        String targetName = sender.getName();
        String name = "home";
        if (args.length == 3) {
            targetName = args[1];
            target = PlayerManager.get(targetName);
            name = args[2];
        } else if (sender instanceof Player player) {
            target = PlayerManager.get(player.getUniqueId());
            if (args.length == 2) {
                name = args[1];
            }
        } else {
            MessageManager.errors_too_few_args(getPlugin(), sender);
            return;
        }
        if (target == null) {
            MessageManager.errors_find_player(getPlugin(), targetName, sender);
            return;
        }


        if (sender instanceof Player player) {
            OddJob.getInstance().getHomeManager().add(sender, target, name, player.getLocation());
            OddJob.getInstance().debug(getPlugin(), "add", target.getDisplayName() + " " + name);
            HistoryManager.add(target.getUuid(), Changed.homes_added, "", name);
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (can(sender,true,false)) {
            if (args.length == 2) {
                ListInterface.playerList(list, args[1]);
            } else if (args.length == 3) {
                UUID uuid = PlayerManager.get(args[1]).getUuid();
                if (uuid != null) {
                    list.add("<name_of_home>");
                }
            }
        }
        if (args.length == 2) {
            list.add("<name_of_home>");
        }
        return list;
    }
}

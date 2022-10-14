package com.spillhuset.oddjob.Commands.Homes;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomesListCommand extends SubCommand {
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
        return "list";
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

        Player target = null;
        if (args.length == 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageManager.errors_find_player(getPlugin(), args[1], sender);
                return;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        }

        if (target == null) {
            MessageManager.errors_something_somewhere(getPlugin(), sender);
            return;
        }

        OddJob.getInstance().debug(getPlugin(), "list", target.getDisplayName());
        OddJob.getInstance().getHomeManager().sendList(sender,target);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        // homes list
        // homes list <player>
        List<String> list = new ArrayList<>();

        if (can(sender,true,false)) {
            if (args.length == 2) {
                ListInterface.playerList(list,args[1]);
            }
        }
        return list;
    }
}

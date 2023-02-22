package com.spillhuset.oddjob.Commands.Player;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.ScoreBoard;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.ListInterface;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerSetScoreboardCommand extends SubCommand {
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
        return null;
    }

    @Override
    public String getName() {
        return null;
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
        return null;
    }

    @Override
    public int minArgs() {
        return 0;
    }

    @Override
    public int maxArgs() {
        return 0;
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

        OddPlayer target = null;
        if (args.length == 2) {
            target = OddJob.getInstance().getPlayerManager().get(args[1]);
            if (target == null) {
                MessageManager.errors_find_player(getPlugin(), args[1], sender);
                return;
            }
        } else if (sender instanceof Player) {
            target = OddJob.getInstance().getPlayerManager().get(((Player) sender).getUniqueId());
        }

        if (target == null) {
            MessageManager.errors_something_somewhere(getPlugin(), sender);
            return;
        }
        OddJob.getInstance().getHomesManager().sendList(sender, target);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        for (ScoreBoard scoreBoard : ScoreBoard.values()) {
            if (args[1].isEmpty() || scoreBoard.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                list.add(scoreBoard.name());
            }
        }

        return list;
    }
}

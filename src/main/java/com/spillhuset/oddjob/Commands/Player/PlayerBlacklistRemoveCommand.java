package com.spillhuset.oddjob.Commands.Player;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerBlacklistRemoveCommand extends SubCommand {
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
        return Plugin.players;
    }

    @Override
    public String getName() {
        return "remove";
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
        return "players";
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
        OddPlayer target;
        OddPlayer black;

        if (!argsLength(sender, args.length)) {
            return;
        }
        if (!can(sender, false, true)) {
            return;
        }
        if (can(sender, true, true) && args.length == 4) {
            // players blacklist remove [target] [black]
            target = OddJob.getInstance().getPlayerManager().get(args[2]);
            black = OddJob.getInstance().getPlayerManager().get(args[3]);
            if (target == null || black == null) {
                MessageManager.errors_find_player(getPlugin(), (target == null) ? args[2] : args[3], sender);
                return;
            }
            target.removeBlackList(black.getUuid());
            MessageManager.player_blacklist_removed(getPlugin(),black.getName(),sender);
        } else if (sender instanceof Player player){
            target = OddJob.getInstance().getPlayerManager().get(player.getUniqueId());
            black = OddJob.getInstance().getPlayerManager().get(args[2]);
            if (target == null || black == null) {
                MessageManager.errors_find_player(getPlugin(), (target == null) ? args[2] : args[3], sender);
                return;
            }
            target.removeBlackList(black.getUuid());
            MessageManager.player_blacklist_removed(getPlugin(),black.getName(),sender);
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return null;
    }
}

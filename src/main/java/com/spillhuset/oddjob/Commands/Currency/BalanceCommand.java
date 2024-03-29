package com.spillhuset.oddjob.Commands.Currency;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BalanceCommand extends SubCommandInterface implements CommandExecutor {
    @Override
    public boolean denyConsole() {
        return false;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public boolean canOthers() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.currency;
    }

    @Override
    public String getPermission() {
        return "currency";
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!can(sender, false, true)) {
            return true;
        }

        if (!argsLength(sender, args.length)) {
            return true;
        }
        OddPlayer player = null;
        if (sender instanceof Player play && args.length == 0) {
            player = OddJob.getInstance().getPlayerManager().get(play.getUniqueId());
        } else if (args.length == 1 && can(sender, true, true)) {
            player = OddJob.getInstance().getPlayerManager().get(args[0]);
        }

        if (player == null) {
            MessageManager.errors_find_player(Plugin.currency, args[0], sender);
        } else {
            OddJob.getInstance().getCurrencyManager().showPlayer(sender, player);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return new ArrayList<>();
    }
}

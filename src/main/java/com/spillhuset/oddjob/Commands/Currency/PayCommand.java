package com.spillhuset.oddjob.Commands.Currency;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PayCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    @Override
    public boolean denyConsole() {
        return true;
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
        return "currency.transfer";
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
        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!argsLength(sender, args.length)) {
            return true;
        }

        if (!can(sender, false, true)) {
            return true;
        }///pay <player> <value>

        OddPlayer target;
        double value;

        target = OddJob.getInstance().getPlayerManager().get(args[0]);
        if (target == null) {
            MessageManager.errors_find_player(getPlugin(), args[0], sender);
            return true;
        }

        if (can(sender, true, false) && args.length == 3) {
            OddPlayer newTarget;
            newTarget = OddJob.getInstance().getPlayerManager().get(args[1]);
            if (newTarget == null) {
                MessageManager.errors_find_player(getPlugin(), args[1], sender);
                return true;
            }
            try {
                value = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                MessageManager.errors_number(getPlugin(), args[2], sender);
                return true;
            }
            OddJob.getInstance().getCurrencyManager().pay(sender, newTarget, target, value);
        } else {
            try {
                value = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                MessageManager.errors_number(getPlugin(), args[1], sender);
                return true;
            }
            OddJob.getInstance().getCurrencyManager().pay(sender, target, value);
        }


        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        //pay <to_player> <value>
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            for (String player : OddJob.getInstance().getPlayerManager().listAll()) {
                if (args[0].isEmpty() || player.toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(player);
                }
            }
        }

        if (args.length == 2) {
            if (can(sender, true, false)) {
                for (String player : OddJob.getInstance().getPlayerManager().listAll()) {
                    if (args[1].isEmpty() || player.toLowerCase().startsWith(args[1].toLowerCase())) {
                        list.add(player);
                    }
                }
            }
            list.add("<value>");
        }
        if (args.length == 3 && can(sender, true, false)) {
            list.add("<value>");
        }
        return list;
    }
}

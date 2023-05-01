package com.spillhuset.oddjob.Commands.Currency;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CurrencyAddCommand extends SubCommand {
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
        return Plugin.currency;
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
        return "/currency add <Account> <value>";
    }

    @Override
    public String getPermission() {
        return "currency.admin";
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

        Account account = null;
        OddPlayer oddPlayer = null;
        double value = 0d;

        if (args.length == 3) {
            for (Account acc : Account.values()) {
                if (acc.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    account = acc;
                }
            }
            try {
                value = Double.parseDouble(args[2]);
            } catch (NumberFormatException ignored) {
            }

        } else if (args.length == 4 && can(sender, true, true)) {
            oddPlayer = OddJob.getInstance().getPlayerManager().get(args[1]);
            for (Account acc : Account.values()) {
                if (acc.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    account = acc;
                }
            }
            try {
                value = Double.parseDouble(args[3]);
            } catch (NumberFormatException ignored) {
            }
        }
        if (account == null) {
            return;
        }
        OddJob.getInstance().getCurrencyManager().add(sender, oddPlayer, account, value);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        // currency add <account> <amount>
        // currency add <player> <account> <amount>
        return new ArrayList<>();
    }
}

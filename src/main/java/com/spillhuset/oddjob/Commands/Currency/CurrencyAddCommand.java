package com.spillhuset.oddjob.Commands.Currency;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;

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
        return 3;
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
        Account account;
        OddPlayer oddPlayer;
        double value = 0d;

        if (args.length == 3) {
            account = findAccount(args[1]);
            try {
                value = Double.parseDouble(args[2]);
            } catch (NumberFormatException ignored) {
            }
            OddJob.getInstance().getCurrencyManager().add(sender, account, value);
        } else if (args.length == 4 && can(sender,true,true)) {
            oddPlayer = OddJob.getInstance().getPlayerManager().get(args[1]);
            account = findAccount(args[2]);
            try {
                value = Double.parseDouble(args[3]);
            } catch (NumberFormatException ignored) {
            }
            OddJob.getInstance().getCurrencyManager().add(sender, oddPlayer,account,value);
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        // currency add <account> <amount>
        // currency add <player> <account> <amount>
        List<String> list = new ArrayList<>();
        Account account;
        OddPlayer oddPlayer = null;
        double value;
        if (args.length == 2) {
            list.addAll(finderAccount(args[1]));
            if (can(sender,true,false)) {
                for (OddPlayer odd : OddJob.getInstance().getPlayerManager().getAll()) {
                    if (args[1].isEmpty() || odd.getName().startsWith(args[1])) {
                        list.add(odd.getName());
                    }
                }
            }
        }
        if (args.length == 3) {
            account = findAccount(args[1]);
            if (account == null && can(sender,true,false)) {
                oddPlayer = OddJob.getInstance().getPlayerManager().get(args[1]);
                if (oddPlayer == null) {
                    list.add("Error player!!");
                } else {
                    list.addAll(finderAccount(args[1]));
                }
            } else {
                if (!args[2].isEmpty()) {
                    try {
                        value = Double.parseDouble(args[2]);
                        list.add("value");
                    } catch (NumberFormatException ex) {
                        list.add("Error number!!");
                    }
                }
            }
        }
        if (args.length == 4 && can(sender,true,false)) {
            oddPlayer = OddJob.getInstance().getPlayerManager().get(args[1]);
            account = findAccount(args[2]);
            if (!args[2].isEmpty()) {
                try {
                    value = Double.parseDouble(args[3]);
                    list.add("value");
                } catch (NumberFormatException ex) {
                    list.add("Error number!!");
                }
            }
        }
        return list;
    }

    private Account findAccount(String name) {
        for (Account acc : Account.values()) {
            if (acc.name().equalsIgnoreCase(name)) {
                return acc;
            }
        }
        return null;
    }

    private List<String> finderAccount(String name) {
        List<String> list = new ArrayList<>();
        for (Account acc : Account.values()) {
            if (name.isEmpty() || acc.name().startsWith(name)) {
                list.add(acc.name());
            }
        }
        return list;
    }
}

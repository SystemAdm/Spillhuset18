package com.spillhuset.oddjob.Commands.Currency;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CurrencySetCommand extends SubCommand {
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
        return "set";
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

        if (!can(sender, false, true)) {
            return;
        }

        if (!argsLength(sender, args.length)) {
            return;
        }

        /*            0       1            2       3    */
        /* /currency set <bank|pocket> <player> <value> */
        /* /currency set <bank|pocket> <value>          */
        OddPlayer target = null;
        Account account = null;
        double value = 0.0;

        if (args.length == 4 && can(sender, true, true) ) {
            // Find target
            target = OddJob.getInstance().getPlayerManager().get(args[1]);
            if (target == null) {
                MessageManager.errors_find_player(getPlugin(), args[1], sender);
                return;
            }
            // Find Account
            for (Account acc : Account.values()) {
                if (acc.name().toLowerCase().startsWith(args[2].toLowerCase())) {
                    account = acc;
                }
            }
            if (account == null) {
                MessageManager.currency_invalid_account(sender, args[2]);
                return;
            }
            // Find Value
            try {
                value = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                MessageManager.errors_number(getPlugin(), args[3], sender);
                return;
            }
            OddJob.getInstance().getCurrencyManager().set(sender, target, account, value);
        }
        if (args.length == 3 && sender instanceof Player player) {
            target = OddJob.getInstance().getPlayerManager().get(player.getUniqueId());
            // Find Account
            for (Account acc : Account.values()) {
                if (acc.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    account = acc;
                }
            }
            if (account == null) {
                MessageManager.currency_invalid_account(sender, args[1]);
                return;
            }
            // FInd Value
            try {
                value = Double.parseDouble(args[2]);
            } catch (NumberFormatException ex) {
                MessageManager.errors_number(getPlugin(), args[2], sender);
                return;
            }
            OddJob.getInstance().getCurrencyManager().set(sender, target, account, value);
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (Account a : Account.values()) {
                if (a.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    list.add(a.name());
                }
            }
        }
        if (args.length == 3) {
            if (can(sender, true, false)) {
                if (args[1].equalsIgnoreCase(Account.guild.name())) {
                    for (String name : OddJob.getInstance().getGuildsManager().list()) {
                        if (name.toLowerCase().startsWith(args[2].toLowerCase())) {
                            list.add(name);
                        }
                    }
                } else {
                    for (String name : OddJob.getInstance().getPlayerManager().listAll()) {
                        if (name.toLowerCase().startsWith(args[2].toLowerCase())) {
                            list.add(name);
                        }
                    }
                }
            }
            list.add("[value]");
        }
        if (args.length == 4 && can(sender,true,false)) {
            list.add("[value]");
        }
        return list;
    }
}

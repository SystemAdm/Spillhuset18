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

public class CurrencySubCommand extends SubCommand {
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
        return "sub";
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
        return "currency.sub";
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
        if (!can(sender, false, true)) {
            return;
        }

        if (!argsLength(sender, args.length)) {
            return;
        }
        /*            0       1            2       3    */
        /* /currency sub <bank|pocket> <player> <value> */
        /* /currency sub <bank|pocket> <value>          */
        OddPlayer target = null;
        Account account = null;
        double value = 0.0;

        for (Account acc : Account.values()) {
            if (acc.name().equalsIgnoreCase(args[1])) {
                account = acc;
            }
        }

        if (args.length == 4) {
            OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(args[2]);
            if (oddPlayer == null) {
                MessageManager.errors_find_player(getPlugin(), args[2], sender);
                return;
            }
            target = oddPlayer;
            try {
                value = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                MessageManager.errors_number(getPlugin(), args[3], sender);
            }
        }
        if (args.length == 3 && sender instanceof Player player) {
            target = OddJob.getInstance().getPlayerManager().get(player.getUniqueId());
            try {
                value = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                MessageManager.errors_number(getPlugin(), args[2], sender);
            }
        }
        OddJob.getInstance().getCurrencyManager().sub(sender, target, account, value);

    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        //currency sub <account> <value>
        //currency sub <name> <account> <value>
        List<String> list = new ArrayList<>();
        Account account = null;
        if (args.length == 2) {
            for (Account a : Account.values()) {
                if (args[1].equalsIgnoreCase(a.name())) {

                }
            }
        }
        return list;
    }
}

package com.spillhuset.oddjob.Commands.Currency;
import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;

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
        // currency sub account value
        if (!can(sender,false,true)) return;
        if (!argsLength(sender,args.length)) return;

        OddJob.getInstance().getCurrencyManager().sub(sender,args[1],args[2]);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        //currency sub <account> <value>
        //currency sub <name> <account> <value>
        List<String> list = new ArrayList<>();
        Account account = null;
        if (args.length == 2) {
            for(Account a : Account.values()) {
                if (args[1].equalsIgnoreCase(a.name())) {

                }
            }
        }
        return list;
    }
}

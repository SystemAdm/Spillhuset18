package com.spillhuset.oddjob.Commands.Currency;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TransferCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
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
        return 3;
    }

    @Override
    public int maxArgs() {
        return 5;
    }

    @Override
    public int depth() {
        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        //transfer <bank,pocket,guild> player <to_player> bank <value>
        //transfer <bank,pocket,guild> guild <to_guild> bank <value>
        //transfer <bank,pocket,guild> <bank,pocket,guild> <value>
        if (!argsLength(sender, args.length)) {
            return true;
        }

        if (!can(sender, false, true)) {
            return true;
        }

        Account accountSender = null;
        Account accountReceiver;
        double value;
        Player player = (Player) sender;
        OddPlayer from = OddJob.getInstance().getPlayerManager().get(player.getUniqueId());
        OddPlayer to;
        Guild toGuild;
        Guild fromGuild;
        boolean guild = false;

        List<String> accounts = new ArrayList<>();
        for (Account account : Account.values()) {
            accounts.add(account.name());
        }

        fromGuild = OddJob.getInstance().getGuildsManager().getGuildByMember(((Player) sender).getUniqueId());
        // Find Account
        if (args[0].equalsIgnoreCase("guild") && fromGuild != null) {
            guild = true;
        } else if (accounts.contains(args[0])) {
            accountSender = Account.valueOf(args[0]);
        } else {
            MessageManager.currency_invalid_account(sender, args[0]);
            return true;
        }



        if (args[1].equalsIgnoreCase("guild")) {
            accountReceiver = Account.guild;
            // Find Target
            toGuild = OddJob.getInstance().getGuildsManager().getGuildByName(args[2]);
            if (toGuild == null) {
                MessageManager.guilds_not_found(sender, args[2]);
                return true;
            }
            // Find Value
            try {
                value = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                MessageManager.errors_number(getPlugin(), args[3], sender);
                return true;
            }
            if (guild) {
                OddJob.getInstance().getCurrencyManager().transfer(sender, null, from, accountReceiver, null, value, fromGuild.getUuid(), toGuild.getUuid());
            } else {
                OddJob.getInstance().getCurrencyManager().transfer(sender, accountSender, from, accountReceiver, null, value, null, toGuild.getUuid());
            }
            return true;
        } else if (args[1].equalsIgnoreCase("player")) {
            accountReceiver = Account.bank;
            // Find Target
            to = OddJob.getInstance().getPlayerManager().get(args[2]);
            if (to == null) {
                MessageManager.errors_find_player(getPlugin(), args[2], sender);
                return true;
            }
            OddJob.getInstance().log("to " + to.getName());
            // Find Value
            try {
                value = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                MessageManager.errors_number(getPlugin(), args[3], sender);
                return true;
            }
        } else if (accounts.contains(args[1])) {
            accountReceiver = Account.valueOf(args[1]);
            to = OddJob.getInstance().getPlayerManager().get(player.getUniqueId());
            // Find Value
            try {
                value = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                MessageManager.errors_number(getPlugin(), args[2], sender);
                return true;
            }
            OddJob.getInstance().log("to " + accountReceiver.name());
        } else {
            MessageManager.currency_invalid_account(sender, args[1]);
            return true;
        }
        if (guild) {
            OddJob.getInstance().getCurrencyManager().transfer(sender, null, from, accountReceiver, to, value, fromGuild.getUuid(), null);
        }else {
            OddJob.getInstance().getCurrencyManager().transfer(sender, accountSender, from, accountReceiver, to, value, null, null);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        //transfer <account_from> player <to_player> <value>
        //transfer <account_from> guild <to_guild> <value>
        //transfer <account_from> <to_account> <value>
        List<String> list = new ArrayList<>();

        List<String> accounts = new ArrayList<>();
        for (Account account : Account.values()) {
            accounts.add(account.name());
        }

        List<String> to = new ArrayList<>(accounts);
        to.add("player");

        if (args.length == 1) {
            for (String account : accounts) {
                if (args[0].isEmpty() || account.startsWith(args[0])) {
                    list.add(account);
                }
            }
        }

        if (args.length == 2) {
            to.remove(args[0]);
            for (String account : to) {
                if (args[1].isEmpty() || account.startsWith(args[1])) {
                    list.add(account);
                }
            }
        }

        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("player")) {
                for (String player : OddJob.getInstance().getPlayerManager().listAll()) {
                    if (args[2].isEmpty() || player.toLowerCase().startsWith(args[2].toLowerCase())) {
                        list.add(player);
                    }
                }
            } else {
                list.add("<value>");
            }
        }
        if (args.length == 4) {
            list.add("<value>");
        }
        return list;
    }
}

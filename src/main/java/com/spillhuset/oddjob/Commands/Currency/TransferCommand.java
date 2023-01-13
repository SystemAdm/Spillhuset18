package com.spillhuset.oddjob.Commands.Currency;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransferCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    @Override
    public boolean denyConsole() {
        return true;
    }

    @Override
    public boolean denyOp() {
        return true;
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
        //transfer <accountSender> player <to_player> bank <value>
        //transfer <accountSender> guild <to_guild> bank <value>
        //transfer <accountSender> <to_account> <value>

        Account accountSender = null;
        Account accountReciever = null;
        UUID uuidTarget = null;
        double value = 0d;
        Player player = (Player) sender;

        List<String> accounts = new ArrayList<>();
        for (Account account : Account.values()) {
            accounts.add(account.name());
        }

        if (accounts.contains(args[0])) {
            accountSender = Account.valueOf(args[0]);
        } else {
            MessageManager.currency_invalid_account(sender, args[0]);
            return true;
        }

        if (args[1].equalsIgnoreCase("player")) {
            OddJob.getInstance().log("player");
            // Get the player
            uuidTarget = OddJob.getInstance().getPlayerManager().get(args[2]).getUuid();
            if (uuidTarget == null) {
                MessageManager.errors_find_player(getPlugin(), args[2], sender);
                return true;
            }
            // Resolve value
            try {
                value = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                MessageManager.errors_number(getPlugin(),args[3],sender);
                return true;
            }
            accountReciever = Account.bank;
        } else if (args[1].equalsIgnoreCase("guild")) {
            OddJob.getInstance().log("guild");
            // Get guild
            Guild guild = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
            if (guild == null) {
                MessageManager.guilds_not_associated(sender);
                return true;
            }
            uuidTarget = guild.getUuid();
            // Resolve value
            try {
                value = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                MessageManager.errors_number(getPlugin(),args[2],sender);
                return true;
            }
            accountReciever = Account.guild;
        } else if (accounts.contains(args[1])) {
            OddJob.getInstance().log("other");
            accountReciever = Account.valueOf(args[1]);
            uuidTarget = player.getUniqueId();
            // Resolve value
            try {
                value = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                MessageManager.errors_number(getPlugin(),args[2],sender);
                return true;
            }
        } else {
            MessageManager.currency_invalid_account(sender, args[1]);
            return true;
        }

        OddJob.getInstance().getCurrencyManager().transfer(sender, accountSender, player.getUniqueId(), accountReciever, uuidTarget, value);

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
            for (String account : to) {
                if (args[1].isEmpty() || account.startsWith(args[1])) {
                    list.add(account);
                }
            }
        }

        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("player")) {
                for (String player : OddJob.getInstance().getPlayerManager().listAll()) {
                    if (args[2].isEmpty() || player.startsWith(args[2])) {
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

package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CurrencyTransferCommand extends SubCommand {
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
        return "transfer";
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
        return "currency";
    }

    @Override
    public int minArgs() {
        return 4;
    }

    @Override
    public int maxArgs() {
        return 0;
    }

    @Override
    public int depth() {
        return 1;
    }

    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        if (!argsLength(sender, args.length)) {
            return;
        }
        if (!can(sender, false, true)) {
            return;
        }
        double value = 0d;
        Player player = (Player) sender;
        Guild guild = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
        UUID fromUUID = null;
        UUID toUUID = null;

        Account fromAccount = switch (args[1]) {
            case "pocket" -> Account.pocket;
            case "guild" -> Account.guild;
            default -> Account.bank;
        };

        Account toAccount = switch (args[2]) {
            case "pocket" -> Account.pocket;
            case "guild" -> Account.guild;
            default -> Account.bank;
        };

        if (args.length == 4) {
            try {
                value = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
            if (value != 0d) {
                if (fromAccount == Account.guild) {
                    if (guild == null) {
                        MessageManager.guilds_not_associated(sender);
                        return;
                    }
                    fromUUID = guild.getUuid();
                } else {
                    fromUUID = player.getUniqueId();
                }
                if (toAccount == Account.pocket || toAccount == Account.bank) {
                    toUUID = player.getUniqueId();
                }
            }
        } else if (args.length == 5) {
            try {
                value = Double.parseDouble(args[4]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }
            if (value != 0d) {

                fromUUID = player.getUniqueId();
                toUUID = OddJob.getInstance().getPlayerManager().get(args[3]).getUuid();
                if (toUUID == null) {
                    MessageManager.errors_find_player(Plugin.currency, args[3], sender);
                }
            }
        }
        if (fromUUID == null || toUUID == null) {
            MessageManager.errors_too_few_args(Plugin.currency, sender);
            return;
        }
        if (OddJob.getInstance().getCurrencyManager().transfer(sender,fromAccount, fromUUID, toAccount, toUUID, value)) {
            MessageManager.currency_transferred(sender,fromAccount.name(),toAccount.name(),value);
        }

    }


    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        //! currency transfer bank guild <value>
        //! currency transfer bank pocket <value>
        // currency transfer bank player <OddThing> <value>
        //! currency transfer pocket guild <value>
        //! currency transfer pocket bank <value>
        // currency transfer pocket player <OddThing> <value>
        //! currency transfer guild pocket <value>
        //! currency transfer guild bank <value>
        List<String> list = new ArrayList<>();
        List<String> preList = new ArrayList<>();
        Player player = (Player) sender;
        Guild guild = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
        if (args.length == 2) {
            if (guild != null) {
                if (OddJob.getInstance().getGuildsManager().getRoles().getOrDefault(player.getUniqueId(), Role.guest) == Role.Master) {
                    preList.add("guild");
                }
            }
            preList.add("pocket");
            preList.add("bank");
            for (String string : preList) {
                if (args[1].isEmpty() || string.startsWith(args[1])) {
                    list.add(string);
                }
            }
        }
        if (args.length == 3) {
            preList.add("pocket");
            preList.add("bank");
            preList.add("player");
            if (guild != null) {
                preList.add("guild");
            }
            for (String string : preList) {
                if (args[2].isEmpty() || string.startsWith(args[2])) {
                    list.add(string);
                }
            }
        }
        if (args.length == 4) {
            // value || player
            if (args[2].equalsIgnoreCase("player")) {
                preList.addAll(OddJob.getInstance().getPlayerManager().listString());
            }
            for (String string : preList) {
                if (args[3].isEmpty() || string.startsWith(args[3])) {
                    list.add(string);
                }
            }
        }


        return list;
    }
}

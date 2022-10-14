package com.spillhuset.oddjob.Commands.Homes;

import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.ListInterface;
import com.spillhuset.oddjob.Utils.SubCommand;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class HomesCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public HomesCommand() {
        subCommands.add(new HomesAddCommand());
        subCommands.add(new HomesListCommand());
        subCommands.add(new HomesDelCommand());
        subCommands.add(new HomesRenameCommand());
        subCommands.add(new HomesChangeCommand());
        subCommands.add(new HomesTeleportCommand());
        subCommands.add(new HomesBuyCommand());
    }

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
        return true;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.homes;
    }

    @Override
    public String getPermission() {
        return "homes";
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
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (!can(sender, false, true)) {
            return true;
        }

        if (!argsLength(sender, args.length)) {
            return true;
        }
        if (sender instanceof Player player) {
            int bought = OddJob.getInstance().getPlayerManager().get(player.getUniqueId()).getBoughtHomes();
            int max = OddJob.getInstance().getPlayerManager().get(player.getUniqueId()).getMaxHomes();
            Plu plu = Plu.PLAYER_HOMES;
            double price = plu.getMultiplier() * (bought + 1) * plu.getValue();

            if (args.length == 0) {
                MessageManager.homes_info(sender, bought, max, price);
            }
        }
        finder(sender, args);

        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        List<String> list = new ArrayList<>();

        // List homes
        if (args.length == 1 && sender instanceof Player player) {
            list = OddJob.getInstance().getHomesManager().getList(player.getUniqueId());
            ListInterface.listHomes(list, player.getUniqueId(), Plugin.homes);
        }

        // List commands
        for (SubCommand subCommand : subCommands) {
            if (subCommand.can(sender, false, false)) {
                if (args.length == 0 || (args.length == 1 && subCommand.getName().startsWith(args[0]))) {
                    list.add(subCommand.getName());
                } else if (subCommand.getName().equalsIgnoreCase(args[0])) {
                    return subCommand.getTabCompleter(sender, args);
                }
            }
        }

        return list;
    }
}

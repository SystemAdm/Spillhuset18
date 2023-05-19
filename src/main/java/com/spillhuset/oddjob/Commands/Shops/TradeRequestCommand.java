package com.spillhuset.oddjob.Commands.Shops;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TradeRequestCommand extends SubCommand {
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
        return Plugin.shops;
    }

    @Override
    public String getName() {
        return "request";
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
        return "trade";
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public int maxArgs() {
        return 2;
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

        OddPlayer player = OddJob.getInstance().getPlayerManager().get(((Player) sender).getUniqueId());
        OddPlayer target = OddJob.getInstance().getPlayerManager().get(args[1]);

        if (player == null) {
            OddJob.getInstance().log("sender not found");
            return;
        }
        if (target == null) {
            MessageManager.errors_find_player(getPlugin(), args[1], sender);
            return;
        }
        OddJob.getInstance().log("trade request sent");
        OddJob.getInstance().getShopsManager().tradeRequest(player, target);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if ((sender instanceof Player cmdSender) && cmdSender.getUniqueId().equals(player.getUniqueId())) continue;
                if (args[depth()].isEmpty() || player.getName().startsWith(args[depth()])) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}

package com.spillhuset.oddjob.Commands;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GuildsClaimCommand extends SubCommand {
    @Override
    public boolean denyConsole() {
        return true;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.guilds;
    }

    @Override
    public String getName() {
        return "claim";
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
        return "guilds.claim";
    }

    @Override
    public int minArgs() {
        return 1;
    }

    @Override
    public int maxArgs() {
        return 3;
    }

    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        OddJob.getInstance().log("claimer");
        if (!argsLength(sender,args.length)) {
            return;
        }

        if (!can(sender,false,true)) {
            return;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            OddJob.getInstance().getGuildsManager().claim(player);
            return;
        }
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("auto")) {
                OddJob.getInstance().getGuildsManager().autoClaim(player,OddJob.getInstance().getGuildsManager().getMembers().get(player.getUniqueId()));
                return;
            }
            if (can(sender, true, true)) {
                Guild guild = OddJob.getInstance().getGuildsManager().getGuildByName(args[1]);
                if (guild == null) {
                    MessageManager.guilds_not_found(sender,args[1]);
                    return;
                }
                if (args.length == 3 && args[2].equalsIgnoreCase("auto")) {
                    OddJob.getInstance().getGuildsManager().autoClaim(player,guild.getUuid());
                } else {
                    OddJob.getInstance().getGuildsManager().claim(player,guild);
                }
            }
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return null;
    }
}

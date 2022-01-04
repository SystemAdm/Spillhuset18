package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class GuildsAcceptCommand extends com.spillhuset.oddjob.Utils.SubCommand {
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
        return "accept";
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
        return "guilds";
    }

    @Override
    public int minArgs() {
        return 1;
    }

    @Override
    public int maxArgs() {
        return 2;
    }

    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        if (!can(sender,false,true)) {
            return;
        }

        if (!argsLength(sender,args.length)) {
            return;
        }

        Player player = (Player) sender;
        UUID guild = OddJob.getInstance().getGuildsManager().getMembers().get(player.getUniqueId());
        if (guild == null) {
            // Has invitations to a guild?
            OddJob.getInstance().getGuildsManager().acceptInvite(player,args[1]);
        } else {
            // Guild has pending requests?
            OddJob.getInstance().getGuildsManager().acceptPending(sender,guild,args[1]);
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return null;
    }
}

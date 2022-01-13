package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildsDenyCommand extends com.spillhuset.oddjob.Utils.SubCommand {
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
        return Plugin.guilds;
    }

    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/guilds deny [player]/[guild]";
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
        if (!can(sender, false, true)) {
            return;
        }

        if (!argsLength(sender, args.length)) {
            return;
        }

        Player player = (Player) sender;
        UUID guild = OddJob.getInstance().getGuildsManager().getMembers().get(player.getUniqueId());
        if (guild == null) {
            // Has invitations to a guild?
            OddJob.getInstance().getGuildsManager().denyInvite(player, args.length == 1 ? null : args[1]);
        } else {
            // Guild has pending requests?
            OddJob.getInstance().getGuildsManager().denyPending(sender, guild, args.length == 1 ? null : args[1]);
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}

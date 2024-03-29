package com.spillhuset.oddjob.Commands.Guilds;

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
        return "/guilds claim [guild|auto] [auto]";
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
    public int depth() {
        return 1;
    }

    @Override
    public boolean noGuild() {
        return false;
    }

    @Override
    public boolean needGuild() {
        return true;
    }

    @Override
    public Role guildRole() {
        return Role.Master;
    }

    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        if (!argsLength(sender, args.length)) {
            return;
        }

        if (!can(sender, false, true)) {
            return;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            OddJob.getInstance().getGuildsManager().claim(player, false);
            return;
        }
        //guilds claim outpost
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("outpost")) {
                OddJob.getInstance().getGuildsManager().claim(player, true);
            }
            if (can(sender, true, true)) {
                Guild guild = OddJob.getInstance().getGuildsManager().getGuildByName(args[1]);
                if (guild == null) {
                    MessageManager.guilds_not_found(sender, args[1]);
                    return;
                }

                OddJob.getInstance().getGuildsManager().claim(player, guild);

            }
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (can(sender, true, false)) {
            if (args.length == 2) {
                for (Guild guild : OddJob.getInstance().getGuildsManager().getGuilds().values()) {
                    if (args[1].isEmpty() || guild.getName().startsWith(args[1])) {
                        list.add(guild.getName());
                    }
                }
            }
        }
        return list;
    }
}

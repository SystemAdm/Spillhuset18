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

public class GuildsBuyCommand extends SubCommand {

    public GuildsBuyCommand() {
        subCommands.add(new GuildsBuyHomesCommand());
        subCommands.add(new GuildsBuyClaimsCommand());
        subCommands.add(new GuildsBuyOutpostsCommand());
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
    public Plugin getPlugin() {
        return Plugin.guilds;
    }

    @Override
    public String getName() {
        return "buy";
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
        return "guilds.use";
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
        if (!can(sender, false, true)) {
            return;
        }

        if (!argsLength(sender, args.length)) {
            return;
        }

        if (args.length == 1) {
            Player player =(Player) sender;
            Guild guild = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());

            MessageManager.guilds_to_buy(sender,guild);
        }

        finder(sender, args);

    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args.length == depth() + 1) {
                if (name.equalsIgnoreCase(args[depth()])) {
                    return subCommand.getTabCompleter(sender, args);
                }
                if (args[depth()].isEmpty() || name.startsWith(args[depth()])) {
                    list.add(name);
                }
            }
        }

        return list;
    }
}

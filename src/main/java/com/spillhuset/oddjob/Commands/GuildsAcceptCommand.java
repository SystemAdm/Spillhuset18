package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.GuildType;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.GuildSQL;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildsAcceptCommand extends SubCommand {
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
        return "/guilds accept [player]/[guild]";
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
        /* /guilds accept | /guilds accept <player> */

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
            OddJob.getInstance().getGuildsManager().acceptInvite(player, args.length == 1 ? null : args[1]);
        } else {
            // Guild has pending requests?
            OddJob.getInstance().getGuildsManager().acceptPending(sender, guild, args.length == 1 ? null : args[1]);
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        Player player = (Player) sender;

        Guild guild = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());

        if (guild.getPermissionInvite() == OddJob.getInstance().getGuildsManager().getRoles().get(player.getUniqueId())) {
            List<UUID> g = GuildSQL.getInvite(guild.getUuid(), GuildType.uuid);
            if (!g.isEmpty()) {
                for (UUID uuid :g) {
                    list.add(OddJob.getInstance().getPlayerManager().get(uuid).getName());
                }
            }
        }

        return list;
    }
}

package com.spillhuset.oddjob.Commands.Guilds;

import com.spillhuset.oddjob.Enums.GuildType;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.GuildSQL;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.OddPlayer;
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
        return 4;
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

        if (sender instanceof Player player) {
            Guild inGuild = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
            if (inGuild == null) {
                List<UUID> guilds = OddJob.getInstance().getGuildsManager().getInvites(player.getUniqueId(), false);
                UUID target;
                if (guilds.size() == 1 && args.length == 1) {
                    target = guilds.get(0);
                    Guild guild = OddJob.getInstance().getGuildsManager().getGuild(target);
                    OddJob.getInstance().getGuildsManager().acceptInvite(player, guild);
                    return;
                }
                for (UUID uuid : guilds) {
                    Guild guild = OddJob.getInstance().getGuildsManager().getGuild(uuid);
                    if (guild != null) {
                        target = UUID.fromString(args[1]);
                        if (target.equals(guild.getUuid())) {
                            OddJob.getInstance().getGuildsManager().acceptInvite(player, guild);
                            return;
                        }
                    }
                }
                MessageManager.guilds_invitation_not_found(sender);
            }else {
                List<UUID> targets = OddJob.getInstance().getGuildsManager().getPending(inGuild.getUuid(), true);
                UUID target;
                if (targets.size() == 1 && args.length == 1) {
                    target = targets.get(0);
                    OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(target);
                    OddJob.getInstance().getGuildsManager().acceptPending(oddPlayer,inGuild);
                    return;
                }
                for (UUID uuid : targets) {
                    OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(uuid);
                    if (oddPlayer != null) {
                        target = UUID.fromString(args[1]);
                        if (target.equals(oddPlayer.getUuid())) {
                            OddJob.getInstance().getGuildsManager().acceptPending(oddPlayer,inGuild);
                            return;
                        }
                    }
                }
                MessageManager.guilds_pending_not_found(sender);
            }
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (sender instanceof Player player) {
            Guild inGuild = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
            if (inGuild == null) {
                for (UUID uuid : OddJob.getInstance().getGuildsManager().getInvites(player.getUniqueId(), false)) {
                    Guild guild = OddJob.getInstance().getGuildsManager().getGuild(uuid);
                    if (guild != null) {
                        list.add(guild.getUuid().toString() + " guild " + guild.getName());
                    }
                }
            } else{
                for (UUID uuid : OddJob.getInstance().getGuildsManager().getPending(player.getUniqueId(), true)) {
                    OddPlayer target = OddJob.getInstance().getPlayerManager().get(uuid);
                    if (target != null) {
                        list.add(target.getUuid().toString() + " player " + target.getName());
                    }
                }
            }
        }
        return list;
    }
}

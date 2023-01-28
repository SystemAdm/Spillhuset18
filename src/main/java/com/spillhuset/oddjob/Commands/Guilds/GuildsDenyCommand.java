package com.spillhuset.oddjob.Commands.Guilds;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuildsDenyCommand extends SubCommand {
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
        if (!can(sender, false, true)) {
            return;
        }

        if (!argsLength(sender, args.length)) {
            return;
        }

        if (sender instanceof Player player) {
            // What guild am I in
            Guild inGuild = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
            if (inGuild == null) {
                // In no guild, list invited
                List<UUID> guilds = OddJob.getInstance().getGuildsManager().getInvites(player.getUniqueId(), false);
                UUID target;
                // Is there more than one invitation
                if (guilds.size() == 1 && args.length == 1) {
                    // find the first and only one
                    target = guilds.get(0);
                    Guild guild = OddJob.getInstance().getGuildsManager().getGuild(target);
                    OddJob.getInstance().getGuildsManager().denyInvite(player, guild);
                    return;
                }
                // list them up
                for (UUID uuid : guilds) {
                    // find guild
                    Guild guild = OddJob.getInstance().getGuildsManager().getGuild(uuid);
                    if (guild != null) {
                        target = UUID.fromString(args[1]);
                        // is it the same as I was expecting
                        if (target.equals(guild.getUuid())) {
                            OddJob.getInstance().getGuildsManager().denyInvite(player, guild);
                            return;
                        }
                    }
                }
                MessageManager.guilds_invitation_not_found(sender);
            } else {
                // I am in guild, list requests
                List<UUID> targets = OddJob.getInstance().getGuildsManager().getPending(inGuild.getUuid(), true);
                UUID target;
                // Are there any requests
                if (targets.size() == 1 && args.length == 1) {
                    // Find the first and only one
                    target = targets.get(0);
                    OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(target);
                    OddJob.getInstance().getGuildsManager().denyPending(oddPlayer, inGuild);
                    return;
                }
                // list them up
                for (UUID uuid : targets) {
                    // find player
                    OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(uuid);
                    if (oddPlayer != null) {
                        target = UUID.fromString(args[1]);
                        // is it the same as I was expecting
                        if (target.equals(oddPlayer.getUuid())) {
                            OddJob.getInstance().getGuildsManager().denyPending(oddPlayer, inGuild);
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
        return new ArrayList<>();
    }
}

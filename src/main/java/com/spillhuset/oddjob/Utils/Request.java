package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.SQL.RequestSql;
import org.bukkit.entity.Player;

public class Request {
    /**
     * Player requests to join a guild
     * @param player Player
     * @param guild Guild
     */
    public static void request(Player player, Guild guild) {
        if (RequestSql.hasRequest(player.getUniqueId(),guild.getUuid())) {
            MessageManager.guilds_request_already_sent(player,guild);
            return;
        }
        RequestSql.request(player.getUniqueId(),guild.getUuid());
    }

    public static void invite(Guild guild, OddPlayer target) {
        if (RequestSql.hasInvite(guild.getUuid(),target.getUuid())) {
            MessageManager.guilds_invite_already_sent(target,guild);
            return;
        }
        RequestSql.invite(guild.getUuid(),target.getUuid());
    }
}

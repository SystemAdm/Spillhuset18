package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.ScoreBoard;
import com.spillhuset.oddjob.Managers.*;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Date;
import java.util.List;
import java.util.UUID;


public class OnPlayerJoinEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("[" + ChatColor.GREEN + "+" + ChatColor.RESET + "] " + player.getName());

        CurrencyManager cm = OddJob.getInstance().getCurrencyManager();
        HomesManager hm = OddJob.getInstance().getHomesManager();
        GuildsManager gm = OddJob.getInstance().getGuildsManager();
        PlayerManager pm = OddJob.getInstance().getPlayerManager();

        if(pm.get(player.getUniqueId()) == null) {
            pm.create(player);
        }

        MessageManager.essentials_join(player, hm.getMax(player.getUniqueId()), hm.getCurrent(player.getUniqueId()));
        cm.showPlayer(player,pm.get(player.getUniqueId()));

        Guild guild = gm.getGuildByMember(player.getUniqueId());
        if (guild != null) {
            // Has Guild
            Role role = gm.getRoles().get(player.getUniqueId());
            MessageManager.guild_join(player, guild, role, gm.getBank(guild.getUuid()), gm.hasHome(guild.getUuid()));
            List<UUID> pending = gm.getPending(guild.getUuid(), true);
            if (!pending.isEmpty()) {
                MessageManager.guilds_pending(player, pending);
            }
        } else {
            // Has no guild
            List<UUID> invites = gm.getInvites(player.getUniqueId(), false);
            if (!invites.isEmpty()) {
                MessageManager.guilds_pending_invites(player, invites);
            }
            List<UUID> pending = gm.getPending(player.getUniqueId(), false);
            if (!pending.isEmpty()) {
                MessageManager.guilds_pending_requests(player, pending);
            }
        }

        // Setup Scoreboard
        ScoreBoard scoreboard = pm.get(player.getUniqueId()).getScoreBoard();
        pm.setScoreboard(player, scoreboard);
        OddJob.getInstance().logToFile(new Date().getTime() + " joined: " + player.getName(), "join_quit.txt");
    }
}

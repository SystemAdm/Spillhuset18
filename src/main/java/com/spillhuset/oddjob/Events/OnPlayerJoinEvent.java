package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.Managers.*;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

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

        MessageManager.essentials_join(player, cm.getPocket(player.getUniqueId()), cm.getBank(player.getUniqueId()), hm.getMax(player.getUniqueId()), hm.getCurrent(player.getUniqueId()));
        // Has Guild
        Guild guild = gm.getGuildByMember(player.getUniqueId());
        if (guild != null) {
            Role role = gm.getRoles().get(player.getUniqueId());
            MessageManager.guild_join(player, guild, role, gm.getBank(guild.getUuid()), gm.hasHome(guild.getUuid()));
            List<UUID> pending = gm.getPending(guild.getUuid(), true);
            if (!pending.isEmpty()) {
                MessageManager.guilds_pending(player, pending);
            }
        } else {
            List<UUID> invites = gm.getInvites(player.getUniqueId(), false);
            if (!invites.isEmpty()) {
                MessageManager.guilds_pending_invites(player, invites);
            }
            List<UUID> pending = gm.getPending(player.getUniqueId(), false);
            if (!pending.isEmpty()) {
                MessageManager.guilds_pending_requests(player, pending);
            }
        }

        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("S", Criteria.DUMMY, ChatColor.GREEN + "Spillhuset");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Chunk chunk = player.getLocation().getChunk();
        pm.setInside(player.getUniqueId(), gm.getGuildByCords(chunk.getX(), chunk.getZ(), chunk.getWorld()).getUuid());

        Score chunk_xy = objective.getScore("Chunk x & y: " + chunk.getX() + " " + chunk.getZ());
        chunk_xy.setScore(1);

        Guild in = gm.getGuildByCords(chunk.getX(), chunk.getZ(), chunk.getWorld());
        if (in == null) {
            in = gm.getGuildByZone(Zone.WILD);
        }
        Score inGuild = objective.getScore("Inside guild: " + in.getName());
        inGuild.setScore(1);
        if (guild != null) {
            Score chunk_guild = objective.getScore("Guild: " + guild.getName());
            chunk_guild.setScore(1);
        }

        pm.setScoreboard(player.getUniqueId(), scoreboard);
        player.setScoreboard(scoreboard);

    }
}

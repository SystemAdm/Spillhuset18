package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.CurrencyManager;
import com.spillhuset.oddjob.Managers.GuildsManager;
import com.spillhuset.oddjob.Managers.HomesManager;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

public class OnPlayerJoinEvent implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("[" + ChatColor.GREEN + "+" + ChatColor.RESET + "] " + player.getName());

        CurrencyManager cm = OddJob.getInstance().getCurrencyManager();
        HomesManager hm = OddJob.getInstance().getHomesManager();
        GuildsManager gm = OddJob.getInstance().getGuildsManager();

        MessageManager.essentials_join(player, cm.getPocket(player.getUniqueId()), cm.getBank(player.getUniqueId()), hm.getMax(player.getUniqueId()), hm.getCurrent(player.getUniqueId()));
        Guild guild = gm.getGuildByMember(player.getUniqueId());
        if (guild != null) {
            Role role = gm.getRoles().get(player.getUniqueId());
            MessageManager.guild_join(player, guild, role, gm.getBank(guild.getUuid()), gm.hasHome(guild.getUuid()));
        }

        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("S", Criteria.DUMMY, ChatColor.GREEN+"Spillhuset");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);



        Score chunk_xy = objective.getScore("Chunk x & y:");
        Score chunk_guild = objective.getScore("Guild:");

        OddJob.getInstance().getPlayerManager().setScoreboard(player.getUniqueId(), scoreboard);
        player.setScoreboard(scoreboard);

    }
}

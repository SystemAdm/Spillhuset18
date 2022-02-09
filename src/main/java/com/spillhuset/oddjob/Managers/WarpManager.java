package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.CountdownSpeed;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.WarpSQL;
import com.spillhuset.oddjob.Utils.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class WarpManager {
    public void west(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 600, 1, false, true, true));
        player.teleport(new Location(player.getWorld(), -20000, 300, 0));
    }

    public void east(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 600, 1, false, true, true));
        player.teleport(new Location(player.getWorld(), 20000, 300, 0));
    }

    public void north(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 600, 1, false, true, true));
        player.teleport(new Location(player.getWorld(), 0, 300, -20000));
    }

    public void south(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 600, 1, false, true, true));
        player.teleport(new Location(player.getWorld(), 0, 300, 20000));
    }

    public void add(Player player, String name, double cost, String passwd) {
        if (WarpSQL.exists(name)) {
            MessageManager.warps_exists(player, name);
            return;
        }

        WarpSQL.add(name, player.getLocation(), passwd, cost);
        MessageManager.warps_added(player, name);
    }

    public void teleport(Player player, String name, String passwd) {
        Warp warp = WarpSQL.get(name);

        // TODO check COST

        if (!warp.matchPwd(passwd)) {
            MessageManager.warps_error_password(player, name);
            return;
        }

        OddJob.getInstance().getTeleportManager().teleport(player, warp.getLocation(), Plugin.warps, CountdownSpeed.warp);
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 160, 1000, true, true));
    }

    public List<String> getList() {
        return WarpSQL.getNames();
    }

    public void del(Player player, String name) {
        if (!WarpSQL.exists(name)) {
            MessageManager.warps_not_exists(player, name);
            return;
        }

        WarpSQL.del(name);
        MessageManager.warps_deleted(player, name);
    }

    public void list(CommandSender sender) {
        List<Warp> warps = WarpSQL.list();
        List<String> w = new ArrayList<>();
        for (Warp warp : warps) {
            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.GOLD).append(warp.getName());
            if (warp.isProtected()) {
                sb.append(ChatColor.RED).append(" *").append(ChatColor.RESET);
            }
            if (warp.hasCost()) {
                sb.append(ChatColor.BLUE).append(" $").append(ChatColor.GRAY).append(warp.getCost()).append(ChatColor.RESET);
            }
            w.add(sb.toString());
        }
        MessageManager.warps_list(sender, w);
    }
}

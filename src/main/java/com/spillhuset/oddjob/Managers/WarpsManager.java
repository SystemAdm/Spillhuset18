package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.WarpSQL;
import com.spillhuset.oddjob.Utils.Warp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WarpsManager {
    private final HashMap<UUID, Warp> warps;

    public WarpsManager() {
        warps = load();
    }

    public HashMap<UUID, Warp> load() {
        return WarpSQL.load();
    }

    public void save() {
        WarpSQL.save(warps);
    }

    public void add(Player player, String name, double cost, String passwd) {
        UUID uuid = UUID.randomUUID();
        Warp warp = new Warp(uuid, player.getLocation(), cost, passwd, name);
        warps.put(uuid, warp);
        WarpSQL.add(name, player.getLocation(), passwd, cost);
        MessageManager.warps_successfully_added(player, name);
    }

    public void del(Player player, String name) {
        WarpSQL.del(name);
        MessageManager.warps_successfully_deleted(player, name);
    }

    public List<String> getList() {
        List<String> list = new ArrayList<>();
        for (Warp warp : warps.values()) {
            list.add(warp.getName());
        }
        return list;
    }

    public void list(CommandSender sender) {
        MessageManager.warps_send_list(sender,warps);
    }

    public void teleport(Player player, String name, String passwd) {
        Warp warp = get(name);
        if (ConfigManager.getBoolean("plugin.currency") && warp.hasCost()) {
            if(!OddJob.getInstance().getCurrencyManager().checkPocket(player.getUniqueId(),warp.getCost())) {
                MessageManager.warps_cant_afford(player,name,warp.getCost());
                return;
            }
            OddJob.getInstance().getCurrencyManager().subPocket(player.getUniqueId(),warp.getCost());
        }
        if (warp.isProtected() && !warp.matchPwd(passwd)) {
            MessageManager.warps_protected(player,name);
            return;
        }
        if (ConfigManager.getBoolean("plugin.teleport")) {
            OddJob.getInstance().getTeleportManager().teleport(player, warp.getLocation());
        } else {
            player.teleport(warp.getLocation());
        }
    }

    public Warp get(String name) {
        for (Warp warp : warps.values()) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }

    public void rename(CommandSender sender, String nameOld, String nameNew) {
        if(WarpSQL.exists(nameNew)) {
            MessageManager.warps_exists(sender,nameNew);
            return;
        }
        get(nameOld).setName(nameNew);
        save();
        MessageManager.warps_successfully_renamed(sender,nameOld,nameNew);
    }

    public void relocate(Player player, String name) {
        if(!WarpSQL.exists(name)) {
            MessageManager.warps_not_exists(player,name);
            return;
        }
        get(name).setLocation(player.getLocation());
        save();
        MessageManager.warps_successfully_relocated(player,name);
    }

    public void cost(CommandSender sender, String name, double value) {
        if(!WarpSQL.exists(name)) {
            MessageManager.warps_not_exists(sender,name);
            return;
        }
        get(name).setCost(value);
        save();
        MessageManager.warps_successfully_cost(sender,name,value);
    }

    public void passwd(CommandSender sender, String name, String passwd) {
        if(!WarpSQL.exists(name)) {
            MessageManager.warps_not_exists(sender,name);
            return;
        }
        get(name).setPasswd(passwd);
        save();
        MessageManager.warps_successfully_passwd(sender,name,passwd);
    }
}

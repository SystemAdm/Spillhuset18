package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.CountdownSpeed;
import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeleportManager {

    public void accept(Player player, String name) {
    }

    public void accept(Player player) {
    }

    public void deny(Player player, String name) {
    }

    public void deny(Player player) {
    }

    public void request(Player sender, String name) {
    }

    public void teleport(OddPlayer oddPlayer, Location location) {
        Player player = Bukkit.getPlayer(oddPlayer.getUuid());
        if (player != null) {
            teleport(player,location);
        }
    }

    public void teleport(Player player, Location location) {
        //TODO
        player.teleport(location);
    }
}

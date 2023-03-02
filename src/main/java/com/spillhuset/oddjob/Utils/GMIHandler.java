package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.SQL.GMISql;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class GMIHandler {
    public GMIHandler() {

    }

    public void switchInventory(Player player, GameMode newGameMode) {
        GMISql.setInventory(player, player.getGameMode() );
        if (player.getGameMode().equals(GameMode.CREATIVE) && !newGameMode.equals(GameMode.CREATIVE)) {
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
        if (!GMISql.getInventory(player, newGameMode)) {
            player.getInventory().setBoots(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setHelmet(null);
            player.getInventory().clear();
            player.getEnderChest().clear();
            player.setExp(0);
            player.updateInventory();
        }
    }

    public void saveOnDeath(Player player) {
        GMISql.setInventory(player);
    }

    public void restoreOnSpawn(Player player) {
        GMISql.getInventory(player);
    }
}

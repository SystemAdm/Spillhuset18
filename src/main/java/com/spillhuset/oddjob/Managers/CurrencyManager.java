package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.SQL.CurrencySQL;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CurrencyManager {
    public void initiate(UUID uuid) {
        boolean has = CurrencySQL.has(uuid);
        if (!has) {
            give_start(uuid);
        }
    }

    private void give_start(UUID uuid) {
        CurrencySQL.initial(uuid);
    }

    public void earnBlockBrake(Player player, Block block) {
        Material material = block.getBlockData().getMaterial();
        Plu plu = null;
        for (Plu test : Plu.values()) {
            if (test.name().equalsIgnoreCase(material.name())) {
                plu = test;
            }
        }
        if (plu == null)
            plu = Plu.DEFAULT;
        CurrencySQL.add(player.getUniqueId(), plu, Account.pocket);
    }
}

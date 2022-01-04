package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.CurrencySQL;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CurrencyManager {
    public void initiatePlayer(UUID uuid) {
        boolean has = CurrencySQL.has(uuid);
        if (!has) {
            give_start(uuid,false);
        }
    }

    private void give_start(UUID uuid,boolean guild) {
        if (guild) {
            CurrencySQL.initializeGuild(uuid);
        } else {
        CurrencySQL.initializePlayer(uuid); }
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

    /**
     * Subtract an amount from the account holder
     *
     * @param sender  CommandSender
     * @param account Account type
     * @param uuid    UUID of account
     * @param sum     Double transaction sum
     * @return Boolean if success
     */
    public boolean sub(CommandSender sender, Account account, UUID uuid, double sum) {
        if (CurrencySQL.has(account, uuid, sum)) {
            CurrencySQL.sub(account, uuid, sum);
            return true;
        } else {
            MessageManager.insufficient_funds_guild(sender, sum, OddJob.getInstance().getGuildsManager().getGuildByUuid(uuid));
            return false;
        }
    }

    public void initiateGuild(UUID uuid) {
        boolean has = CurrencySQL.has(uuid);
        if (!has) {
            give_start(uuid,true);
        }
    }
}

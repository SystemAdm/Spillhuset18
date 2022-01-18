package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.Enums.Plugin;
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
            give_start(uuid, false);
        }
    }

    private void give_start(UUID uuid, boolean guild) {
        if (guild) {
            CurrencySQL.initializeGuild(uuid);
        } else {
            CurrencySQL.initializePlayer(uuid);
        }
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

        double value = OddJob.getInstance().earnings.getOrDefault(player.getUniqueId(),0d) + plu.getValue();
        OddJob.getInstance().earnings.put(player.getUniqueId(), value);
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
            give_start(uuid, true);
        }
    }

    public void add(CommandSender sender, String accountString, String valueString) {
        Account account = null;
        for (Account a : Account.values()) {
            if (a.name().equalsIgnoreCase(accountString)) {
                account = a;
            }
        }
        if (account == null) {
            MessageManager.currency_account_not_found(sender, accountString);
            return;
        }

        double value = 0d;
        value = Double.parseDouble(valueString);
        if (value == 0d) {
            MessageManager.invalidNumber(Plugin.currency, sender, valueString);
            return;
        }
        Player player = (Player) sender;
        UUID uuid = null;
        if (account == Account.guild) {
            uuid = OddJob.getInstance().getGuildsManager().getMembers().get(player.getUniqueId());
        } else {
            uuid = player.getUniqueId();
        }

        if (uuid == null) {
            MessageManager.currency_account_not_found(sender, account.name());
            return;
        }
        if (!CurrencySQL.has(uuid))
            CurrencySQL.initializeGuild(uuid);

        CurrencySQL.add(uuid, value, account);
        MessageManager.currency_added(sender, uuid.toString(), account.name(), value);
    }
}
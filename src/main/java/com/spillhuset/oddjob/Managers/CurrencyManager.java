package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.CurrencySQL;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CurrencyManager {
    public boolean checkBank(UUID player, double price) {
        return CurrencySQL.hasBank(player, price);
    }

    public void subBank(UUID player, double price) {
        CurrencySQL.subBank(player, price);
    }

    public void add(CommandSender sender, Account account, double value) {
    }

    public void add(CommandSender sender, OddPlayer oddPlayer, Account account, double value) {
    }

    public void showPlayer(Player player) {
        MessageManager.currency_holding(player, CurrencySQL.getPocket(player.getUniqueId()), CurrencySQL.getBank(player.getUniqueId()));
    }

    public void sub(CommandSender sender, String account, String value) {
    }

    public boolean transfer(CommandSender sender, Account fromAccount, UUID fromUUID, Account toAccount, UUID toUUID, double value) {
        if (fromAccount == Account.pocket) {
            if (!CurrencySQL.hasPocket(fromUUID, value)) {
                MessageManager.insufficient_funds(sender);
                return true;
            }
            CurrencySQL.subPocket(fromUUID, value);
        } else {
            OddJob.getInstance().log("bank:" + CurrencySQL.getBank(fromUUID) + " : " + value);
            if (!CurrencySQL.hasBank(fromUUID, value)) {
                MessageManager.insufficient_funds(sender);
                return true;
            }
            CurrencySQL.subBank(fromUUID, value);
        }

        switch (toAccount) {
            case bank, guild -> CurrencySQL.addBank(toUUID, value);
            case pocket -> CurrencySQL.addPocket(toUUID, value);
        }
        MessageManager.currency_transferred(sender, fromAccount.name(), OddJob.getInstance().getPlayerManager().get(fromUUID).getName(), false, toAccount.name(), OddJob.getInstance().getPlayerManager().get(toUUID).getName(), false, value);
        return true;
        ///transfer bank pocket 280
    }

    public boolean checkPocket(UUID player, double cost) {
        return CurrencySQL.hasPocket(player, cost);
    }

    public void subPocket(UUID player, double cost) {
        CurrencySQL.subPocket(player, cost);
    }

    public double getPocket(UUID player) {
        return CurrencySQL.getPocket(player);
    }

    public double getBank(UUID uniqueId) {
        return CurrencySQL.getBank(uniqueId);
    }

    public void pay(CommandSender sender, String name, UUID target, double value) {
        Player player = (Player) sender;
        if (!CurrencySQL.hasPocket(player.getUniqueId(), value)) {
            MessageManager.insufficient_funds(sender);
            return;
        }

        CurrencySQL.subPocket(player.getUniqueId(), value);
        CurrencySQL.addPocket(target, value);
        MessageManager.currency_paid(sender, name, value);
    }
}

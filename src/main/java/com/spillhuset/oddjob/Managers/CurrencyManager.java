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
        boolean sender_guild = false;
        boolean receiver_guild = false;
        String sender_name = "";
        String receiver_name = "";

        if (fromAccount == Account.pocket) {
            if (!CurrencySQL.hasPocket(fromUUID, value)) {
                MessageManager.insufficient_funds(sender);
                return true;
            }
            CurrencySQL.subPocket(fromUUID, value);
        } else {
            if (!CurrencySQL.hasBank(fromUUID, value)) {
                MessageManager.insufficient_funds(sender);
                return true;
            }
            CurrencySQL.subBank(fromUUID, value);
        }
        OddJob.getInstance().log("transfer");
        switch (toAccount) {
            case guild -> {
                receiver_guild = true;
                receiver_name = OddJob.getInstance().getGuildsManager().getGuilds().get(toUUID).getName();
                CurrencySQL.addBank(toUUID, value);
            }
            case bank -> {
                receiver_name = OddJob.getInstance().getPlayerManager().get(toUUID).getName();
                toUUID = ((Player) sender).getUniqueId();
                CurrencySQL.addBank(toUUID, value);
            }
            case pocket -> {
                receiver_name = OddJob.getInstance().getPlayerManager().get(toUUID).getName();
                CurrencySQL.addPocket(toUUID, value);
            }
        }
        MessageManager.currency_transferred(sender, fromAccount.name(), sender_name, sender_guild, toAccount.name(), receiver_name, receiver_guild, value);
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

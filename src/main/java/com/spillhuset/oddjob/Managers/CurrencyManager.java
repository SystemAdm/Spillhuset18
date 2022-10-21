package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.SQL.CurrencySQL;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CurrencyManager {
    public boolean checkBank(UUID player, double price) {
        return CurrencySQL.hasBank(player,price);
    }

    public void subBank(UUID player, double price) {
        CurrencySQL.subBank(player,price);
    }

    public void add(CommandSender sender, Account account, double value) {
    }

    public void add(CommandSender sender, OddPlayer oddPlayer, Account account, double value) {
    }

    public void showPlayer(Player player) {
    }

    public void sub(CommandSender sender, String account, String value) {
    }

    public boolean transfer(CommandSender sender, Account fromAccount, UUID fromUUID, Account toAccount, UUID toUUID, double value) {
        return true;
    }

    public boolean checkPocket(UUID player, double cost) {
        return CurrencySQL.hasPocket(player,cost);
    }

    public void subPocket(UUID player, double cost) {
        CurrencySQL.subPocket(player,cost);
    }
}

package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.CurrencySQL;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;


public class CurrencyManager {
    private final HashMap<UUID, Double> earnings = new HashMap<>();
    NamespacedKey key = NamespacedKey.minecraft("income");

    public CurrencyManager() {
        final double[] i = {300};
        Bukkit.getScheduler().runTaskTimerAsynchronously(OddJob.getInstance(), () -> {

            String time = "";
            int m = NumberConversions.floor(i[0] / 60);
            if (m > 1) time = m + " minutes ";
            if (m == 1) time = m + " minute ";
            int s = NumberConversions.floor(i[0] - (m * 60));
            if (s > 1) time += s + " seconds";
            if (s == 1) time += s + " second";

            BossBar bossBar = Bukkit.getBossBar(key);
            if (bossBar == null)
                bossBar = Bukkit.createBossBar(key, "INCOME in " + ChatColor.GOLD + time, BarColor.GREEN, BarStyle.SEGMENTED_20);
            bossBar.setTitle("INCOME in " + ChatColor.GOLD + time);
            bossBar.setProgress(i[0] / 300);

            for (UUID uuid : earnings.keySet()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    if (!bossBar.getPlayers().contains(player)) {
                        bossBar.addPlayer(player);
                    }
                }
            }

            if (i[0] == 0) {
                payday();
                i[0] = 300;
            } else {
                i[0]--;
            }
        }, 0, 20);
    }

    private void payday() {
        for (UUID uuid : earnings.keySet()) {
            OddPlayer player = OddJob.getInstance().getPlayerManager().get(uuid);
            add(null, player, Account.bank, earnings.get(uuid));
        }
        BossBar bossBar = Bukkit.getBossBar(key);
        if (bossBar != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (bossBar.getPlayers().contains(player)) bossBar.removePlayer(player);
            }
        }
        earnings.clear();
    }

    public void add(CommandSender sender, Account account, double value) {
        Player player = (Player) sender;
        double current = CurrencySQL.get(player.getUniqueId(), Account.bank);
        double newValue = current + value;
        CurrencySQL.set(player.getUniqueId(), Account.bank, newValue);
        MessageManager.currency_added(sender, account, value, newValue);
    }

    public void add(CommandSender sender, @NotNull OddPlayer oddPlayer, @NotNull Account account, double value) {
        double current = CurrencySQL.get(oddPlayer.getUuid(), Account.bank);
        double newValue = current + value;
        CurrencySQL.set(oddPlayer.getUuid(), Account.bank, newValue);
        if (sender != null) {
            MessageManager.currency_added(sender, oddPlayer, account, value, newValue);
        } else {
            MessageManager.currency_payday(oddPlayer, value);
        }
    }

    public void showPlayer(Player player) {
        MessageManager.currency_holding(player, CurrencySQL.get(player.getUniqueId(), Account.pocket), CurrencySQL.get(player.getUniqueId(), Account.bank));
    }

    public void sub(CommandSender sender, Account account, double value) {
        Player player = (Player) sender;
        double current = CurrencySQL.get(player.getUniqueId(), account);
        double newValue = current - value;
        CurrencySQL.set(player.getUniqueId(), account, newValue);
        MessageManager.currency_subbed(sender, account, value, newValue);
    }

    public void sub(CommandSender sender, @NotNull OddPlayer oddPlayer, @NotNull Account account, double value) {
        double current = CurrencySQL.get(oddPlayer.getUuid(), account);
        double newValue = current - value;
        CurrencySQL.set(oddPlayer.getUuid(), account, newValue);
        if (sender != null) {
            MessageManager.currency_subbed(sender, oddPlayer, account, value, newValue);
        }
    }

    public void transfer(CommandSender sender, Account fromAccount, OddPlayer fromPlayer, Account toAccount, OddPlayer toPlayer, double value) {
        boolean sender_guild = false;
        boolean receiver_guild = false;
        String sender_name = "";
        String receiver_name;

        if (!CurrencySQL.has(fromPlayer.getUuid(), fromAccount, value)) {
            MessageManager.insufficient_funds(sender);
            return;
        }
        sub(null, fromPlayer, fromAccount, value);

        OddJob.getInstance().log("transfer");


        receiver_name = toPlayer.getName();
        add(null, toPlayer, toAccount, value);


        MessageManager.currency_transferred(sender, fromAccount.name(), sender_name, sender_guild, toAccount.name(), receiver_name, receiver_guild, value);
        ///transfer bank pocket 280
    }

    public boolean has(UUID player, Account account, double cost) {
        return !CurrencySQL.has(player, account, cost);
    }

    public void sub(CommandSender sender, UUID player, Account account, double value) {
        double current = CurrencySQL.get(player, account);
        double newValue = current - value;
        CurrencySQL.set(player, account, newValue);
    }

    public double get(UUID uniqueId, Account account) {
        return CurrencySQL.get(uniqueId, account);
    }

    public void pay(CommandSender sender, OddPlayer target, double value) {
        Account affected = Account.pocket;
        Player player = (Player) sender;

        if (!CurrencySQL.has(player.getUniqueId(), affected, value)) {
            MessageManager.insufficient_funds(sender);
            return;
        }

        sub(null, OddJob.getInstance().getPlayerManager().get(player.getUniqueId()), affected, value);
        add(null, target, affected, value);
        MessageManager.currency_paid(sender, target.getDisplayName(), value);
    }

    public void earnings(UUID uniqueId) {
        double inc = 0.5;
        if (earnings.containsKey(uniqueId)) {
            inc += earnings.get(uniqueId);
        }
        earnings.put(uniqueId, inc);
    }

    public void set(CommandSender sender, OddPlayer oddPlayer, Account account, double value) {
        CurrencySQL.set(oddPlayer.getUuid(), account, value);
        if (sender != null) {
            MessageManager.currency_set(sender, oddPlayer, account, value);
        } else {
            MessageManager.currency_payday(oddPlayer, value);
        }
    }
}

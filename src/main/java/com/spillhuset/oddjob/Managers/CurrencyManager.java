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
            if (m > 1) time = m + " minutter ";
            if (m == 1) time = m + " minutt ";
            int s = NumberConversions.floor(i[0] - (m * 60));
            if (s > 1) time += s + " sekunder";
            if (s == 1) time += s + " sekund";

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


    public boolean checkBank(UUID player, double price) {
        return CurrencySQL.hasBank(player, price);
    }

    public void subBank(UUID player, double price) {
        CurrencySQL.subBank(player, price);
    }

    public void add(CommandSender sender, Account account, double value) {
        Player player = (Player) sender;
        if (account.equals(Account.pocket)) {
            CurrencySQL.addPocket(player.getUniqueId(), value);
        } else {
            CurrencySQL.addBank(player.getUniqueId(), value);
        }
        MessageManager.currency_added(sender, account, value);
    }

    public void add(CommandSender sender, OddPlayer oddPlayer, @NotNull Account account, double value) {
        if (account.equals(Account.pocket)) {
            CurrencySQL.addPocket(oddPlayer.getUuid(), value);
        } else {
            CurrencySQL.addBank(oddPlayer.getUuid(), value);
        }
        if (sender != null) {
            MessageManager.currency_added(sender, oddPlayer, account, value);
        } else {
            MessageManager.currency_payday(oddPlayer, value);
        }
    }

    public void showPlayer(Player player) {
        MessageManager.currency_holding(player, CurrencySQL.getPocket(player.getUniqueId()), CurrencySQL.getBank(player.getUniqueId()));
    }

    public void sub(CommandSender sender, Account account, double value) {
        Player player = (Player) sender;
        if (account.equals(Account.pocket)) {
            CurrencySQL.subPocket(player.getUniqueId(), value);
        } else {
            CurrencySQL.subBank(player.getUniqueId(), value);
        }
        MessageManager.currency_subbed(sender, account, value);
    }

    public void sub(CommandSender sender, OddPlayer oddPlayer, @NotNull Account account, double value) {
        if (account.equals(Account.pocket)) {
            CurrencySQL.subPocket(oddPlayer.getUuid(), value);
        } else {
            CurrencySQL.subBank(oddPlayer.getUuid(), value);
        }
        if (sender != null) {
            MessageManager.currency_subbed(sender, oddPlayer, account, value);
        }
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

    public void earnings(UUID uniqueId) {
        double inc = 0.5;
        if (earnings.containsKey(uniqueId)) {
            inc += earnings.get(uniqueId);
        }
        earnings.put(uniqueId, inc);
    }
}

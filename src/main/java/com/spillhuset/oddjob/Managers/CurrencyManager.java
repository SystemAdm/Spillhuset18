package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.CurrencySQL;
import com.spillhuset.oddjob.Utils.Guild;
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
    private final Account AFFECTED_PAY = Account.pocket;
    private final Account AFFECTED_TRANSFER = Account.bank;
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

            if (i[0] <= 0) {
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
            add(null,player.getUuid(), AFFECTED_TRANSFER, earnings.get(uuid),false);
            OddJob.getInstance().log("earn:"+earnings.get(uuid));
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
        double newValue = add(player.getUniqueId(), account, value);
        MessageManager.currency_added(sender, account, value, newValue);
    }

    public double add(UUID uuid, Account account, double value) {
        double current = CurrencySQL.get(uuid, account);
        double newValue = current + value;
        CurrencySQL.set(uuid, account, newValue);
        return newValue;
    }

    public void add(CommandSender sender, @NotNull UUID uuid, @NotNull Account account, double value,boolean guild) {
        double newValue = add(uuid, account, value);
        if (sender != null) {
            if (guild){
                Guild guildTarget = OddJob.getInstance().getGuildsManager().getGuild(uuid);
                MessageManager.currency_added_guild(sender, guildTarget, account, value, newValue);
            } else {
                OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(uuid);
                MessageManager.currency_added(sender, oddPlayer, account, value, newValue);
            }
        } else {
            OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(uuid);
            MessageManager.currency_payday(oddPlayer, value);
        }
    }

    public void showPlayer(CommandSender sender, OddPlayer player) {
        MessageManager.currency_holding(sender, get(player.getUuid(), Account.pocket), get(player.getUuid(), AFFECTED_TRANSFER));
    }

    public void sub(CommandSender sender, Account account, double value) {
        Player player = (Player) sender;
        double newValue = sub(player.getUniqueId(), account, value);
        MessageManager.currency_subbed(sender, account, value, newValue);
    }

    public double sub(CommandSender sender, @NotNull OddPlayer oddPlayer, @NotNull Account account, double value) {
        double newValue = sub(oddPlayer.getUuid(), account, value);
        if (sender != null) {
            MessageManager.currency_added(sender, oddPlayer, account, value, newValue);
        }
        return newValue;
    }

    public void transfer(CommandSender sender, Account fromAccount, OddPlayer fromPlayer, Account toAccount, OddPlayer toPlayer, double value, UUID fromGuild, UUID toGuild) {
        boolean sender_guild = false;
        boolean receiver_guild = false;
        String sender_name = "";
        String receiver_name;

        if (has(fromPlayer.getUuid(), fromAccount, value)) {
            MessageManager.insufficient_funds(sender);
            return;
        }
        OddJob.getInstance().log("value " + sub(null, fromPlayer, fromAccount, value));

        OddJob.getInstance().log("transfer");

        UUID toUUID = (toGuild == null) ? toPlayer.getUuid() : toGuild;
        receiver_name = (toGuild == null) ? toPlayer.getName() : OddJob.getInstance().getGuildsManager().getGuild(toGuild).getName();
        add(null, toUUID, toAccount, value,toGuild != null);

        MessageManager.currency_transferred(sender, fromAccount.name(), sender_name, sender_guild, toAccount.name(), receiver_name, receiver_guild, value);
        ///transfer bank pocket 280
    }

    public boolean has(UUID player, Account account, double cost) {
        return !CurrencySQL.has(player, account, cost);
    }

    public double sub(UUID player, Account account, double value) {
        double current = CurrencySQL.get(player, account);
        double newValue = current - value;
        CurrencySQL.set(player, account, newValue);
        return newValue;
    }

    public double get(UUID uniqueId, Account account) {
        return CurrencySQL.get(uniqueId, account);
    }

    public void pay(CommandSender sender, OddPlayer target, double value) {
        Player player = (Player) sender;
        if (player == null) return;
        if (!CurrencySQL.has(player.getUniqueId(), AFFECTED_PAY, value)) {
            MessageManager.insufficient_funds(sender);
            return;
        }

        sub(null, OddJob.getInstance().getPlayerManager().get(player.getUniqueId()), AFFECTED_PAY, value);
        add(null, target.getUuid(), AFFECTED_PAY, value,false);
        MessageManager.currency_paid(sender, target.getDisplayName(), value);
    }

    public void pay(CommandSender sender, OddPlayer newTarget, OddPlayer target, double value) {
        if (!CurrencySQL.has(newTarget.getUuid(), AFFECTED_PAY, value)) {
            MessageManager.insufficient_funds(sender);
            return;
        }

        sub(null, OddJob.getInstance().getPlayerManager().get(newTarget.getUuid()), AFFECTED_PAY, value);
        add(null, target.getUuid(), AFFECTED_PAY, value,false);
        MessageManager.currency_paid(sender, target.getDisplayName(), value);
    }

    public void earnings(UUID uniqueId) {
        double inc = 0.5;
        if (earnings.containsKey(uniqueId)) {
            inc += earnings.get(uniqueId);
        }
        earnings.put(uniqueId, inc);
    }

    public void set(UUID uuid, Account account, double value) {
        CurrencySQL.set(uuid, account, value);
    }

    public void set(CommandSender sender, OddPlayer oddPlayer, Account account, double value) {
        set(oddPlayer.getUuid(), account, value);
        if (sender != null) {
            MessageManager.currency_set(sender, oddPlayer, account, value);
        }
    }
}

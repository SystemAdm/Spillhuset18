package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TradeMenu implements Menu {
    private final Inventory inventory = Bukkit.createInventory(this, 27, "TradeMenu");
    private final UUID target;
    private final UUID trader;
    private UUID clicked = null;
    private boolean abort = true;

    public TradeMenu(UUID trader, UUID target) {
        this.trader = trader;
        OddJob.getInstance().getShopsManager().values.put(trader, 0d);
        this.target = target;
        OddJob.getInstance().getShopsManager().values.put(target, 0d);
        inventory.setItem(9, OddJob.getInstance().getShopsManager().incTen());
        inventory.setItem(10, OddJob.getInstance().getShopsManager().incOne());
        inventory.setItem(11, OddJob.getInstance().getShopsManager().decOne());
        inventory.setItem(12, OddJob.getInstance().getShopsManager().decTen());
        inventory.setItem(14, OddJob.getInstance().getShopsManager().total(trader, true));
        inventory.setItem(15, OddJob.getInstance().getShopsManager().clear());
        inventory.setItem(16, OddJob.getInstance().getShopsManager().notReady());
        inventory.setItem(17, OddJob.getInstance().getShopsManager().total(target, false));
    }

    @Override
    public boolean onClick(Player player, int slot, ClickType type) {
        boolean trader = player.getUniqueId().equals(this.trader);
        double value = OddJob.getInstance().getShopsManager().values.get(player.getUniqueId());
        switch (slot) {
            case 9 -> {
                if (OddJob.getInstance().getCurrencyManager().has(player.getUniqueId(), Account.pocket, value * 10)) {
                    value = value * 10;
                }
            }
            case 10 -> {
                if (OddJob.getInstance().getCurrencyManager().has(player.getUniqueId(), Account.pocket, value + 1)) {
                    value++;
                }
            }
            case 15 -> value = 0;
            case 11 -> value--;
            case 12 -> value = value / 10;
            case 16 -> {
                if (inventory.getItem(slot) == null) return false;
                if (inventory.getItem(slot).getType() == Material.REDSTONE_BLOCK) {
                    inventory.setItem(slot, OddJob.getInstance().getShopsManager().ready(player.getName()));
                    this.clicked = player.getUniqueId();
                } else if (inventory.getItem(slot).getType() == Material.EMERALD_BLOCK) {
                    if (player.getUniqueId().equals(clicked)) {
                        abort();
                    } else {
                        clicked = null;
                        trade();
                        OddJob.getInstance().getShopsManager().tradeActive.remove(this);
                    }
                }
            }
        }
        OddJob.getInstance().getShopsManager().values.put(player.getUniqueId(), value);
        if (trader) {
            inventory.setItem(14, OddJob.getInstance().getShopsManager().total(player.getUniqueId(), true));
        } else {
            inventory.setItem(17, OddJob.getInstance().getShopsManager().total(player.getUniqueId(), false));
        }
        if (slot < 27 && slot != 16 && clicked != null) {
            abort();
        }
        return true;
    }

    private void abort() {
        clicked = null;
        inventory.setItem(16, OddJob.getInstance().getShopsManager().notReady());
    }

    private void trade() {
        Player target = Bukkit.getPlayer(this.target);
        if (target != null) {
            for (int i = 0; i < 9; i++) {
                if (inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
                    target.getInventory().addItem(inventory.getItem(i));
                }
            }
        }
        double trade = OddJob.getInstance().getShopsManager().values.get(this.trader);
        OddJob.getInstance().getCurrencyManager().sub(this.trader, Account.pocket, trade);
        OddJob.getInstance().getCurrencyManager().add(this.target, Account.pocket, trade);

        Player trader = Bukkit.getPlayer(this.trader);
        if (trader != null) {
            for (int i = 18; i < 27; i++) {
                if (inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
                    trader.getInventory().addItem(inventory.getItem(i));
                }
            }
        }
        double trading = OddJob.getInstance().getShopsManager().values.get(this.target);
        OddJob.getInstance().getCurrencyManager().sub(this.target, Account.pocket, trading);
        OddJob.getInstance().getCurrencyManager().add(this.trader, Account.pocket, trading);
        abort = false;
        close();
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public UUID getTrader() {
        return trader;
    }

    public UUID getTarget() {
        return target;
    }

    public void close() {
        OddJob.getInstance().getShopsManager().tradeActive.remove(this);
        OddJob.getInstance().getShopsManager().values.remove(target);
        OddJob.getInstance().getShopsManager().values.remove(trader);
        Player target = Bukkit.getPlayer(this.target);
        if (target != null) {
            if (abort) {
                for (int i = 18; i < 27; i++) {
                    if (inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
                        target.getInventory().addItem(inventory.getItem(i));
                    }
                }
            }
            target.closeInventory();
        }
        Player trader = Bukkit.getPlayer(this.trader);
        if (trader != null) {
            if (abort) {
                for (int i = 0; i < 9; i++) {
                    if (inventory.getItem(i) != null && inventory.getItem(i).getType() != Material.AIR) {
                        trader.getInventory().addItem(inventory.getItem(i));
                    }
                }
            }
            trader.closeInventory();
        }
        inventory.clear();
    }
}

package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.PriceList;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class ShopsManager {

    private final HashMap<UUID, UUID> trades = new HashMap<UUID, UUID>();

    public void sell(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        PriceList plu = null;
        for (PriceList unit : PriceList.values()) {
            if (unit.name().equalsIgnoreCase(item.getType().name())) {
                plu = unit;
                break;
            }
        }
        if (plu == null) {
            OddJob.getInstance().log("wrong: " + item.getType().name());
            return;
        }
        if (!plu.isEnabled()) {
            MessageManager.shops_not_sellable(player, item);
            return;
        }
        if (!plu.isSellAble() || plu.getNormal() == 0) {
            MessageManager.shops_not_sellable(player, item);
        } else {
            int amount = item.getAmount();
            double price = Math.round(plu.getNormal() * amount);
            double temp = Math.round(Math.max(plu.getNormal() * 0.9, plu.getMinimum()));

            OddJob.getInstance().getCurrencyManager().add(player, Account.bank, price);
            player.getInventory().remove(item);
            MessageManager.shops_sold_info(player, item, plu.getNormal(), amount, price, temp);
            plu.setNormal(temp);
        }
    }

    public void buy(Player player, ItemStack item) {
        int amount = item.getAmount();
        PriceList plu = null;
        for (PriceList unit : PriceList.values()) {
            if (unit.name().equalsIgnoreCase(item.getType().name())) {
                plu = unit;
                break;
            }
        }
        if (plu == null) {
            OddJob.getInstance().log("wrong: " + item.getType().name());
            return;
        }
        // Is enabled
        if (!plu.isEnabled()) {
            MessageManager.shops_not_sellable(player, item);
            return;
        }
        // Is buy able
        if (!plu.isBuyAble() || plu.getNormal() == 0) {
            MessageManager.shops_not_sellable(player, item);
        } else {
            double price = Math.round(plu.getNormal() * amount * 1.1);
            double temp = Math.min(plu.getNormal() * 1.1, plu.getMaximum());

            OddJob.getInstance().getCurrencyManager().sub(player, Account.bank, price);
            player.getInventory().addItem(item);
            MessageManager.shops_bought_info(player, item, plu.getNormal(), amount, price, temp);
            plu.setNormal(temp);
        }
    }

    public void getPrice(Player player, ItemStack item) {
        PriceList plu = null;
        for (PriceList unit : PriceList.values()) {
            if (unit.name().equalsIgnoreCase(item.getType().name())) {
                plu = unit;
                break;
            }
        }
        if (plu == null) {
            OddJob.getInstance().log("wrong: " + item.getType().name());
            return;
        }
        if (!plu.isEnabled()) {
            MessageManager.shops_not_sellable(player, item);
            return;
        }
        if (!plu.isSellAble() || plu.getNormal() == 0) {
            MessageManager.shops_not_sellable(player, item);
        } else {
            int amount = item.getAmount();
            double priceSell = Math.round(plu.getNormal() * amount);
            double priceBuy = Math.round(priceSell * 1.1);
            double tempSell = Math.round(Math.max(plu.getNormal() * 0.9, plu.getMinimum()));
            double tempBuy = Math.round(Math.min((plu.getNormal() * 1.1) * 1.1, plu.getMaximum()));

            MessageManager.shops_price_sell(player, item, plu.getNormal(), amount, priceSell, tempSell);
            MessageManager.shops_price_buy(player, item, Math.round(plu.getNormal() * 1.1), amount, priceBuy, tempBuy);
        }
    }

    public void tradeRequest(CommandSender sender, Player player) {
        UUID trader = ((Player) sender).getUniqueId();

        // traded with earlier
        UUID old = trades.get(trader);
        OddPlayer target = OddJob.getInstance().getPlayerManager().get(old);
        if (old != null) {
            if (old.equals(player.getUniqueId())) {
                // trade cancelled
                MessageManager.shops_trade_cancelled(sender, target);
                //todo cancel
                trades.remove(trader);
                return;
            } else {
                // trade changed from old to new
                MessageManager.shops_trade_changed(sender, player,target);
            }
            // trade with old aborted
            MessageManager.shops_trade_aborted(sender, target);
        }

        MessageManager.shops_trade_created(sender,player);
        trades.put(trader, player.getUniqueId());
    }
}

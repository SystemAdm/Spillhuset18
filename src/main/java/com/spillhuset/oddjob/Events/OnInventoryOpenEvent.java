package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Menu;
import com.spillhuset.oddjob.Utils.TradeMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

public class OnInventoryOpenEvent implements Listener {
    @EventHandler
    private void onOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryHolder holder = event.getInventory().getHolder();
        TradeMenu active = null;
        if (holder instanceof Menu) {
            for (TradeMenu trade:OddJob.getInstance().getShopsManager().tradeActive) {
                if (trade.getTarget().equals(player.getUniqueId()) || trade.getTrader().equals(player.getUniqueId())) {
                    active = trade;
                }
            }
            if (active == null) return;
        }
    }
}

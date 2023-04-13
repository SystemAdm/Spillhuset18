package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Shop;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class OnInventoryCloseEvent implements Listener {
    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        OddJob.getInstance().log("inventory interact event");
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {

        OddJob.getInstance().log("inventory move item event");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        String name = event.getView().getTitle().split(" ")[0];
        OddJob.getInstance().log(name);

        Shop shop = OddJob.getInstance().getShopsManager().get(name);
        if (shop == null) {
            return;
        }
        event.setCancelled(true);
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType().equals(Material.AIR) || !itemStack.hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasLore()) {
            return;
        }

        int amount = itemStack.getAmount();
        if (!itemMeta.hasLore() || itemMeta.getLore() == null) return;
        List<String> lore = itemMeta.getLore();
        try {
            if (!event.isRightClick()) {
                if (lore.get(0).equalsIgnoreCase("")) return;
                double sell = Double.parseDouble(ChatColor.stripColor(lore.get(0).split(": ")[1]));
                OddJob.getInstance().getShopsManager().sell(player, itemStack.getType(), amount, sell);
            } else {
                if (lore.get(1).equalsIgnoreCase("")) return;
                double buy = Double.parseDouble(ChatColor.stripColor(lore.get(1).split(": ")[1]));
                OddJob.getInstance().getShopsManager().buy(player, itemStack.getType(), amount, buy);
            }
        } catch (NumberFormatException ignored) {

        }

        OddJob.getInstance().log((event.isRightClick() ? "r" : "l") + " " + event.getRawSlot());
        OddJob.getInstance().log("inventory click event");
    }

    @EventHandler
    public void onInventoryPickup(InventoryPickupItemEvent event) {
        OddJob.getInstance().log("inventory pickup item event");
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        OddJob.getInstance().log("inventory drag event");
    }

    @EventHandler
    public void onInventory(InventoryEvent event) {
        OddJob.getInstance().log("inventory event");
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();

        if (inventory.getType().equals(InventoryType.WORKBENCH) && player.getGameMode().equals(GameMode.CREATIVE)) {
            boolean empty = true;
            for (ItemStack itemStack : inventory.getContents()) {
                if (!itemStack.getType().isAir()) {
                    empty = false;
                }
            }
            if (!empty) {
                inventory.clear();
            }
        }
        OddJob.getInstance().getPlayerManager().removeInventory(inventory, player);
    }
}

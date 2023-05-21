package com.spillhuset.oddjob.Utils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;

public interface Menu extends InventoryHolder {
    default boolean onClick(Player player, int slot, ClickType type) {
        return true;
    }

    default void onOpen(Player player) {
    }

    default void onClose(Player player) {
    }
}

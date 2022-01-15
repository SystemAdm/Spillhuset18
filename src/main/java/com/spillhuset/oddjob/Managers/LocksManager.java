package com.spillhuset.oddjob.Managers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LocksManager {
    private final HashMap<String, ItemStack> tools = new HashMap<>();
    private final List<UUID> isLocking = new ArrayList<>();
    private final List<UUID> isUnlocking = new ArrayList<>();
    private final List<UUID> isInfo = new ArrayList<>();

    public LocksManager() {
        createTools();
    }

    private void createTools() {
        // Locking tool
        ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "LOCKING TOOL");
            lore.add("Use this tool to lock your item.");
            lore.add("Right click to protect your stuff!");
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        tools.put("lock", item);

        // UnLocking tool
        item = new ItemStack(Material.TRIPWIRE_HOOK);
        meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "UNLOCKING TOOL");
            lore.add("Use this tool to unlock your item.");
            lore.add("Right click to unprotect your stuff!");
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        tools.put("unlock", item);

        // InfoLocking tool
        item = new ItemStack(Material.MAP);
        meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "INFO TOOL");
            lore.add("Use this tool to know whom locked your item.");
            lore.add("Right click look at the signature!");
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        tools.put("info", item);
    }

    public void giveLockTool(Player player) {
        clear(player);
        isLocking.add(player.getUniqueId());
        player.getInventory().setItem(player.getInventory().firstEmpty(), tools.get("lock"));

    }

    public void giveUnlockTool(Player player) {
        clear(player);
        isUnlocking.add(player.getUniqueId());
        player.getInventory().setItem(player.getInventory().firstEmpty(), tools.get("unlock"));

    }

    public void giveInfoTool(Player player) {
        clear(player);
        isInfo.add(player.getUniqueId());
        player.getInventory().setItem(player.getInventory().firstEmpty(), tools.get("info"));

    }

    private void clear(Player player) {
        isLocking.remove(player.getUniqueId());
        isUnlocking.remove(player.getUniqueId());
        isInfo.remove(player.getUniqueId());

        ItemStack[] items = tools.values();
        player.getInventory().removeItem(items);
    }
}

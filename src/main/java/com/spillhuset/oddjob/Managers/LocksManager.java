package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.SQL.LocksSQL;
import com.spillhuset.oddjob.Utils.LockUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

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
            lore.add("Right click to look at the locks signature!");
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        tools.put("info", item);
    }

    public void giveLockTool(Player player) {
        if (!isLocking.contains(player.getUniqueId())) {
            clear(player);
            isLocking.add(player.getUniqueId());
            player.getInventory().setItem(player.getInventory().firstEmpty(), tools.get("lock"));
        } else
            clear(player);
    }

    public void giveUnlockTool(Player player) {
        if (!isUnlocking.contains(player.getUniqueId())) {
            clear(player);
            isUnlocking.add(player.getUniqueId());
            player.getInventory().setItem(player.getInventory().firstEmpty(), tools.get("unlock"));
        } else
            clear(player);
    }

    public void giveInfoTool(Player player) {
        if (!isInfo.contains(player.getUniqueId())) {
            clear(player);
            isInfo.add(player.getUniqueId());
            player.getInventory().setItem(player.getInventory().firstEmpty(), tools.get("info"));
        } else
            clear(player);
    }

    public void clear(Player player) {
        isLocking.remove(player.getUniqueId());
        isUnlocking.remove(player.getUniqueId());
        isInfo.remove(player.getUniqueId());

        player.getInventory().removeItem(tools.get("lock"));
        player.getInventory().removeItem(tools.get("unlock"));
        player.getInventory().removeItem(tools.get("info"));
    }

    public Collection<ItemStack> getTools() {
        return tools.values();
    }

    public void lockBlock(Player player, Block clickedBlock) {
        LocksSQL.lockBlock(player.getUniqueId(), clickedBlock.getWorld().getUID(), clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ());
        MessageManager.locks_now_locked(player);
        clear(player);
    }

    public UUID isLocked(Block block) {
        Location location = block.getLocation();
        if (block.getState() instanceof Chest) {
            location = LockUtil.getChestLeft(block.getLocation());
        } else if (block.getState() instanceof Door) {
            location = LockUtil.getLowerLeftDoor(block.getLocation());
        }
        World world = location.getWorld();
        if (world != null)
            return LocksSQL.hasLock(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return null;
    }

    public void breakLock(Block block) {
        Location location = block.getLocation();
        if (block.getState() instanceof Chest) {
            location = LockUtil.getChestLeft(block.getLocation());
        } else if (block.getState() instanceof Door) {
            location = LockUtil.getLowerLeftDoor(block.getLocation());
        }
        World world = location.getWorld();
        if (world != null)
            LocksSQL.unlock(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}

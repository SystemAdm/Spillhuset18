package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.LocksSQL;
import com.spillhuset.oddjob.Utils.LockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocksManager {
    public LocksManager() {
        ItemStack itemStack = new ItemStack(Material.BLAZE_ROD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("Locking tool");
            List<String> lore = new ArrayList<>();
            lore.add("Right click on an object to lock it.");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        lockTool = itemStack;

        itemStack = new ItemStack(Material.BLAZE_ROD);
        itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("Adding tool");
            List<String> lore = new ArrayList<>();
            lore.add("Right click on an object to add it to lockable.");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        addTool = itemStack;

        itemStack = new ItemStack(Material.END_ROD);
        itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("Removing tool");
            List<String> lore = new ArrayList<>();
            lore.add("Right click on an object to remove it from lockable.");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        delTool = itemStack;

        itemStack = new ItemStack(Material.END_ROD);
        itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("Unlocking tool");
            List<String> lore = new ArrayList<>();
            lore.add("Right click on an object to unlock it.");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }
        unlockTool = itemStack;

        lockable = LocksSQL.getLockable();
    }

    private final List<Material> lockable;
    public final ItemStack lockTool;
    public final ItemStack unlockTool;
    public final ItemStack addTool;
    public final ItemStack delTool;

    public void giveInfoTool(Player player) {
    }

    public void giveAddTool(Player player) {
        Inventory inventory = player.getInventory();
        if (inventory.contains(addTool)) {
            inventory.remove(addTool);
        } else {
            inventory.addItem(addTool);
        }
    }

    public void giveDelTool(Player player) {
        Inventory inventory = player.getInventory();
        if (inventory.contains(delTool)) {
            inventory.remove(delTool);
        } else {
            inventory.addItem(delTool);
        }
    }

    public void giveLockTool(Player player) {
        Inventory inventory = player.getInventory();
        if (inventory.contains(lockTool)) {
            inventory.remove(lockTool);
        } else {
            inventory.addItem(lockTool);
        }
    }

    public void giveUnlockTool(Player player) {
        Inventory inventory = player.getInventory();
        if (inventory.contains(unlockTool)) {
            inventory.remove(unlockTool);
        } else {
            inventory.addItem(unlockTool);
        }
    }

    public boolean isLockable(Material type) {
        return lockable.contains(type);
    }

    public void lock(Player player, Block block) {
        Location location = block.getLocation();

        // Is it a chest?
        if (block.getType() == Material.CHEST) {
            location = LockUtil.getChestLeft(location);
        }

        // Is it a door?
        if (block.getBlockData() instanceof Door) {
            location = LockUtil.getLowerLeftDoor(location);
        }

        // Guilds activated?
        if (ConfigManager.getBoolean("plugin.guilds")) {
            int x = location.getChunk().getX();
            int z = location.getChunk().getZ();
            UUID world = location.getWorld().getUID();
            // TODO Check guild
        }

        // Is the block already locked?
        if (LocksSQL.hasLock(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ()) != null) {
            MessageManager.locks_already_locked(player);
            return;
        }

        // Lock it
        LocksSQL.lockBlock(player.getUniqueId(), location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        // Remove tool after use
        player.getInventory().remove(lockTool);

        MessageManager.locks_successfully_locked(player, location.getBlock().getType().name());
    }

    public void unlock(Player player, Block block) {
        Location location = block.getLocation();

        // Is it a chest?
        if (block.getType() == Material.CHEST) {
            location = LockUtil.getChestLeft(location);
        }

        // Is it a door?
        if (block.getBlockData() instanceof Door) {
            location = LockUtil.getLowerLeftDoor(location);
        }

        // Is it locked?
        UUID owner = LocksSQL.hasLock(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (owner == null) {
            MessageManager.locks_not_locked(player, location.getBlock().getType().name());
            return;
        }

        // Is it locked by you?
        if (owner.equals(player.getUniqueId())) {
            // Unlock it
            LocksSQL.unlock(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            // Remove tool after use
            player.getInventory().remove(unlockTool);
            MessageManager.locks_successfully_unlocked(player, location.getBlock().getType().name());
            return;
        }
        MessageManager.locks_owned(player, location.getBlock().getType().name());
    }

    public UUID isLocked(Location location) {
        return LocksSQL.hasLock(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public void breakLock(Player player, Location location) {
        LocksSQL.unlock(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        OddJob.getInstance().log("Lock: x=" + location.getBlockX() + "; y=" + location.getBlockY() + "; z=" + location.getBlockZ());
        MessageManager.locks_broken(player, location.getBlock().getType().name());
    }


    public void add(Player player, Block block) {
        LocksSQL.addBlock(block.getType());
        lockable.add(block.getType());
        MessageManager.locks_added(player, block.getType().name());
    }

    public void del(Player player, Block block) {
        LocksSQL.delBlock(block.getType());
        lockable.remove(block.getType());
        MessageManager.locks_deleted(player, block.getType().name());
    }
}

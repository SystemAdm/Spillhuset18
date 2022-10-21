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

    private List<Material> lockable;
    public final ItemStack lockTool;
    public final ItemStack unlockTool;

    public void giveInfoTool(Player player) {
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
        //return LocksSQL.isLockable(type);
        return lockable.contains(type);
    }

    public void lock(Player player, Block block) {
        Location location = block.getLocation();
        if (block.getType() == Material.CHEST) {
            OddJob.getInstance().log("chest");
            location = LockUtil.getChestLeft(location);
        }
        if (block.getBlockData() instanceof Door) {
            OddJob.getInstance().log("door");
            location = LockUtil.getLowerLeftDoor(location);
        }
        if (ConfigManager.getBoolean("plugin.guilds")) {
            int x = location.getChunk().getX();
            int z = location.getChunk().getZ();
            UUID world = location.getWorld().getUID();
            // TODO Check guild
        }
        if (LocksSQL.hasLock(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ()) != null) {
            MessageManager.locks_already_locked(player);
            return;
        }
        LocksSQL.lockBlock(player.getUniqueId(), location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        player.getInventory().remove(lockTool);
        MessageManager.locks_successfully_locked(player, location.getBlock().getType().name());
    }

    public void unlock(Player player, Block block) {
        Location location = block.getLocation();
        if (block.getType() == Material.CHEST) {
            OddJob.getInstance().log("chest");
            location = LockUtil.getChestLeft(location);
        }
        if (block.getBlockData() instanceof Door) {
            OddJob.getInstance().log("door");
            location = LockUtil.getLowerLeftDoor(location);
        }
        UUID owner = LocksSQL.hasLock(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (owner == null) {
            MessageManager.locks_not_locked(player, location.getBlock().getType().name());
            return;
        }
        if (owner.equals(player.getUniqueId())) {
            LocksSQL.unlock(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
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
        OddJob.getInstance().log("Lock: x="+location.getBlockX()+"; y="+location.getBlockY()+"; z="+location.getBlockZ());
        MessageManager.locks_broken(player, location.getBlock().getType().name());
    }
}

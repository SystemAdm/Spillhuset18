package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.LocksSQL;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.LockUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class OnPlayerInteractEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block clickedBlock = event.getClickedBlock();
        //TODO EquipmentSlot hand = event.getHand(); KEY!!!
        ItemStack item = event.getItem();

        if (clickedBlock != null) {
            OddJob.getInstance().log("locking " + clickedBlock.getX() + "," + clickedBlock.getY() + "," + clickedBlock.getZ() + ",");
            if (!LocksSQL.isLockable(clickedBlock.getType())) {
                return;
            }

            if (OddJob.getInstance().getLocksManager().getTools().contains(item) && action == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                OddJob.getInstance().log("Lefty with tool");
                Location location = clickedBlock.getLocation();
                World world = location.getWorld() != null ? location.getWorld() : player.getWorld();

                if (!world.getName().equalsIgnoreCase("world")) {
                    MessageManager.locks_only_world(player);
                    return;
                }

                BlockState blockState = clickedBlock.getState();
                if (blockState instanceof Chest) {
                    location = LockUtil.getChestLeft(location);
                    OddJob.getInstance().log("chest " + location.getX() + "," + location.getY() + "," + location.getZ() + ",");
                } else if (blockState instanceof Door) {
                    location = LockUtil.getLowerLeftDoor(location);
                }

                // Chunk owner
                Guild guildChunk = OddJob.getInstance().getGuildsManager().getGuildByChunk(location.getChunk());
                Guild guildPlayer = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
                if (guildChunk != null && guildChunk != guildPlayer) {
                    MessageManager.locks_inside_another_guild(player, guildChunk);
                    return;
                }

                // Already locked
                UUID owned = LocksSQL.hasLock(world.getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
                if (owned != null) {
                    if (owned.equals(player.getUniqueId())) {
                        MessageManager.locks_already_locked_you(player);
                    } else {
                        MessageManager.locks_already_locked_someone(player);
                    }
                    return;
                }
                OddJob.getInstance().log("locking " + clickedBlock.getX() + "," + clickedBlock.getY() + "," + clickedBlock.getZ() + ",");
                OddJob.getInstance().getLocksManager().lockBlock(player, location.getBlock());
                event.setCancelled(true);

            } else {
                if (OddJob.getInstance().getLocksManager().getTools().contains(item)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}


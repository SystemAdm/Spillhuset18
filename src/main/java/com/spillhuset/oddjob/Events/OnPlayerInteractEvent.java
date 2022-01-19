package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.LocksSQL;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.LockUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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
        boolean door = false;
        boolean chest = false;
        if (item != null && OddJob.getInstance().getLocksManager().getTools().contains(item)) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
        }

        if (clickedBlock != null) {
            OddJob.getInstance().log("locking " + clickedBlock.getX() + "," + clickedBlock.getY() + "," + clickedBlock.getZ() + ",");
            if (!LocksSQL.isLockable(clickedBlock.getType())) {
                return;
            }

            door = (clickedBlock.getBlockData() instanceof Door);
            chest = (clickedBlock.getState() instanceof Chest);

            Location location = clickedBlock.getLocation();
            World world = location.getWorld() != null ? location.getWorld() : player.getWorld();
            if (door) {
                location = LockUtil.getLowerLeftDoor(location);
            } else if (chest) {
                location = LockUtil.getChestLeft(location);
            }
            UUID owned = LocksSQL.hasLock(world.getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
            if (!OddJob.getInstance().getLocksManager().getTools().contains(item) && owned != null) {
                if (!owned.equals(player.getUniqueId())) {
                    event.setCancelled(true);
                    MessageManager.locks_owned(player);
                    return;
                }
            }


            if (OddJob.getInstance().getLocksManager().getTools().contains(item) && action == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                OddJob.getInstance().log("Lefty with tool");

                if (!world.getName().equalsIgnoreCase("world")) {
                    MessageManager.locks_only_world(player);
                    return;
                }

                BlockState blockState = clickedBlock.getState();
                if (blockState instanceof Chest) {
                    location = LockUtil.getChestLeft(location);
                    OddJob.getInstance().log("chest " + location.getX() + "," + location.getY() + "," + location.getZ() + ",");
                } else if (clickedBlock.getBlockData() instanceof Door) {
                    location = LockUtil.getLowerLeftDoor(location);
                }

                // Chunk owner
                Guild guildChunk = OddJob.getInstance().getGuildsManager().getGuildByChunk(location.getChunk());
                if (guildChunk != null) OddJob.getInstance().log("chunk:" + guildChunk.getName());
                Guild guildPlayer = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
                if (guildPlayer != null) OddJob.getInstance().log("player:" + guildPlayer.getName());
                if (guildChunk != null && guildChunk != guildPlayer) {
                    MessageManager.locks_inside_another_guild(player, guildChunk);
                    return;
                }

                // Already locked
                if (owned != null) {
                    if (owned.equals(player.getUniqueId())) {
                        MessageManager.locks_already_locked_you(player);
                    } else {
                        if (item != null && item.getType() == Material.MAP) {
                            MessageManager.locks_owned_by(player,OddJob.getInstance().getPlayerManager().get(owned));
                        } else
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


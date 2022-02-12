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
        Guild guildPlayer = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());

        boolean door = false;
        boolean chest = false;

        // Item is a tool! | Tools may not be placed out
        if (item != null && OddJob.getInstance().getLocksManager().getTools().contains(item)) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setCancelled(true);
        }

        // Player interacted with a block
        if (clickedBlock != null) {
            Guild guildClicked = OddJob.getInstance().getGuildsManager().getGuildByChunk(clickedBlock.getChunk());

            // Is the block lockable?
            if (!LocksSQL.isLockable(clickedBlock.getType())) {
                return;
            }

            door = (clickedBlock.getBlockData() instanceof Door);
            chest = (clickedBlock.getState() instanceof Chest);

            Location location = clickedBlock.getLocation();
            World world = location.getWorld() != null ? location.getWorld() : player.getWorld();

            if (door) {
                // Clicked block is a door
                location = LockUtil.getLowerLeftDoor(location);
            } else if (chest) {
                // Clicked block is a chest
                location = LockUtil.getChestLeft(location);
            }

            UUID owned = LocksSQL.hasLock(world.getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

            // Clicked block has an owner
            if (!OddJob.getInstance().getLocksManager().getTools().contains(item) && owned != null) {
                // Clicked block is not yours!
                if (!owned.equals(player.getUniqueId())) {
                    event.setCancelled(true);
                    MessageManager.locks_owned(player);
                    return;
                }
            }

            // Player right-clicked with a tool. | Using it.
            if (OddJob.getInstance().getLocksManager().getTools().contains(item) && action == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);

                // Is the tool usable in this world?
                if (!world.getName().equalsIgnoreCase("world")) {
                    MessageManager.locks_only_world(player);
                    return;
                }
/*
                BlockState blockState = clickedBlock.getState();
                if (blockState instanceof Chest) {
                    //
                    location = LockUtil.getChestLeft(location);
                } else if (clickedBlock.getBlockData() instanceof Door) {
                    location = LockUtil.getLowerLeftDoor(location);
                }
*/
                if (item != null && item.getType() == Material.MAP) {
                    if (owned == null) {
                        MessageManager.locks_nope(player);
                    } else {
                        MessageManager.locks_owned_by(player, OddJob.getInstance().getPlayerManager().get(owned));
                    }
                    event.setCancelled(true);
                    return;
                }

                // Check chunk owner | Not possible to lock inside another guild
                Guild guildChunk = OddJob.getInstance().getGuildsManager().getGuildByChunk(location.getChunk());
                if (guildChunk != null && guildChunk != guildPlayer) {
                    MessageManager.locks_inside_another_guild(player, guildChunk);
                    return;
                }

                // Is it already locked? | Block can not be locked twice
                if (owned != null) {
                    if (owned.equals(player.getUniqueId())) {
                        MessageManager.locks_already_locked_you(player);
                    } else {
                        if (item != null && item.getType() == Material.MAP) {
                            MessageManager.locks_owned_by(player, OddJob.getInstance().getPlayerManager().get(owned));
                        } else
                            MessageManager.locks_already_locked_someone(player);
                    }
                    return;
                }
                OddJob.getInstance().getLocksManager().lockBlock(player, location.getBlock());
                event.setCancelled(true);

            } else {
                // Not right-clicking, and not with a tool
                if (OddJob.getInstance().getLocksManager().getTools().contains(item)) {
                    // Using a tool
                    event.setCancelled(true);
                } else {
                    // Not using a tool
                    OddJob.getInstance().log("here");
                    // Block is locked, and lock is owned by you, and right-clicked, and is a door!
                    if (owned != null && owned.equals(player.getUniqueId()) && action == Action.RIGHT_CLICK_BLOCK && clickedBlock.getBlockData() instanceof Door) {
                        LockUtil.toggleDoor(clickedBlock, player, player.getLocation());
                        return;
                    }
                }
            }
        }
    }
}

/*
 * Guild member interact locked object
 * Other members interact
 * */
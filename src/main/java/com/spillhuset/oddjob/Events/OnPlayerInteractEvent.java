package com.spillhuset.oddjob.Events;

import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.Managers.PlayerManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.LocksSQL;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.LockUtil;
import com.spillhuset.oddjob.Utils.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
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

        if (player.isOp() || player.hasPermission("guilds.bypass")) {
            return;
        }

        if (clickedBlock != null && clickedBlock.getState() instanceof Sign sign) {
            Warp warp;
            String[] lines = sign.getLines();
            if (lines.length >= 3 && ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase("[warp]")) {
                warp = OddJob.getInstance().getWarpManager().get(sign.getLine(2).split(" ")[1]);
                if (warp != null) {
                    OddJob.getInstance().getWarpManager().teleport(player, warp.getName(), "");
                    return;
                }
            }
        }

        PlayerManager.setCombat(player.getUniqueId(),true);

        boolean door = false;
        boolean chest = false;

        if (item != null) {
            // There is an item being used
            if (OddJob.getInstance().getLocksManager().getTools().contains(item)) {
                // A tool in hand
                event.setUseItemInHand(Event.Result.DENY);
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setCancelled(true);
            }
        }

        if (clickedBlock != null) {
            // Player interacted with a block
            OddJob.getInstance().log("clicked");
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

            if (!OddJob.getInstance().getLocksManager().getTools().contains(item) && owned != null) {
                // Clicked with a tool && Clicked block has an owner
                OddJob.getInstance().log("owned");
                if (!owned.equals(player.getUniqueId())) {
                    // Clicked block is not yours!
                    event.setCancelled(true);
                    MessageManager.locks_owned(player);
                    return;
                }
            }

            // Player right-clicked with a tool. | Using it.
            if (OddJob.getInstance().getLocksManager().getTools().contains(item) && action == Action.RIGHT_CLICK_BLOCK) {
                OddJob.getInstance().log("right tool");
                event.setCancelled(true);

                // Is the tool usable in this world?
                if (!world.getName().equalsIgnoreCase("world")) {
                    MessageManager.locks_only_world(player);
                    return;
                }

                if (item != null) {
                    // Has an item in hand
                    if (item.equals(OddJob.getInstance().getLocksManager().tools.get("info"))) {
                        // Has the info-tool in hand
                        OddJob.getInstance().log("map");
                        if (owned == null) {
                            // Block is not owned
                            MessageManager.locks_nope(player);
                        } else {
                            // Block has an owner
                            MessageManager.locks_owned_by(player, OddJob.getInstance().getPlayerManager().get(owned));
                        }
                        return;
                    }
                }

                // Check chunk owner | Not possible to lock inside another guild
                Guild guildChunk = OddJob.getInstance().getGuildsManager().getGuildByChunk(location.getChunk());
                if (guildChunk != null) {
                    // Chunk is owned by a guild
                    if (guildPlayer == null || guildChunk != guildPlayer) {
                        // You are not associated with the same guild
                        OddJob.getInstance().log("others guild");
                        MessageManager.locks_inside_another_guild(player, guildChunk);
                        event.setCancelled(true);
                        return;
                    }
                }
                // Is it already locked? | Block can not be locked twice
                if (owned != null) {
                    // Has an owner
                    OddJob.getInstance().log("owned");
                    if (owned.equals(player.getUniqueId())) {
                        if (OddJob.getInstance().getLocksManager().tools.get("unlock").equals(item)) {
                            OddJob.getInstance().getLocksManager().breakLock(player, clickedBlock);
                            MessageManager.locks_broken(player);
                        } else {
                            // You own it
                            MessageManager.locks_already_locked_you(player);
                        }
                    } else {
                        // You do not own it
                        if (item != null && item.getType() == Material.MAP) {
                            // Showing info about lock
                            MessageManager.locks_owned_by(player, OddJob.getInstance().getPlayerManager().get(owned));
                        } else
                            MessageManager.locks_already_locked_someone(player);
                    }
                    return;
                } else {
                    OddJob.getInstance().log("not owned");
                    if (OddJob.getInstance().getLocksManager().tools.get("lock").equals(item)) {
                        OddJob.getInstance().log("locking");
                        OddJob.getInstance().getLocksManager().lockBlock(player, clickedBlock);
                    }
                }

                event.setCancelled(true);

            } else {
                // Not (right-clicking with a tool)
                if (OddJob.getInstance().getLocksManager().getTools().contains(item)) {
                    OddJob.getInstance().log("cancelled");
                    // Left-clicking with a tool
                    event.setCancelled(true);
                } else {
                    // Left-clicking without a tool
                    OddJob.getInstance().log("here");
                    // Block is locked, and lock is owned by you, and right-clicked, and is a door!
                    if (owned != null && owned.equals(player.getUniqueId()) && action == Action.RIGHT_CLICK_BLOCK && clickedBlock.getBlockData() instanceof Door) {
                        LockUtil.toggleDoor(clickedBlock, player, player.getLocation());
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
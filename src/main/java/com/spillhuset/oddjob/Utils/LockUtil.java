package com.spillhuset.oddjob.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.*;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Door.Hinge;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class LockUtil {
    public static Location getChestLeft(Location location) {
        BlockState blockState = location.getBlock().getState();
        Chest chest = (Chest) blockState;
        Inventory inventory = chest.getInventory();
        if (inventory instanceof DoubleChestInventory) {
            DoubleChest doubleChest = (DoubleChest) inventory.getHolder();
            if (doubleChest != null && doubleChest.getLeftSide() != null) {
                return doubleChest.getLeftSide().getInventory().getLocation();
            }
        }
        return chest.getLocation();
    }

    public static Location getLowerLeftDoor(Location location) {
        return getDoorLower(getDoorLeft(location));
    }

    public static Location getLowerRightDoor(Location location) {
        return getDoorLower(getDoorRight(location));
    }

    private static Location getDoorLower(Location location) {
        BlockData blockState = location.getBlock().getBlockData();
        if (!(blockState instanceof Door door)) {
            return location;
        }
        Half half = door.getHalf();
        if (half == Half.TOP) {
            location = location.getBlock().getRelative(BlockFace.DOWN).getLocation();
        }
        return location;
    }

    private static Location getDoorLeft(Location location) {
        BlockData blockState = location.getBlock().getBlockData();
        if (!(blockState instanceof Door door)) {
            return location;
        }
        Hinge hinge = door.getHinge();
        if (hinge == Hinge.RIGHT) {
            switch (door.getFacing()) {
                case EAST -> location = location.getBlock().getRelative(BlockFace.NORTH).getLocation();
                case WEST -> location = location.getBlock().getRelative(BlockFace.SOUTH).getLocation();
                case NORTH -> location = location.getBlock().getRelative(BlockFace.WEST).getLocation();
                case SOUTH -> location = location.getBlock().getRelative(BlockFace.EAST).getLocation();
            }
        }
        return location;
    }

    private static Location getDoorRight(Location location) {
        BlockData blockState = location.getBlock().getBlockData();
        if (!(blockState instanceof Door door)) {
            return location;
        }
        Hinge hinge = door.getHinge();
        if (hinge == Hinge.LEFT) {
            switch (door.getFacing()) {
                case EAST -> location = location.getBlock().getRelative(BlockFace.SOUTH).getLocation();
                case WEST -> location = location.getBlock().getRelative(BlockFace.NORTH).getLocation();
                case NORTH -> location = location.getBlock().getRelative(BlockFace.EAST).getLocation();
                case SOUTH -> location = location.getBlock().getRelative(BlockFace.WEST).getLocation();
            }
        }
        return location;
    }

    public static void toggleDoor(Block block, Player player, Location location) {
        Door door = (Door) block.getBlockData();
        boolean open = door.isOpen();
        Half half = door.getHalf();
        Hinge hinge = door.getHinge();
        boolean left = (hinge == Hinge.LEFT);
        boolean lower = (half == Half.BOTTOM);

        Block upperRight = null;
        Block upperLeft = null;
        Block lowerRight = null;
        Block lowerLeft = null;

        List<Block> doorBlades = new ArrayList<>();

        if (!lower) {
            if (!left) {
                upperRight = block;
                lowerRight = block.getRelative(BlockFace.DOWN);
                Block opposite = getLowerLeftDoor(block.getLocation()).getBlock();
                if (opposite != block) {
                    lowerLeft = opposite;
                    upperLeft = opposite.getRelative(BlockFace.UP);
                }
            } else {
                upperLeft = block;
                lowerLeft = block.getRelative(BlockFace.DOWN);
                Block opposite = getLowerRightDoor(block.getLocation()).getBlock();
                if (opposite != block) {
                    lowerRight = opposite;
                    upperRight = opposite.getRelative(BlockFace.UP);
                }
            }
        } else {
            if (!left) {
                lowerLeft = block;
                upperLeft = block.getRelative(BlockFace.UP);
                Block opposite = getLowerLeftDoor(block.getLocation()).getBlock();
                if (opposite != block) {
                    lowerRight = opposite;
                    upperRight = opposite.getRelative(BlockFace.UP);
                }
            } else {
                lowerRight = block;
                upperRight = block.getRelative(BlockFace.UP);
                Block opposite = getLowerRightDoor(block.getLocation()).getBlock();
                if (opposite != block) {
                    lowerLeft = opposite;
                    upperLeft = opposite.getRelative(BlockFace.UP);
                }
            }
        }

        if (lowerLeft != null) doorBlades.add(lowerLeft);
        if (upperLeft != null) doorBlades.add(upperLeft);
        if (lowerRight != null) doorBlades.add(lowerRight);
        if (upperRight != null) doorBlades.add(upperRight);

        for (Block bl : doorBlades) {
            if (bl == null || bl.getType() == Material.AIR) {
                continue;
            }
            if (bl instanceof Openable openable) {
                if (open) {
                    player.playSound(location, Sound.BLOCK_WOODEN_DOOR_CLOSE, 1f, 8f);
                } else {
                    player.playSound(location, Sound.BLOCK_WOODEN_DOOR_OPEN, 1f, 8f);
                }
                openable.setOpen(!open);
                bl.setBlockData(openable);
                bl.getState().update(true, true);
            }
        }
    }
}

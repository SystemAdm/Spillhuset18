package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.CountdownSpeed;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.WarpSQL;
import com.spillhuset.oddjob.Utils.Warp;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WarpManager {
    public static Location block_left;
    public static Location block_right;
    public static Material block_left_type;
    public static Material block_right_type;
    public HashMap<String, Long> joinCooldown = new HashMap<>();
    public HashMap<String, HashMap<String, Long>> cooldown = new HashMap<>();
    public boolean portalsActive = false;
    private boolean showBungeeMessage;
    private double throwback;
    private Sound portalSound;
    private int portalProtectionRadius;
    private boolean blockSpectatorMode;
    private int joinCooldownDelay;
    private boolean commandLog;
    private final Random random = new Random();
    private static YamlConfiguration portalsConfig;

    public static void fill() {
        int minX = Math.min(block_left.getBlockX(),block_right.getBlockX());
        int maxX = Math.max(block_left.getBlockX(),block_right.getBlockX());
        int minY = Math.min(block_left.getBlockY(),block_right.getBlockY());
        int maxY = Math.max(block_left.getBlockY(),block_right.getBlockY());
        int minZ = Math.min(block_left.getBlockZ(),block_right.getBlockZ());
        int maxZ = Math.max(block_left.getBlockZ(),block_right.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = new Location(block_left.getWorld(),x,y,z).getBlock();
                    if (block.getType() == Material.AIR) {
                        block.setType(Material.COBWEB);
                    }
                }
            }
        }
    }

    public static void remove() {
        int minX = Math.min(block_left.getBlockX(),block_right.getBlockX());
        int maxX = Math.max(block_left.getBlockX(),block_right.getBlockX());
        int minY = Math.min(block_left.getBlockY(),block_right.getBlockY());
        int maxY = Math.max(block_left.getBlockY(),block_right.getBlockY());
        int minZ = Math.min(block_left.getBlockZ(),block_right.getBlockZ());
        int maxZ = Math.max(block_left.getBlockZ(),block_right.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = new Location(block_left.getWorld(),x,y,z).getBlock();
                    if (block.getType() == Material.COBWEB) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    public static List<UUID> edit = new ArrayList<>();

    public static void removeTool(Player player) {
        edit.remove(player.getUniqueId());
        player.getInventory().remove(WarpManager.tool());
        player.updateInventory();
        remove();
        block_left = null;
        block_left_type = null;
        block_right = null;
        block_right_type = null;
    }

    public static String name(String name) {
        if (portalsConfig.contains())
    }

    private void loadPortals() {
        File portalsConfigFile = new File(OddJob.getInstance().getDataFolder(), "portals.yml");
        if (portalsConfigFile.exists()) {
            portalsConfig = new YamlConfiguration();
            try {
                portalsConfig.load(portalsConfigFile);
            } catch (IOException | InvalidConfigurationException ex) {
                ex.printStackTrace();
            }
        } else {
            portalsConfigFile.getParentFile().mkdirs();
        }
    }

    public static ItemStack tool() {
        ItemStack itemStack = new ItemStack(Material.REDSTONE_TORCH, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName("Portal tool");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.BLUE + "A tool, to make an portal frame");
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void add(Player player, String name, double cost, String passwd) {
        if (WarpSQL.exists(name)) {
            MessageManager.warps_exists(player, name);
            return;
        }

        WarpSQL.add(name, player.getLocation(), passwd, cost);
        MessageManager.warps_added(player, name);
    }

    public Warp get(String name) {
        return WarpSQL.get(name);
    }

    public void teleport(Player player, String name, String passwd) {
        Warp warp = WarpSQL.get(name);
        if (warp != null) {
            if (!warp.matchPwd(passwd)) {
                MessageManager.warps_error_password(player, name);
                return;
            }
            OddJob.getInstance().getTeleportManager().teleport(player, warp.getLocation(), Plugin.warps, CountdownSpeed.warp);
            return;
        }
        MessageManager.warps_not_exists(player, name);
    }

    public List<String> getList() {
        return WarpSQL.getNames();
    }

    public void del(Player player, String name) {
        if (!WarpSQL.exists(name)) {
            MessageManager.warps_not_exists(player, name);
            return;
        }

        WarpSQL.del(name);
        MessageManager.warps_deleted(player, name);
    }

    public void list(CommandSender sender) {
        List<Warp> warps = WarpSQL.list();
        List<String> w = new ArrayList<>();
        for (Warp warp : warps) {
            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.GOLD).append(warp.getName());
            if (warp.isProtected()) {
                sb.append(ChatColor.RED).append(" *").append(ChatColor.RESET);
            }
            if (warp.hasCost()) {
                sb.append(ChatColor.BLUE).append(" $").append(ChatColor.GRAY).append(warp.getCost()).append(ChatColor.RESET);
            }
            w.add(sb.toString());
        }
        MessageManager.warps_list(sender, w);
    }


    public void clear(Player player) {
        player.getInventory().remove(tool());
    }
}

package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Changed;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.PlayerSQL;
import com.spillhuset.oddjob.Utils.ChunkCord;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerManager {
    public static List<UUID> spiritIgnore = new ArrayList<>();
    public static HashMap<UUID, OddPlayer> oddPlayers = new HashMap<>();
    public static HashMap<UUID, ChunkCord> playerTracker = new HashMap<>();
    private final HashMap<UUID, BukkitTask> spiritTask = new HashMap<>();
    private final HashMap<UUID, Inventory> spiritInventories = new HashMap<>();
    private final HashMap<UUID, UUID> spiritOwner = new HashMap<>();

    public List<String> listString() {
        OddJob.getInstance().log("listing");
        List<String> list = new ArrayList<>();
        OddJob.getInstance().log("size:" + oddPlayers.size());
        for (OddPlayer oddPlayer : oddPlayers.values()) {
            OddJob.getInstance().log("item-player");
            list.add(oddPlayer.getName());
        }
        return list;
    }

    /**
     * Saving to database
     *
     * @param oddPlayer OddPlayer or Everyone
     */
    public void save(OddPlayer oddPlayer) {
        if (oddPlayer == null) {
            PlayerSQL.save(oddPlayers);
        } else {
            PlayerSQL.save(oddPlayer);
        }
    }

    /**
     * Loading a joining player to the memory
     *
     * @param uuid UUID of the Player
     * @param name String name of the Player
     */
    public void join(UUID uuid, String name) {
        OddPlayer oddPlayer = PlayerSQL.get(uuid);
        OddJob.getInstance().log("got-player");
        Long joined = System.currentTimeMillis() / 1000;
        OddJob.getInstance().log("joined " + joined);
        if (oddPlayer == null) {
            OddJob.getInstance().log("added");
            HistoryManager.add(uuid, Changed.first, "", String.valueOf(joined));
            oddPlayers.put(uuid, new OddPlayer(uuid, name, joined));
        } else {
            OddJob.getInstance().log("loaded");
            HistoryManager.add(uuid, Changed.name, oddPlayer.getName(), name);
            oddPlayers.put(uuid, new OddPlayer(uuid, name));
        }

        HistoryManager.add(uuid, Changed.joined, "", String.valueOf(joined));
    }

    /**
     * Loads a Player from the database
     *
     * @param uuid UUID of the Player
     */
    public void load(UUID uuid) {
        PlayerSQL.load(uuid);
    }

    /**
     * Loads a Player from the database
     *
     * @param name String name of the Player
     * @return Boolean
     */
    public boolean load(String name) {
        boolean load = false;
        OddPlayer oddPlayer = PlayerSQL.get(name);
        if (oddPlayer != null) {
            oddPlayers.put(oddPlayer.getUuid(), oddPlayer);
            load = true;
        }
        return load;
    }

    /**
     * Gets and loads a Player from the database
     *
     * @param name String name of the Player
     * @return OddPlayer
     */
    public OddPlayer get(String name) {
        for (UUID uuid : oddPlayers.keySet()) {
            OddPlayer oddPlayer = oddPlayers.get(uuid);
            if (oddPlayer.getName().equalsIgnoreCase(name)) {
                return oddPlayer;
            }
        }
        return PlayerSQL.get(name);
    }

    public OddPlayer get(UUID uuid) {
        OddPlayer oddPlayer = oddPlayers.get(uuid);
        if (oddPlayer != null) return oddPlayer;
        return PlayerSQL.get(uuid);
    }

    public void removeSpirit(UUID uuid) {
        BukkitTask task = spiritTask.get(uuid);
        if (task != null) task.cancel();
        spiritTask.remove(uuid);

        Entity entity = Bukkit.getEntity(uuid);
        if (entity != null) entity.remove();
        spiritInventories.remove(uuid);
        spiritOwner.remove(uuid);
    }

    public Inventory getSpiritInventory(UUID uuid) {
        return spiritInventories.get(uuid);
    }

    public void addSpirit(Entity entity, Player player) {
        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD,1);
        SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
        if (skullMeta != null) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            skullMeta.setDisplayName(ChatColor.DARK_PURPLE + "Head of " + player.getDisplayName());
            playerSkull.setItemMeta(skullMeta);
        }

        ArmorStand armorStand = (ArmorStand) entity;
        armorStand.setCustomName("The spirit of "+player.getDisplayName());
        armorStand.setCustomNameVisible(true);

        Inventory playerInventory = player.getInventory();
        if (armorStand.getEquipment() != null) {
            armorStand.getEquipment().setArmorContents(player.getInventory().getArmorContents());
            armorStand.getEquipment().setItemInMainHand(player.getInventory().getItemInMainHand());
            armorStand.getEquipment().setItemInOffHand(player.getInventory().getItemInOffHand());
        }

        Inventory inventory = Bukkit.createInventory(null,54,"Spirit of "+player.getDisplayName());
        if (playerInventory.getContents().length > 0) {
            inventory.setContents(player.getInventory().getContents().clone());
        }
        inventory.addItem(playerSkull);
        player.getInventory().clear();

        spiritInventories.put(entity.getUniqueId(),inventory);
        spiritOwner.put(entity.getUniqueId(),player.getUniqueId());
        spiritTask.put(entity.getUniqueId(),new BukkitRunnable() {
            int i = 1200;

            @Override
            public void run() {
                if (i == 1200) {
                    if (player.isOnline())
                        MessageManager.death1200(player);
                } else if (i == 600) {
                    if (player.isOnline())
                        MessageManager.death600(player);
                } else if (i == 60) {
                    if (player.isOnline())
                        MessageManager.death60(player);
                } else if (i < 10 && i > 0) {
                    if (player.isOnline())
                        MessageManager.death10(i, player);
                } else if (i < 1) {
                    OddJob.getInstance().getPlayerManager().replaceSpirit(entity, null);
                    if (player.isOnline())
                        MessageManager.death0(player);
                    cancel();
                }
                i--;
            }
        }.runTaskTimer(OddJob.getInstance(),20,20));
    }

    private void replaceSpirit(Entity spirit, UUID finder) {
        UUID owner = spiritOwner.get(spirit.getUniqueId());
        if (owner != finder) {
            Player player = Bukkit.getPlayer(owner);
            if (finder != null && player != null && player.isOnline()) {
                MessageManager.spiritFoundOther(OddJob.getInstance().getPlayerManager().get(owner));
            }
            if (finder != null) {
                MessageManager.spiritFound(OddJob.getInstance().getPlayerManager().get(finder),OddJob.getInstance().getPlayerManager().get(owner));
            }
        } else {
            MessageManager.spiritFoundSelf(OddJob.getInstance().getPlayerManager().get(finder));
        }

        spirit.remove();
        removeSpirit(spirit.getUniqueId());
    }
}

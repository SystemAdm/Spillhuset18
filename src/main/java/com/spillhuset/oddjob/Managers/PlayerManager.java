package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Changed;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.PlayerSQL;
import com.spillhuset.oddjob.Utils.ChunkCord;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class PlayerManager {
    public static List<UUID> spiritIgnore = new ArrayList<>();
    public static HashMap<UUID, OddPlayer> oddPlayers = new HashMap<>();
    public static HashMap<UUID, ChunkCord> playerTracker = new HashMap<>();
    private final HashMap<UUID, BukkitTask> spiritTask = new HashMap<>();
    private final HashMap<UUID, Inventory> spiritInventories = new HashMap<>();
    private final HashMap<UUID, UUID> spiritOwner = new HashMap<>();
    private static final HashMap<UUID, BukkitTask> combat = new HashMap<>();

    /**
     * Player UUID | Entity UUID
     */
    public HashMap<UUID, Entity> openSpirit = new HashMap<>();

    public static boolean isFrozen(UUID uniqueId) {
        //Todo
        return false;
    }

    public static boolean inCombat(UUID uniqueId) {
        return combat.get(uniqueId) != null;
    }
    public static void setCombat(UUID uniqueId, boolean set) {
        KeyedBossBar bossBar = Bukkit.getBossBar(NamespacedKey.minecraft("combat"));
        Player player = Bukkit.getPlayer(uniqueId);
        if (player == null) return;
        if (set) {
            if (bossBar == null) {
                bossBar = Bukkit.createBossBar(NamespacedKey.minecraft("combat"),"In combat, no teleport accepted", BarColor.RED, BarStyle.SOLID);
            }
            bossBar.setProgress(1d);
            bossBar.addPlayer(player);

            if (combat.get(uniqueId) != null) combat.get(uniqueId).cancel();
            combat.put(uniqueId,new BukkitRunnable() {
                @Override
                public void run() {
                    PlayerManager.setCombat(uniqueId, false);
                }
            }.runTaskLater(OddJob.getInstance(), 200));
        } else {
            if (bossBar != null) {
                bossBar.removePlayer(player);
            }
            combat.remove(uniqueId);
        }
    }

    public static OddPlayer getPlayerByName(String arg) {
        for (OddPlayer oddPlayer : oddPlayers.values()) {
            if (oddPlayer.getName().equalsIgnoreCase(arg)) {
                return oddPlayer;
            }
        }
        return null;
    }

    public List<String> listString() {
        List<String> list = new ArrayList<>();
        for (OddPlayer oddPlayer : oddPlayers.values()) {
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
        OddJob.getInstance().log("Removed task");

        Entity entity = Bukkit.getEntity(uuid);
        if (entity != null) entity.remove();
        spiritInventories.remove(uuid);
        OddJob.getInstance().log("Removed inventory");
        spiritOwner.remove(uuid);
        OddJob.getInstance().log("Removed owner");
    }

    public Inventory getSpiritInventory(UUID uuid) {
        return spiritInventories.get(uuid);
    }

    public void addSpirit(Entity entity, Player player) {
        /* Generating Skull */
        ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD, 1);

        /* Changing to a player skull */
        SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
        if (skullMeta != null) {
            // Sets what player to copy
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            // Sets name
            skullMeta.setDisplayName(ChatColor.DARK_PURPLE + "Head of " + player.getDisplayName());
            // Sets detail to the skull
            playerSkull.setItemMeta(skullMeta);

            OddJob.getInstance().log("Skull generated");
        }

        /* Entity */
        ArmorStand armorStand = (ArmorStand) entity;
        // Sets name
        armorStand.setCustomName("The spirit of " + player.getDisplayName());
        // Sets visibility
        armorStand.setCustomNameVisible(true);

        /* Armor Stand Inventory */
        Inventory playerInventory = player.getInventory();
        if (armorStand.getEquipment() != null) {
            // Sets helmet
            armorStand.getEquipment().setHelmet(playerSkull);
            // Sets armor
            armorStand.getEquipment().setArmorContents(player.getInventory().getArmorContents());
            // Sets main hand
            armorStand.getEquipment().setItemInMainHand(player.getInventory().getItemInMainHand());
            // Sets offhand
            armorStand.getEquipment().setItemInOffHand(player.getInventory().getItemInOffHand());

            OddJob.getInstance().log("armor generated");
        }

        /* Inventory */
        Inventory inventory = Bukkit.createInventory(null, 54, "Spirit of " + player.getDisplayName());
        if (playerInventory.getContents().length > 0) {
            /* Copies players inventory */
            inventory.setContents(player.getInventory().getContents().clone());

            OddJob.getInstance().log("inventory generated");
        }
        // Adds items to the inventory
        inventory.addItem(playerSkull);
        // Deletes the player inventory
        player.getInventory().clear();

        /* Saving the inventory */
        spiritInventories.put(entity.getUniqueId(), inventory);

        OddJob.getInstance().log("Inventory saved");

        /* Saving the armor stand */
        spiritOwner.put(entity.getUniqueId(), player.getUniqueId());

        OddJob.getInstance().log("Owner set");

        /* Creating the timer */
        spiritTask.put(entity.getUniqueId(), new BukkitRunnable() {
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
                    OddJob.getInstance().getPlayerManager().replaceSpirit(entity, null, false);
                    if (player.isOnline())
                        MessageManager.death0(player);
                    cancel();
                }
                i--;
            }
        }.runTaskTimer(OddJob.getInstance(), 20, 20));

        OddJob.getInstance().log("Timer is running");
    }

    /**
     *
     * @param spirit Entity ArmorStand
     * @param finder UUID finding player
     * @param looted boolean Looted
     */
    public void replaceSpirit(Entity spirit, UUID finder, boolean looted) {
        /* ID of the player who is dead */
        UUID owner = spiritOwner.get(spirit.getUniqueId());
        if (owner != null && owner != finder) {
            /* The dead player */
            Player player = Bukkit.getPlayer(owner);

            if (finder != null && player != null && player.isOnline()) {
                // You found another players spirit who is online
                MessageManager.spiritFoundOther(Bukkit.getPlayer(owner));
            }

            if (finder != null) {
                // You found another players spirit who is offline
                MessageManager.spiritFound(Bukkit.getPlayer(finder), player);
            }
        } else {
            MessageManager.spiritFoundSelf(Bukkit.getPlayer(finder), looted);
        }

        spirit.remove();
        OddJob.getInstance().log("Spirit gone!");
        removeSpirit(spirit.getUniqueId());
    }

    public List<OddPlayer> getAll() {
        return oddPlayers.values().stream().toList();
    }

    public void healOne(CommandSender sender) {
        Player player = (Player) sender;
        heal(player);
    }

    public void heal(Player player) {
        player.setHealth(20d);
    }

    public void healOne(String arg, CommandSender sender) {
        Player player = Bukkit.getPlayer(arg);
        if (player == null) {
            MessageManager.errors_find_player(Plugin.essentials, arg, sender);
        } else {
            heal(player);
        }
    }

    public void healAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            heal(player);
        }
    }

    public void healMany(String[] args, CommandSender sender) {
        for (String arg : args) {
            Player player = Bukkit.getPlayer(arg);
            if (player == null) {
                MessageManager.errors_find_player(Plugin.essentials, arg, sender);
            } else {
                heal(player);
            }
        }
    }

    public void feedOne(CommandSender sender) {
        Player player = (Player) sender;
        feed(player);
    }

    public void feed(Player player) {
        player.setFoodLevel(20);
    }

    public void feedOne(String arg, CommandSender sender) {
        Player player = Bukkit.getPlayer(arg);
        if (player == null) {
            MessageManager.errors_find_player(Plugin.essentials, arg, sender);
        } else{
        feed(player);}
    }

    public void feedAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            feed(player);
        }
    }

    public void feedMany(String[] args, CommandSender sender) {
        for (String arg : args) {
            Player player = Bukkit.getPlayer(arg);
            if (player == null) {
                MessageManager.errors_find_player(Plugin.essentials, arg, sender);
            } else {
                feed(player);
            }

        }
    }
}

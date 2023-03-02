package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.ScoreBoard;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.PlayerSQL;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.OddPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerManager {
    private final HashMap<UUID, UUID> spiritsOwner = new HashMap<>();
    private final HashMap<UUID, Inventory> spiritContents = new HashMap<>();
    private final HashMap<UUID, BukkitTask> spiritTimer = new HashMap<>();
    private final HashMap<UUID, OddPlayer> players;
    private Scoreboard scoreboard;
    private final HashMap<UUID, Scoreboard> scoreboards = new HashMap<>();
    private final HashMap<UUID, UUID> inside = new HashMap<>();
    private final HashMap<UUID, BukkitTask> combat = new HashMap<>();

    public PlayerManager() {
        players = PlayerSQL.load();
        load();
    }

    public OddPlayer get(UUID player) {
        return players.get(player);
    }

    public OddPlayer get(String player) {
        for (UUID uuid : players.keySet()) {
            if (players.get(uuid).getName().equals(player)) {
                return players.get(uuid);
            }
        }
        return null;
    }

    public void add(UUID player) {
        players.put(player, PlayerSQL.get(player));
    }

    public void incBoughtHomes(UUID uniqueId) {
        players.get(uniqueId).incBoughtHomes();
    }

    public void feedOne(CommandSender sender) {
        Player player = (Player) sender;
        player.setFoodLevel(20);
        MessageManager.essentials_feed_self(sender);
    }

    public void feedAll(CommandSender sender) {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setFoodLevel(20);
            MessageManager.essentials_feed(player);
            players.add(player.getName());
        }
        MessageManager.essentials_feed_all(sender, players);
    }

    public void feedMany(String[] args, CommandSender sender) {
        List<String> players = new ArrayList<>();
        for (String name : args) {
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                player.setFoodLevel(20);
                MessageManager.essentials_feed(player);
                players.add(player.getName());
            }
        }
        MessageManager.essentials_feed_many(sender, players);
    }

    public void feedOne(String arg, CommandSender sender) {
        Player player = Bukkit.getPlayer(arg);
        if (player != null) {
            player.setFoodLevel(20);
            MessageManager.essentials_feed(player);
            MessageManager.essentials_feed_one(sender, player.getName());
        }
    }

    public void healOne(CommandSender sender) {
        Player player = (Player) sender;
        player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
        MessageManager.essentials_heal_self(sender);
    }

    public void healAll(CommandSender sender) {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != null) {
                player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                MessageManager.essentials_healed(player);
                players.add(player.getName());
            }
        }
        MessageManager.essentials_heal_all(sender, players);
    }

    public void healOne(String arg, CommandSender sender) {
        Player player = Bukkit.getPlayer(arg);
        if (player != null) {
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
            MessageManager.essentials_healed(player);
            MessageManager.essentials_heal_one(sender, player.getName());
        }
    }

    public void healMany(String[] args, CommandSender sender) {
        List<String> players = new ArrayList<>();
        for (String name : args) {
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue());
                MessageManager.essentials_healed(player);
                players.add(player.getName());
            }
        }
        MessageManager.essentials_heal_many(sender, players);
    }

    public Collection<OddPlayer> getAll() {
        return players.values();
    }

    public List<String> listAll() {
        List<String> list = new ArrayList<>();
        for (OddPlayer oddPlayer : players.values()) {
            list.add(oddPlayer.getName());
        }
        return list;
    }

    public void setScoreboard(UUID uniqueId, Scoreboard scoreboard) {
        scoreboards.put(uniqueId, scoreboard);
    }

    public Scoreboard getScoreboard(UUID uniqueId) {
        return scoreboards.remove(uniqueId);
    }

    public UUID getInside(UUID player) {
        return inside.get(player);
    }

    public void setInside(UUID player, UUID guild) {
        inside.put(player, guild);
    }

    public void combat(Entity entity) {
        if (entity instanceof Player player) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "In combat"));
            if (inCombat(player.getUniqueId())) {
                combat.get(player.getUniqueId()).cancel();
                combat.remove(player.getUniqueId());
            }
            AtomicInteger i = new AtomicInteger(20);
            combat.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(OddJob.getInstance(), () -> {
                i.getAndDecrement();

                if (i.get() == 0)
                    removeCombat(player.getUniqueId());
                else
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "In combat"));
            }, 20, 20));
        }
    }

    private void removeCombat(UUID uniqueId) {
        combat.get(uniqueId).cancel();
        combat.remove(uniqueId);

        Player player = Bukkit.getPlayer(uniqueId);
        if (player != null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Out of combat"));
        }
    }

    public void setSpirit(Player player, ArmorStand armorStand, Inventory inventory) {
        UUID uuid = armorStand.getUniqueId();
        UUID owner = player.getUniqueId();
        armorStand.setCustomName("Spirit of " + player.getName());
        armorStand.setCustomNameVisible(true);
        OddJob.getInstance().log("Spirit created " + uuid);
        UUID world = player.getWorld().getUID();
        var ref = new Object() {
            int i = 1200;
        };
        spiritsOwner.put(uuid, owner);
        spiritContents.put(uuid, inventory);
        MessageManager.death_timer(owner, ref.i);
        player.setCompassTarget(player.getLocation());
        spiritTimer.put(uuid, Bukkit.getScheduler().runTaskTimer(OddJob.getInstance(), () -> {
            armorStand.setCustomName(ChatColor.GREEN + "Spirit of " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + " leaving in " + ChatColor.GRAY + ref.i);
            {
                if (ref.i == 600) {
                    MessageManager.death_timer(owner, ref.i);
                }
                if (ref.i == 300) {
                    MessageManager.death_timer(owner, ref.i);
                }
                if (ref.i == 60) {
                    MessageManager.death_timer(owner, ref.i);
                }
                if (ref.i <= 10) {
                    MessageManager.death_timer(owner, ref.i);
                }
                if (ref.i == 0) {
                    removeArmorstand(uuid);
                }
                ref.i--;
            }
        }, 20, 20));
    }

    public UUID removeArmorstand(UUID armorStandUUID) {
        Entity armor = Bukkit.getEntity(armorStandUUID);
        if (armor == null) return null;
        World world = armor.getWorld();
        UUID owner = spiritsOwner.get(armorStandUUID);

        // removing and cancelling
        spiritTimer.get(armorStandUUID).cancel();
        spiritTimer.remove(armorStandUUID);
        spiritContents.remove(armorStandUUID);
        spiritsOwner.remove(armorStandUUID);

        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.ARMOR_STAND) {
                ArmorStand armorStand = (ArmorStand) entity;
                if (armorStand.getCustomName() != null && ChatColor.stripColor(armorStand.getCustomName()).startsWith("Spirit of ")) {
                    armorStand.remove();
                    return owner;
                }
            }
        }

        return owner;
    }


    public HashMap<UUID, UUID> getSpirits() {
        return spiritsOwner;
    }

    public void removeInventory(Inventory inventory, Player player) {
        for (UUID uuid : spiritContents.keySet()) {
            if (spiritContents.get(uuid).equals(inventory)) {
                if (spiritsOwner.get(uuid).equals(player.getUniqueId())) {
                    MessageManager.death_lucky(player);
                } else {
                    Player owner = Bukkit.getPlayer(spiritsOwner.get(uuid));
                    MessageManager.death_bastard(player, owner);
                }
                removeArmorstand(uuid);
            }
        }
    }

    public void openArmorstand(UUID armorstand, Player player) {
        player.openInventory(spiritContents.get(armorstand));
    }

    public void load() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        if (scoreboardManager == null) return;
        scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("Main", Criteria.DUMMY, "SPILLHUSET");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.getScore(ChatColor.GRAY + ">> Online").setScore(15);

        Team onlineCounter = scoreboard.registerNewTeam("onlineCounter");
        onlineCounter.addEntry(ChatColor.BLACK + "" + ChatColor.WHITE);

        if (Bukkit.getOnlinePlayers().size() == 0) {
            onlineCounter.setPrefix("0/" + Bukkit.getMaxPlayers());
        } else {
            onlineCounter.setPrefix(Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
        }

        objective.getScore("").setScore(14);
    }

    public void setScoreboard(Player player, ScoreBoard scoreboard) {
        if (player == null) return;
        switch (scoreboard) {
            case Server -> player.setScoreboard(this.scoreboard);
            case Player -> player.setScoreboard(getScoreboard(player.getUniqueId()));
            case Guild -> {
                Guild guild = OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId());
                if (guild != null) {
                    UUID uuid = guild.getUuid();
                    player.setScoreboard(getScoreboard(uuid));
                }
            }

            default -> player.setScoreboard(emptyScoreboard());
        }
    }

    public Scoreboard emptyScoreboard() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        return scoreboardManager != null ? scoreboardManager.getNewScoreboard() : null;
    }

    public boolean inCombat(UUID uniqueId) {
        return combat.containsKey(uniqueId);
    }
}

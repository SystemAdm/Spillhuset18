package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.Enums.GameState;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Game {
    /**
     * The state of the game
     */
    private GameState gameState;

    /**
     * Unique ID of the game
     */
    private UUID uuid;

    /**
     * Name of the game
     */
    private String name;

    /**
     * List of players who want to join the game
     */
    private ArrayList<UUID> queue = new ArrayList<>();

    /**
     * List of players alive
     */
    private ArrayList<UUID> alive = new ArrayList<>();

    /**
     * List of player inside the game
     */
    private ArrayList<UUID> inside = new ArrayList<>();

    /**
     * Location of the lobby, where you enter before and after a game
     */
    private Location lobby;

    /**
     * A list of all spawns set
     */
    private ArrayList<Location> spawns = new ArrayList<>();

    /**
     * Total number of teams
     */
    private int teams;

    /**
     * Number of players per team
     */
    private int ppt;

    /**
     * Maximum number of player in a game
     */
    private int max;

    /**
     * Minimum number of players to start the game
     */
    private int min;

    /**
     * List of points per player
     */
    private HashMap<UUID, Integer> pointsPlayers = new HashMap<>();

    /**
     * List of points per team
     */
    private HashMap<Team, Integer> pointsTeams = new HashMap<>();

    /**
     * Maximum points
     */
    private int point;

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLobby() {
        return lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public int getTeams() {
        return teams;
    }

    public void setTeams(int teams) {
        this.teams = teams;
    }

    public int getPpt() {
        return ppt;
    }

    public void setPpt(int ppt) {
        this.ppt = ppt;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public Game(UUID uuid, String name, int max, int min, int point, int ppt, int teams, Player player) {
        this.uuid = uuid;
        this.name = name;
        this.max = max;
        this.min = min;
        this.point = point;
        this.ppt = ppt;
        this.teams = teams;
        lobby = player.getLocation();
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean in(UUID player) {
        return inside.contains(player);
    }
}

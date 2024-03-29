package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.Enums.ScoreBoard;
import com.spillhuset.oddjob.Managers.ConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OddPlayer {
    private final UUID uuid;
    private final String name;
    private Long joined;
    private List<UUID> whiteList = new ArrayList<>();
    private List<UUID> blackList = new ArrayList<>();
    private boolean denyTpa;
    private boolean denyTrade;
    private ScoreBoard scoreBoard = ScoreBoard.None;
    private int boughtHomes = 0;
    private final int defaultHomes = ConfigManager.isSet("homes.default") ? ConfigManager.getInt("homes.default") : 1;

    /**
     * Created from join
     *
     * @param uuid UUID
     * @param name String
     */
    public OddPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    /**
     * Created from first join
     *
     * @param uuid   UUID
     * @param name   String
     * @param joined Long
     */
    public OddPlayer(UUID uuid, String name, Long joined) {
        this.uuid = uuid;
        this.name = name;
        this.joined = joined;
    }

    /**
     * Creation from Database
     *
     * @param uuid       UUID
     * @param name       String
     * @param joined     Long
     * @param whiteList  List
     * @param blackList  List
     * @param denyTpa    Boolean
     * @param denyTrade  Boolean
     * @param scoreBoard ScoreBoard
     */
    public OddPlayer(UUID uuid, String name, long joined, List<UUID> whiteList, List<UUID> blackList, boolean denyTpa, boolean denyTrade, ScoreBoard scoreBoard, int boughtHomes) {
        this.uuid = uuid;
        this.name = name;
        this.joined = joined;
        this.whiteList = whiteList;
        this.blackList = blackList;
        this.denyTpa = denyTpa;
        this.denyTrade = denyTrade;
        this.scoreBoard = scoreBoard;
        this.boughtHomes = boughtHomes;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public List<UUID> getWhiteList() {
        return this.whiteList;
    }

    public List<UUID> getBlackList() {
        return this.blackList;
    }

    public boolean getDenyTpa() {
        return this.denyTpa;
    }

    public boolean getDenyTrade() {
        return this.denyTrade;
    }

    public int getMaxHomes() {
        return defaultHomes + boughtHomes;
    }

    public ScoreBoard getScoreBoard() {
        return this.scoreBoard;
    }

    public String getDisplayName() {
        return this.name;
    }

    public int getBoughtHomes() {
        return this.boughtHomes;
    }

    public void incBoughtHomes() {
        boughtHomes++;
    }

    public void removeBlackList(UUID uuid) {
        blackList.remove(uuid);
    }
}


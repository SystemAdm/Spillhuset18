package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Enums.ScoreBoard;
import com.spillhuset.oddjob.Managers.MySQLManager;
import com.spillhuset.oddjob.Utils.OddPlayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerSQL extends MySQLManager {
    public static void save(HashMap<UUID, OddPlayer> oddPlayers) {
        for (OddPlayer oddPlayer : oddPlayers.values()) {
            save(oddPlayer);
        }
    }

    public static boolean save(OddPlayer oddPlayer) {
        boolean set = false;
        String whiteList = assemble(oddPlayer.getWhiteList());
        String blackList = assemble(oddPlayer.getBlackList());
        int denyTpa = (oddPlayer.getDenyTpa() ? 1 : 0);
        int denyTrade = (oddPlayer.getDenyTrade() ? 1 : 0);
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE mine_players SET " +
                    "whitelist = ?, " +
                    "blacklist = ?, " +
                    "denytpa = ?, " +
                    "denytrade = ?, " +
                    "scoreboard = ?, " +
                    "maxhomes = ? " +
                    "WHERE uuid = ?");
            preparedStatement.setString(1, whiteList);
            preparedStatement.setString(2, blackList);
            preparedStatement.setInt(3, denyTpa);
            preparedStatement.setInt(4, denyTrade);
            preparedStatement.setString(5, oddPlayer.getScoreBoard().name());
            preparedStatement.setInt(6, oddPlayer.getMaxHomes());
            preparedStatement.executeUpdate();
            set = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return set;
    }

    public static void load(UUID uuid) {
        get(uuid);
    }

    public static OddPlayer get(String name) {
        UUID uuid = null;
        long joined = 0;
        List<UUID> whiteList = new ArrayList<>();
        List<UUID> blackList = new ArrayList<>();
        boolean denyTpa = false;
        boolean denyTrade = false;
        int maxHomes = 0;
        ScoreBoard scoreBoard = ScoreBoard.None;

        OddPlayer oddPlayer = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM mine_players WHERE name = ?");
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                uuid = UUID.fromString(resultSet.getString("uuid"));
                joined = resultSet.getLong("joined");
                whiteList = deAssemble(resultSet.getString("whitelist"));
                blackList = deAssemble(resultSet.getString("blacklist"));
                denyTpa = resultSet.getInt("denytpa") == 1;
                denyTrade = resultSet.getInt("denytrade") == 1;
            }
            oddPlayer = new OddPlayer(uuid, name, joined, whiteList, blackList, denyTpa, denyTrade, maxHomes, scoreBoard);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return oddPlayer;
    }

    public static OddPlayer get(UUID uuid) {
        String name = "";
        long joined = 0;
        List<UUID> whiteList = new ArrayList<>();
        List<UUID> blackList = new ArrayList<>();
        boolean denyTpa = false;
        boolean denyTrade = false;
        int maxHomes = 0;
        ScoreBoard scoreBoard = ScoreBoard.None;

        OddPlayer oddPlayer = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM mine_players WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                name = resultSet.getString("name");
                joined = resultSet.getLong("joined");
                whiteList = deAssemble(resultSet.getString("whitelist"));
                blackList = deAssemble(resultSet.getString("blacklist"));
                denyTpa = resultSet.getInt("denytpa") == 1;
                denyTrade = resultSet.getInt("denytrade") == 1;
            }
            oddPlayer = new OddPlayer(uuid, name, joined, whiteList, blackList, denyTpa, denyTrade, maxHomes, scoreBoard);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return oddPlayer;
    }
}

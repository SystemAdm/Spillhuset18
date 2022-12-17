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

    public static void save(OddPlayer oddPlayer) {
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
                    "boughthomes = ? " +
                    "WHERE uuid = ?");
            preparedStatement.setString(1, whiteList);
            preparedStatement.setString(2, blackList);
            preparedStatement.setInt(3, denyTpa);
            preparedStatement.setInt(4, denyTrade);
            preparedStatement.setString(5, oddPlayer.getScoreBoard().name());
            preparedStatement.setInt(6, oddPlayer.getBoughtHomes());
            preparedStatement.setString(7, oddPlayer.getUuid().toString());
            preparedStatement.executeUpdate();
            set = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
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
        int boughtHomes = 0;
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
                boughtHomes = resultSet.getInt("boughthomes");
            }
            oddPlayer = new OddPlayer(uuid, name, joined, whiteList, blackList, denyTpa, denyTrade, scoreBoard, boughtHomes);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return oddPlayer;
    }

    public static HashMap<UUID, OddPlayer> load() {
        HashMap<UUID, OddPlayer> players = new HashMap<>();

        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM mine_players ");
            resultSet = preparedStatement.executeQuery();

            String name;
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                name = resultSet.getString("name");
                long joined = resultSet.getLong("joined");
                List<UUID> whiteList = deAssemble(resultSet.getString("whitelist"));
                List<UUID> blackList = deAssemble(resultSet.getString("blacklist"));
                boolean denyTpa = resultSet.getInt("denytpa") == 1;
                boolean denyTrade = resultSet.getInt("denytrade") == 1;
                int boughtHomes = resultSet.getInt("boughthomes");
                ScoreBoard scoreBoard = ScoreBoard.None;
                players.put(uuid, new OddPlayer(uuid, name, joined, whiteList, blackList, denyTpa, denyTrade, scoreBoard, boughtHomes));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return players;
    }

    public static OddPlayer get(UUID uuid) {
        String name = "";
        long joined = 0;
        List<UUID> whiteList = new ArrayList<>();
        List<UUID> blackList = new ArrayList<>();
        boolean denyTpa = false;
        boolean denyTrade = false;
        ScoreBoard scoreBoard = ScoreBoard.None;
        int boughtHomes = 0;

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
                boughtHomes = resultSet.getInt("boughthomes");
            }
            oddPlayer = new OddPlayer(uuid, name, joined, whiteList, blackList, denyTpa, denyTrade, scoreBoard, boughtHomes);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return oddPlayer;
    }

    public static List<String> getList() {
        List<String> list = new ArrayList<>();

        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM mine_players");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                list.add(resultSet.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return list;
    }
}

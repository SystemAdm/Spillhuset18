package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;

import java.sql.SQLException;
import java.util.UUID;

public class RequestSql extends MySQLManager {

    public static void request(UUID player, UUID guild) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_pending` (`player`, `uuid`) VALUES (?,?) ");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, guild.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static boolean hasRequest(UUID player, UUID guild) {
        boolean ret = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_pending` WHERE `player` = ? AND `uuid` = ?");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, guild.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ret = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }

    public static boolean hasInvite(UUID guild, UUID player) {
        boolean ret = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_invites` WHERE `player` = ? AND `uuid` = ?");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, guild.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ret = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }

    public static void invite(UUID guild, UUID player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_invites` (`player`, `uuid`) VALUES (?,?) ");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, guild.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
}

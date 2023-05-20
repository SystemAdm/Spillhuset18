package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;
import com.spillhuset.oddjob.Utils.Arena;

import java.sql.SQLException;
import java.util.UUID;

public class ArenaSQL extends MySQLManager {
    public static void save(Arena arena) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_arenas` WHERE `uuid` = ?");
            preparedStatement.setString(1, arena.getUuid().toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_arenas` SET `name` = ?,`world` = ? WHERE `uuid` = ?");
                preparedStatement.setString(1, arena.getName());
                preparedStatement.setString(2, arena.getWorld().toString());
                preparedStatement.setString(3, arena.getUuid().toString());
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_arenas` (`uuid`,`name`,`world`) VALUES (?,?,?)");
                preparedStatement.setString(1, arena.getUuid().toString());
                preparedStatement.setString(2, arena.getName());
                preparedStatement.setString(3, arena.getWorld().toString());
                preparedStatement.executeQuery();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void delete(UUID uuid) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_arenas` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }
}

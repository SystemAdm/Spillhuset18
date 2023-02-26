package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;
import com.spillhuset.oddjob.Utils.Arena;

import java.sql.SQLException;

public class ArenaSQL extends MySQLManager {
    public static void save(Arena arena) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_arenas` WHERE `name` = ?");
            preparedStatement.setString(1, arena.getName());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_arenas` SET `name` = ? WHERE `name` = ?");
                preparedStatement.setString(1, arena.getName());
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_arenas` (`name`) VALUES (?)");
                preparedStatement.setString(1, arena.getName());
                preparedStatement.executeQuery();
            }
        } catch (SQLException ignored) {
        }
    }

}

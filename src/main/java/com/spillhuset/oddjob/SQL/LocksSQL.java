package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocksSQL extends MySQLManager {
    @Nullable
    public static UUID hasLock(UUID world, int blockX, int blockY, int blockZ) {
        UUID owner = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_secured_blocks` WHERE `world` = ? AND `x` = ? AND `y` = ? AND `z` = ?");
            preparedStatement.setString(1, world.toString());
            preparedStatement.setInt(2, blockX);
            preparedStatement.setInt(3, blockY);
            preparedStatement.setInt(4, blockZ);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                owner = UUID.fromString(resultSet.getString("uuid"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return owner;
    }

    public static boolean isLockable(Material type) {
        boolean lockable = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_lockable_materials` WHERE `name` = ? AND `value` = ?");
            preparedStatement.setString(1, type.name());
            preparedStatement.setInt(2, 1);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                lockable = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return lockable;
    }

    public static void lockBlock(UUID player, UUID world, int x, int y, int z) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_secured_blocks` (uuid, world, x, y, z) VALUES (?,?,?,?,?)");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2, world.toString());
            preparedStatement.setInt(3, x);
            preparedStatement.setInt(4, y);
            preparedStatement.setInt(5, z);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void unlock(UUID world, int x, int y, int z) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_secured_blocks` WHERE world = ? AND x = ? AND y = ? AND z = ?");
            preparedStatement.setString(1, world.toString());
            preparedStatement.setInt(2, x);
            preparedStatement.setInt(3, y);
            preparedStatement.setInt(4, z);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static List<Material> getLockable() {
        List<Material> list = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_lockable_materials` WHERE `value` = 1");
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                list.add(Material.valueOf(resultSet.getString("name")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return list;
    }

    public static void addBlock(Material type) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_lockable_materials` (name, value) VALUES (?,1)");
            preparedStatement.setString(1,type.name());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static void delBlock(Material type) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_lockable_materials` WHERE `name` = ?");
            preparedStatement.setString(1,type.name());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
}

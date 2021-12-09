package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class HomesSQL extends MySQLManager {
    public static boolean exists(@Nonnull String name, @Nonnull UUID target) {
        boolean has = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_homes` WHERE `server` = ? AND `uuid` = ? AND `name` = ?");
            preparedStatement.setString(1, OddJob.getInstance().getConfig().getString("server_unique_id"));
            preparedStatement.setString(2, target.toString());
            preparedStatement.setString(3, name);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                has = true;
            }
        } catch (SQLException ignored) {
        } finally {
            close();
        }
        return has;
    }

    public static List<String> getList(@Nonnull UUID target) {
        List<String> list = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_homes` WHERE `server` = ? AND `uuid` = ?");
            preparedStatement.setString(1, OddJob.getInstance().getConfig().getString("server_unique_id"));
            preparedStatement.setString(2, target.toString());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                list.add(resultSet.getString("name"));
            }
        } catch (SQLException ignored) {
        } finally {
            close();
        }
        return list;
    }

    public static boolean add(@Nonnull UUID uuid, @Nonnull Location location, @Nonnull String name) {
        boolean done = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_homes` (`uuid`, `server`, `name`, `world`, `x`, `y`, `z`, `yaw`, `pitch`) VALUES (?,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, OddJob.getInstance().getConfig().getString("server_unique_id"));
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, Objects.requireNonNull(location.getWorld()).getUID().toString());
            preparedStatement.setDouble(5, location.getX());
            preparedStatement.setDouble(6, location.getY());
            preparedStatement.setDouble(7, location.getZ());
            preparedStatement.setFloat(8, location.getYaw());
            preparedStatement.setFloat(9, location.getPitch());
            preparedStatement.executeUpdate();
            done = true;
        } catch (SQLException ignored) {
        } finally {
            close();
        }
        return done;
    }

    public static Location get(@Nonnull OddPlayer player, @Nonnull String name) {
        Location home = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_homes` WHERE `server` = ? AND `uuid` = ? AND `name` = ?");
            preparedStatement.setString(1, OddJob.getInstance().getConfig().getString("server_unique_id"));
            preparedStatement.setString(2, player.getUuid().toString());
            preparedStatement.setString(3, name);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                home = new Location(
                        Bukkit.getWorld(UUID.fromString(resultSet.getString("world"))),
                        resultSet.getDouble("x"),
                        resultSet.getDouble("y"),
                        resultSet.getDouble("z"),
                        resultSet.getFloat("yaw"),
                        resultSet.getFloat("pitch"));
            }
        } catch (SQLException ignored) {

        } finally {
            close();
        }
        return home;
    }

    public static boolean delete(UUID uuid, String name) {
        boolean affected = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_homes` WHERE `server` = ? AND `uuid` = ? AND `name` = ?");
            preparedStatement.setString(1, OddJob.getInstance().getConfig().getString("server_unique_id"));
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.setString(3, name);
            affected = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return affected;
    }

    public static boolean rename(UUID uuid, String nameOld, String nameNew) {
        boolean affected = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_homes` SET `name` = ? WHERE `server` = ? AND `uuid` = ? AND `name` = ?");
            preparedStatement.setString(1,nameNew);
            preparedStatement.setString(2,server);
            preparedStatement.setString(3,uuid.toString());
            preparedStatement.setString(4,nameOld);
            affected = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return affected;
    }

    public static boolean change(UUID uuid, String name, Location location) {
        boolean affected = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_homes` SET `world` = ?,`x` = ?,`y` = ?,`z` = ?,`yaw` = ?,`pitch` = ? WHERE `server` = ? AND `uuid` = ? AND `name` = ?");
            preparedStatement.setString(1,location.getWorld().getUID().toString());
            preparedStatement.setDouble(2,location.getX());
            preparedStatement.setDouble(3,location.getY());
            preparedStatement.setDouble(4,location.getZ());
            preparedStatement.setFloat(5,location.getYaw());
            preparedStatement.setFloat(6,location.getPitch());
            preparedStatement.setString(7,server);
            preparedStatement.setString(8,uuid.toString());
            preparedStatement.setString(9,name);
            affected = preparedStatement.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return affected;
    }
}

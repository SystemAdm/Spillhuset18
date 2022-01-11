package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;
import com.spillhuset.oddjob.Utils.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpSQL extends MySQLManager {

    public static boolean exists(String name) {
        boolean exists = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_warps` WHERE `name` = ? AND `server` = ?");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, server);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                exists = true;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        } finally {
            close();
        }

        return exists;
    }

    public static void add(String name, Location location, String passwd, double cost) {
        World world = location.getWorld();
        UUID worldUuid = (world != null) ? world.getUID() : null;
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        try {
            UUID uuid = UUID.randomUUID();
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_warps` (`uuid`,`name`,`server`,`passwd`,`cost`,`world`,`x`,`y`,`z`,`yaw`,`pitch`) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, server);
            preparedStatement.setString(4, passwd);
            preparedStatement.setDouble(5, cost);
            preparedStatement.setString(6, (worldUuid != null) ? worldUuid.toString() : null);
            preparedStatement.setDouble(7, x);
            preparedStatement.setDouble(8, y);
            preparedStatement.setDouble(9, z);
            preparedStatement.setFloat(10, yaw);
            preparedStatement.setFloat(11, pitch);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static Warp get(String name) {
        Location location = null;
        World world = null;
        UUID worldUuid = null;
        double x = 0;
        double y = 0;
        double z = 0;
        float yaw = 0;
        float pitch = 0;
        String passwd = "";
        UUID uuid = null;
        double cost = 0d;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_warps` WHERE `server` = ? AND `name` = ?");
            preparedStatement.setString(1, server);
            preparedStatement.setString(2, name);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                worldUuid = UUID.fromString(resultSet.getString("world"));
                world = Bukkit.getWorld(worldUuid);
                x = resultSet.getDouble("x");
                y = resultSet.getDouble("y");
                z = resultSet.getDouble("z");
                yaw = resultSet.getFloat("yaw");
                pitch = resultSet.getFloat("pitch");
                location = new Location(world,x,y,z,yaw,pitch);
                cost = resultSet.getDouble("cost");
                passwd = resultSet.getString("passwd");
                uuid = UUID.fromString(resultSet.getString("uuid"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return new Warp(uuid,location,cost,passwd,name);
    }

    public static List<String> getNames() {
        List<String> list = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `name` FROM `mine_warps` WHERE `server` = ?");
            preparedStatement.setString(1,server);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                list.add(resultSet.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return list;
    }

    public static void del(String name) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_warps` WHERE `server` = ? AND `name` = ?");
            preparedStatement.setString(1,server);
            preparedStatement.setString(2,name);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static List<Warp> list() {
        List<Warp> warps = new ArrayList<>();
        for (String string: getNames()){
            warps.add(get(string));
        }
        return warps;
    }
}

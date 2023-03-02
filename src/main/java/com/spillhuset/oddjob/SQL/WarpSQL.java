package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Portal;
import com.spillhuset.oddjob.Utils.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
        Warp warp = null;
        Location location;
        World world;
        UUID worldUuid;
        double x;
        double y;
        double z;
        float yaw;
        float pitch;
        String passwd;
        UUID uuid;
        double cost;
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
                location = new Location(world, x, y, z, yaw, pitch);
                cost = resultSet.getDouble("cost");
                passwd = resultSet.getString("passwd");
                uuid = UUID.fromString(resultSet.getString("uuid"));

                warp = new Warp(uuid, location, cost, passwd, name);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return warp;
    }

    public static List<String> getNames() {
        List<String> list = new ArrayList<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `name` FROM `mine_warps` WHERE `server` = ?");
            preparedStatement.setString(1, server);
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

    public static void del(String name) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_warps` WHERE `server` = ? AND `name` = ?");
            preparedStatement.setString(1, server);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static List<Warp> list() {
        List<Warp> warps = new ArrayList<>();
        for (String string : getNames()) {
            warps.add(get(string));
        }
        return warps;
    }


    public static HashMap<UUID, Warp> load() {
        HashMap<UUID, Warp> warps = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_warps` WHERE `server` = ?");
            preparedStatement.setString(1, server);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String name;
                UUID worldUuid = UUID.fromString(resultSet.getString("world"));
                World world = Bukkit.getWorld(worldUuid);
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                float yaw = resultSet.getFloat("yaw");
                float pitch = resultSet.getFloat("pitch");
                Location location = new Location(world, x, y, z, yaw, pitch);
                double cost = resultSet.getDouble("cost");
                String passwd = resultSet.getString("passwd");
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                name = resultSet.getString("name");
                warps.put(uuid, new Warp(uuid, location, cost, passwd, name));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().log("Loaded " + warps.size() + " warps");
        return warps;
    }

    public static void save(HashMap<UUID, Warp> warps) {


        try {
            for (UUID uuid : warps.keySet()) {
                Location location = warps.get(uuid).getLocation();
                String name = warps.get(uuid).getName();
                String passwd = warps.get(uuid).getPasswd();
                double cost = warps.get(uuid).getCost();
                World world = location.getWorld();
                UUID worldUuid = (world != null) ? world.getUID() : null;
                double x = location.getX();
                double y = location.getY();
                double z = location.getZ();
                float yaw = location.getYaw();
                float pitch = location.getPitch();
                connect();
                preparedStatement = connection.prepareStatement("UPDATE `mine_warps` SET `name`=?,`server`=?,`passwd`=?,`cost`=?,`world`=?,`x`=?,`y`=?,`z`=?,`yaw`=?,`pitch`=? WHERE `uuid` = ?");
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, server);
                preparedStatement.setString(3, passwd);
                preparedStatement.setDouble(4, cost);
                preparedStatement.setString(5, (worldUuid != null) ? worldUuid.toString() : null);
                preparedStatement.setDouble(6, x);
                preparedStatement.setDouble(7, y);
                preparedStatement.setDouble(8, z);
                preparedStatement.setFloat(9, yaw);
                preparedStatement.setFloat(10, pitch);
                preparedStatement.setString(11, uuid.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void savePortal(Location location, Portal portal) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        World world = location.getWorld();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_warps_portals` WHERE `world` = ? AND `x`= ? AND `y`= ? AND `z`= ? AND `server` =?");
            preparedStatement.setString(1, world.getUID().toString());
            preparedStatement.setInt(2, x);
            preparedStatement.setInt(3, y);
            preparedStatement.setInt(4, z);
            preparedStatement.setString(5, server);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_warps_portals` SET `warp` = ?, `name` = ? WHERE `world` = ? AND `x`= ? AND `y`= ? AND `z`= ? AND `server` =?");
                preparedStatement.setString(1, portal.getWarp().toString());
                preparedStatement.setString(2, portal.getName());
                preparedStatement.setString(3, world.getUID().toString());
                preparedStatement.setInt(4, x);
                preparedStatement.setInt(5, y);
                preparedStatement.setInt(6, z);
                preparedStatement.setString(7, server);
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_warps_portals` (`warp`,`world`,`x`,`y`,`z`,`server`,`name`) VALUES (?,?,?,?,?,?,?)");
                preparedStatement.setString(1, portal.getWarp().toString());
                preparedStatement.setString(2, world.getUID().toString());
                preparedStatement.setInt(3, x);
                preparedStatement.setInt(4, y);
                preparedStatement.setInt(5, z);
                preparedStatement.setString(6, server);
                preparedStatement.setString(7, portal.getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static HashMap<Location, Portal> loadPortals() {
        HashMap<Location, Portal> portals = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_warps_portals` WHERE `server` = ?");
            preparedStatement.setString(1, server);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                if (world != null) {
                    Location location = new Location(world, resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                    Portal portal = new Portal(resultSet.getString("name"), UUID.fromString(resultSet.getString("warp")));
                    portals.put(location, portal);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return portals;
    }
}

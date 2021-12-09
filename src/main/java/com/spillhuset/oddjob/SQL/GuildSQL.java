package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.Managers.MySQLManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Guild;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class GuildSQL extends MySQLManager {

    /**
     *
     * @param uuid UUID of Guild
     * @return Integer of saved guilds
     */
    public static int loadGuild(UUID uuid) {
        int affected = 0;
        String search = (uuid != null) ? uuid.toString() : "%";
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds` WHERE `uuid` LIKE ? AND `server` = ?");
            preparedStatement.setString(1, search);
            preparedStatement.setString(2, server);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                affected++;

                // Set spawn
                Location spawn = null;
                String world = resultSet.getString("world");
                if (!world.isEmpty()) {
                    spawn = new Location(Bukkit.getWorld(UUID.fromString(world)), resultSet.getDouble("x"), resultSet.getDouble("y"), resultSet.getDouble("z"), resultSet.getFloat("yaw"), resultSet.getFloat("pitch"));
                }

                // Translate
                boolean friendlyFire = resultSet.getInt("friendly_fire") == 1;
                boolean invitedOnly = resultSet.getInt("invited_only") == 1;
                boolean open = resultSet.getInt("open") == 1;
                boolean spawnMobs = resultSet.getInt("spawnmobs") == 1;
                Role permissionKick = Role.valueOf(resultSet.getString("permission_kick"));
                Role permissionInvite = Role.valueOf(resultSet.getString("permission_invite"));
                Zone zone = Zone.valueOf(resultSet.getString("zone"));
                int maxClaims = resultSet.getInt("maxclaims");
                String name = resultSet.getString("name");
                uuid = UUID.fromString(resultSet.getString("uuid"));

                OddJob.getInstance().getGuildsManager().loadGuild(new Guild(uuid, name, zone, spawn, maxClaims, spawnMobs, open, invitedOnly, friendlyFire, permissionKick, permissionInvite));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return affected;
    }

    /**
     *
     * @param guild Guild to save
     * @return Boolean if success
     */
    public static boolean save(Guild guild) {
        boolean affected = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds` WHERE `uuid` = ? AND `server` =?");
            preparedStatement.setString(1, guild.getUuid().toString());
            preparedStatement.setString(2, server);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_guilds` SET `name` = ?, `zone` = ?, `invited_only`=?,`friendly_fire`=?,`permission_kick`=?,`permission_invite`=?,`open`=?,`maxclaims`=?,`spawnmobs`=?,`world`=?,`x`=?,`y`=?,`z`=?,`yaw`=?,`pitch`=? WHERE `uuid` = ? AND `server` = ?");
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds` (`name`, `zone` , `invited_only`,`friendly_fire`,`permission_kick`,`permission_invite`,`open`,`maxclaims`,`spawnmobs`,`world`,`x`,`y`,`z`,`yaw`,`pitch` ,`uuid` ,`server`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            }
            preparedStatement.setString(1, guild.getName());
            preparedStatement.setString(2, guild.getZone().name());
            preparedStatement.setInt(3, guild.isInvited_only() ? 1 : 0);
            preparedStatement.setInt(4, guild.isFriendlyFire() ? 1 : 0);
            preparedStatement.setString(5, guild.getPermissionKick().name());
            preparedStatement.setString(6, guild.getPermissionInvite().name());
            preparedStatement.setInt(7, guild.isOpen() ? 1 : 0);
            preparedStatement.setInt(8, guild.getMaxClaims());
            preparedStatement.setInt(9, guild.isSpawnMobs() ? 1 : 0);
            preparedStatement.setString(10, (guild.getSpawn() != null && guild.getSpawn().getWorld() != null) ? guild.getSpawn().getWorld().getUID().toString() : "");
            preparedStatement.setDouble(11, (guild.getSpawn() != null) ? guild.getSpawn().getX() : 0);
            preparedStatement.setDouble(12, (guild.getSpawn() != null) ? guild.getSpawn().getY() : 0);
            preparedStatement.setDouble(13, (guild.getSpawn() != null) ? guild.getSpawn().getZ() : 0);
            preparedStatement.setFloat(14, (guild.getSpawn() != null) ? guild.getSpawn().getYaw() : 0);
            preparedStatement.setFloat(15, (guild.getSpawn() != null) ? guild.getSpawn().getPitch() : 0);
            preparedStatement.setString(16, guild.getUuid().toString());
            preparedStatement.setString(17, server);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return affected;
    }

    /**
     *
     * @return HashMap of Chunk and Guild UUID
     */
    public static HashMap<Chunk, UUID> loadChunks() {
        HashMap<Chunk, UUID> chunks = new HashMap<>();

        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_chunks` WHERE `server` = ?");
            preparedStatement.setString(1, server);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                if (world != null) {
                    Chunk chunk = world.getChunkAt(resultSet.getInt("x"), resultSet.getInt("z"));
                    UUID guild = UUID.fromString(resultSet.getString("uuid"));
                    chunks.put(chunk, guild);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return chunks;
    }

    /**
     *
     * @param chunks HashMap of Chunk and Guild UUID
     * @return Integer | number of saved chunks
     */
    public static int saveChunks(HashMap<Chunk, UUID> chunks) {
        int affected = 0;
        for (Chunk chunk : chunks.keySet()) {
            try {
                connect();
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_chunks` WHERE `server` = ? AND `world` = ? AND `x` = ? AND `z` = ?");
                preparedStatement.setString(1,server);
                preparedStatement.setString(2,chunk.getWorld().getUID().toString());
                preparedStatement.setInt(3,chunk.getX());
                preparedStatement.setInt(4,chunk.getZ());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_guilds_chunks` SET `uuid` = ? WHERE `server` = ? AND `world` = ? AND `x` = ? AND `z` = ?");
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_chunks` (`uuid`,`server`,`world`,`x`,`z`) VALUES (?,?,?,?,?)");
                }
                preparedStatement.setString(1,chunks.get(chunk).toString());
                preparedStatement.setString(2,server);
                preparedStatement.setString(3,chunk.getWorld().getUID().toString());
                preparedStatement.setInt(4,chunk.getX());
                preparedStatement.setInt(5,chunk.getZ());
                affected += preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
        }

        return affected;
    }

    /**
     *
     * @return HashMap of Player UUID and Guild UUID
     */
    public static HashMap<UUID, UUID> loadMembers() {
        HashMap<UUID,UUID> members = new HashMap<>();
        try{
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid`,`player` FROM `mine_guilds_members`");
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                UUID guild = UUID.fromString(resultSet.getString("uuid"));
                if (OddJob.getInstance().getGuildsManager().getGuilds().containsKey(guild)) {
                    members.put(UUID.fromString(resultSet.getString("player")),guild);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return members;
    }

    /**
     *
     * @return HashMap of Player UUID and Role
     */
    public static HashMap<UUID, Role> loadRoles() {
        HashMap<UUID,Role> roles = new HashMap<>();
        try{
            connect();
            preparedStatement = connection.prepareStatement("SELECT `player`,`role` FROM `mine_guilds_members`");
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                UUID player = UUID.fromString(resultSet.getString("uuid"));
                Role role = Role.valueOf(resultSet.getString("role"));
                if (OddJob.getInstance().getGuildsManager().getMembers().containsKey(player)) {
                    roles.put(player,role);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return roles;
    }

    public static HashMap<UUID, UUID> loadPending() {
        HashMap<UUID,UUID> pending = new HashMap<>();
        try{
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid`,`player` FROM `mine_guilds_pendings`");
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                UUID guild = UUID.fromString(resultSet.getString("uuid"));
                UUID player = UUID.fromString(resultSet.getString("player"));
                if (OddJob.getInstance().getGuildsManager().getGuilds().containsKey(guild)) {
                    pending.put(player,guild);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return pending;
    }

    public static HashMap<UUID, UUID> loadInvites() {
        HashMap<UUID,UUID> invites = new HashMap<>();
        try{
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid`,`player` FROM `mine_guilds_invites`");
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                UUID guild = UUID.fromString(resultSet.getString("uuid"));
                UUID player = UUID.fromString(resultSet.getString("player"));
                if (OddJob.getInstance().getGuildsManager().getGuilds().containsKey(guild)) {
                    invites.put(player,guild);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return invites;
    }
}

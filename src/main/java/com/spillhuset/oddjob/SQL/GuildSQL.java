package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.Managers.MySQLManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Cords;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GuildSQL extends MySQLManager {

    /**
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

                // Translate
                boolean friendlyFire = resultSet.getInt("friendly_fire") == 1;
                boolean invitedOnly = resultSet.getInt("invited_only") == 1;
                boolean open = resultSet.getInt("open") == 1;
                boolean spawnMobs = resultSet.getInt("spawnmobs") == 1;
                Role permissionKick = Role.valueOf(resultSet.getString("permission_kick"));
                Role permissionInvite = Role.valueOf(resultSet.getString("permission_invite"));
                Zone zone = Zone.valueOf(resultSet.getString("zone"));
                int boughtClaims = resultSet.getInt("boughtClaims");
                int boughtHomes = resultSet.getInt("boughtHomes");
                String name = resultSet.getString("name");
                uuid = UUID.fromString(resultSet.getString("uuid"));
                int boughtOutposts = resultSet.getInt("boughtOutposts");
                int usedOutposts = resultSet.getInt("usedOutposts");
                boolean waterFlow = resultSet.getInt("waterflow") == 1;
                boolean lavaFlow = resultSet.getInt("lavaflow") == 1;

                OddJob.getInstance().getGuildsManager().loadGuild(new Guild(uuid, name, zone, boughtClaims, boughtHomes, spawnMobs, open, invitedOnly, friendlyFire, permissionKick, permissionInvite, boughtOutposts, usedOutposts,waterFlow,lavaFlow));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return affected;
    }

    /**
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
                preparedStatement = connection.prepareStatement("UPDATE `mine_guilds` SET `name` = ?, `zone` = ?, `invited_only`=?,`friendly_fire`=?,`permission_kick`=?,`permission_invite`=?,`open`=?,`boughtClaims`=?,`boughtHomes`=?,`spawnmobs`=?,`boughtOutposts` = ?,`usedOutposts` = ?,`waterflow` = ?,`lavaflow` = ? WHERE `uuid` = ? AND `server` = ?");
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds` (`name`, `zone` , `invited_only`,`friendly_fire`,`permission_kick`,`permission_invite`,`open`,`boughtClaims`,`boughtHomes`,`spawnmobs` ,`boughtOutposts`,`usedOutposts`,`waterflow`,`lavaflow`,`uuid` ,`server`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            }
            preparedStatement.setString(1, guild.getName());
            preparedStatement.setString(2, guild.getZone().name());
            preparedStatement.setInt(3, guild.isInvited_only() ? 1 : 0);
            preparedStatement.setInt(4, guild.isFriendlyFire() ? 1 : 0);
            preparedStatement.setString(5, guild.getPermissionKick().name());
            preparedStatement.setString(6, guild.getPermissionInvite().name());
            preparedStatement.setInt(7, guild.isOpen() ? 1 : 0);
            preparedStatement.setInt(8, guild.getBoughtClaims());
            preparedStatement.setInt(9, guild.getBoughtHomes());
            preparedStatement.setInt(10, guild.isSpawnMobs() ? 1 : 0);
            preparedStatement.setInt(11, guild.getBoughtOutposts());
            preparedStatement.setInt(12, guild.getUsedOutposts());
            preparedStatement.setInt(13,guild.getFlowWater()?1:0);
            preparedStatement.setInt(14,guild.getFlowLava()?1:0);
            preparedStatement.setString(15, guild.getUuid().toString());
            preparedStatement.setString(16, server);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return affected;
    }

    /**
     * @return HashMap of Chunk and Guild UUID
     */
    public static HashMap<Cords, UUID> loadChunks() {
        HashMap<Cords, UUID> chunks = new HashMap<>();

        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_chunks` WHERE `server` = ?");
            preparedStatement.setString(1, server);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                World world = Bukkit.getWorld(UUID.fromString(resultSet.getString("world")));
                if (world != null) {
                    UUID guild = UUID.fromString(resultSet.getString("uuid"));
                    Cords chunk = new Cords(resultSet.getInt("x"), resultSet.getInt("z"), world.getUID(), guild);
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
     * @param chunks HashMap of Chunk and Guild UUID
     * @return Integer | number of saved chunks
     */
    public static int saveChunks(HashMap<Cords, UUID> chunks) {
        int affected = 0;
        for (Cords chunk : chunks.keySet()) {
            try {
                connect();
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_chunks` WHERE `server` = ? AND `world` = ? AND `x` = ? AND `z` = ?");
                preparedStatement.setString(1, server);
                preparedStatement.setString(2, chunk.getWorld().toString());
                preparedStatement.setInt(3, chunk.getX());
                preparedStatement.setInt(4, chunk.getZ());
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_guilds_chunks` SET `uuid` = ? WHERE `server` = ? AND `world` = ? AND `x` = ? AND `z` = ?");
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_chunks` (`uuid`,`server`,`world`,`x`,`z`) VALUES (?,?,?,?,?)");
                }
                preparedStatement.setString(1, chunks.get(chunk).toString());
                preparedStatement.setString(2, server);
                preparedStatement.setString(3, chunk.getWorld().toString());
                preparedStatement.setInt(4, chunk.getX());
                preparedStatement.setInt(5, chunk.getZ());
                affected += preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
        }

        return affected;
    }

    public static void loadMembersRoles() {
        HashMap<UUID, UUID> members = new HashMap<>();
        HashMap<UUID, Role> roles = new HashMap<>();
        HashMap<UUID, Guild> guilds = OddJob.getInstance().getGuildsManager().getGuilds();
        if (guilds.isEmpty()) {
            OddJob.getInstance().getGuildsManager().setRoles(roles);
            OddJob.getInstance().getGuildsManager().setMembers(members);
            return;
        }
        for (UUID uuid : OddJob.getInstance().getGuildsManager().getGuilds().keySet()) {
            try {
                connect();
                preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_members` WHERE `uuid` = ?");
                preparedStatement.setString(1, uuid.toString());
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    UUID member = UUID.fromString(resultSet.getString("player"));
                    members.put(member, uuid);
                    Role role = Role.valueOf(resultSet.getString("role"));
                    roles.put(member, role);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                close();
            }
        }
        OddJob.getInstance().getGuildsManager().setRoles(roles);
        OddJob.getInstance().getGuildsManager().setMembers(members);
    }

    public static int saveMembersRoles(HashMap<UUID, UUID> members, HashMap<UUID, Role> roles) {
        int i = 0;
        for (UUID member : members.keySet()) {
            UUID guild = members.get(member);
            if (roles.containsKey(member)) {
                try {
                    connect();
                    preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_members` WHERE `player` = ? AND `uuid` = ?");
                    preparedStatement.setString(1, member.toString());
                    preparedStatement.setString(2, guild.toString());
                    resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        OddJob.getInstance().log("update");
                        preparedStatement = connection.prepareStatement("UPDATE `mine_guilds_members` SET `role` = ? WHERE `player` = ? AND `uuid` = ? ");
                    } else {
                        OddJob.getInstance().log("insert");
                        preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_members` (`role`,`player`,`uuid`) VALUES (?,?,?)");
                    }
                    preparedStatement.setString(1, roles.get(member).name());
                    preparedStatement.setString(2, member.toString());
                    preparedStatement.setString(3, guild.toString());
                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    close();
                }
                i++;
            }
        }
        return i;
    }

    /**
     * @return List of opposite type UUID
     */
    public static List<UUID> getInvite(UUID uuid, boolean guild) {
        List<UUID> list = new ArrayList<>();

        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_invites` WHERE " + ((guild) ? "uuid" : "player") + " = ?");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UUID target = UUID.fromString(resultSet.getString(((guild) ? "player" : "uuid")));
                list.add(target);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return list;
    }

    /**
     * @param uuid  UUID target
     * @param guild Boolean guild
     * @return List
     */
    public static List<UUID> getPending(UUID uuid, boolean guild) {
        List<UUID> list = new ArrayList<>();

        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_pending` WHERE " + ((guild) ? "uuid" : "player") + " = ?");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String string = resultSet.getString((guild ? "player" : "uuid"));
                UUID target = UUID.fromString(string);
                if (!string.isEmpty())
                    list.add(target);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return list;
    }

    public static void invite(UUID player, UUID guild) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_invites` (`uuid`,`player`) VALUES (?,?)");
            preparedStatement.setString(1, guild.toString());
            preparedStatement.setString(2, player.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void removeInvites(UUID player,UUID guild) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_invites` WHERE `player` = ? AND `uuid` = ?");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2,guild.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void removePending(UUID player,UUID guild) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_pending` WHERE `player` = ? AND `uuid` = ?");
            preparedStatement.setString(1, player.toString());
            preparedStatement.setString(2,guild.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void setPending(Guild guild, OddPlayer oddPlayer) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_pending` (`player`,`uuid`) VALUES (?,?)");
            preparedStatement.setString(1, oddPlayer.getUuid().toString());
            preparedStatement.setString(2, guild.getUuid().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void disband(UUID uuid) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_members` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_chunks` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_invites` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_pending` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void removeChunk(Guild guild, Cords chunk) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_chunks` WHERE `uuid` = ? AND `server` = ? AND `world` = ? AND `x` = ? AND `z` = ?");
            preparedStatement.setString(1, guild.getUuid().toString());
            preparedStatement.setString(2, server);
            preparedStatement.setString(3, chunk.getWorld().toString());
            preparedStatement.setInt(4, chunk.getX());
            preparedStatement.setInt(5, chunk.getZ());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void saveChunk(Chunk chunk, Guild guild) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_guilds_chunks` WHERE `server` = ? AND `world` = ? AND `x` = ? AND `z` = ?");
            preparedStatement.setString(1, server);
            preparedStatement.setString(2, chunk.getWorld().getUID().toString());
            preparedStatement.setInt(3, chunk.getX());
            preparedStatement.setInt(4, chunk.getZ());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_guilds_chunks` SET `uuid` = ? WHERE `server` = ? AND `world` = ? AND `x` = ? AND `z` = ?");
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_guilds_chunks` (`uuid`,`server`,`world`,`x`,`z`) VALUES (?,?,?,?,?)");
            }
            preparedStatement.setString(1, guild.getUuid().toString());
            preparedStatement.setString(2, server);
            preparedStatement.setString(3, chunk.getWorld().getUID().toString());
            preparedStatement.setInt(4, chunk.getX());
            preparedStatement.setInt(5, chunk.getZ());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static String guildChunk(String string, int x, int z) {
        String s = null;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_guilds_chunks` WHERE `world` = ? AND `x` = ? AND `z` = ?");
            preparedStatement.setString(1, string);
            preparedStatement.setInt(2, x);
            preparedStatement.setInt(3, z);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                s = resultSet.getString("uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return s;
    }

    public static void removeMember(UUID player) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("DELETE FROM `mine_guilds_members` WHERE `player` = ?");
            preparedStatement.setString(1, player.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }
}

package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class GuildsManager extends Managers {
    /**
     * UUID Guild, Guild
     */
    private HashMap<UUID, Guild> guilds;
    /**
     * UUID Player, Role
     */
    private HashMap<UUID, Role> roles;
    /**
     * UUID Player, UUID Guild
     */
    private HashMap<UUID, UUID> members;
    /**
     * Cords, UUID Guild
     */
    private HashMap<Cords, UUID> chunks;

    public void buyClaims(CommandSender sender) {

    }

    public void join(Player player, String name) {
        Guild guild = getGuildByName(name);
        if (guild.isOpen()) {
            join(player, guild);
            return;
        }
        if (guild.isInvited_only()) {
            MessageManager.guilds_invited_only(player, name);
            return;
        }
        request(player, guild);
    }

    private void request(Player player, Guild guild) {
        Request.request(player, guild);
    }

    private void join(Player player, Guild guild) {
        members.put(player.getUniqueId(), guild.getUuid());
        roles.put(guild.getUuid(), Role.Members);
        MessageManager.guilds_welcome(player, guild);
    }

    public HashMap<UUID, Guild> getGuilds() {
        return null;
    }

    public void leave(Player player) {
    }

    public void loadGuild(Guild guild) {
    }

    public void info(Player player) {
    }

    public HashMap<UUID, Role> getRoles() {
        return roles;
    }

    public void info(CommandSender sender, String arg) {
    }

    public List<String> list() {
        List<String> list = new ArrayList<>();
        for (UUID uuid : guilds.keySet()) {
            list.add(guilds.get(uuid).getName());
        }
        return list;
    }

    public Guild getGuildByMember(UUID player) {
        return getGuildByUUID(members.get(player));
    }

    private Guild getGuildByUUID(UUID guild) {
        return guilds.get(guild);
    }

    public Guild getGuildByCords(int x, int z, World world) {
        for (Cords cords : chunks.keySet()) {
            if (cords.getWorld().equals(world.getUID()) && cords.getX() == x && cords.getZ() == z) {
                return getGuildByUUID(cords.getGuild());
            }
        }
        return null;
    }

    public void acceptInvite(Player player, String s) {
    }

    public void acceptPending() {
    }

    public void buyHomes(CommandSender sender) {
    }

    public void claim(Player player, boolean outpost) {
    }

    public Guild getGuildByName(String name) {
        for (Guild guild : guilds.values()) {
            if (guild.getName().equalsIgnoreCase(name)) {
                return guild;
            }
        }
        return null;
    }

    public void claim(Player player, Guild guild) {
    }

    public void create(Player player, String name) {
        if (getGuildByName(name) != null) {
            MessageManager.guilds_exists(player, name);
            return;
        }
        if (getGuildByMember(player.getUniqueId()) != null) {
            MessageManager.guilds_already_associated(player, getGuildByMember(player.getUniqueId()).getName());
            return;
        }
        UUID uuid = UUID.randomUUID();
        Guild guild = new Guild(uuid, name);
        guilds.put(uuid, guild);
        members.put(player.getUniqueId(), uuid);
        roles.put(player.getUniqueId(), Role.Master);
    }

    public void denyInvite(Player player, String name) {
    }

    public void denyPending(CommandSender sender, UUID guild, String name) {
    }

    public void disband(Player player) {
    }

    public void homeAdd(Player player, String home) {
        Location location = player.getLocation();
        Chunk chunk = location.getChunk();
        Guild guild = getGuildByMember(player.getUniqueId());
        Role role = roles.get(player.getUniqueId());

        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }
        if (!role.equals(Role.Master)) {
            MessageManager.guilds_privileges(player.getUniqueId());
            return;
        }

        for (Cords cords : chunks(guild.getUuid())) {
            if(chunk.getWorld().getUID() != cords.getWorld()){
                continue;
            }if(chunk.getX() != cords.getX()) {
                continue;
            }if (chunk.getZ() != cords.getZ()) {
                continue;
            }
            OddJob.getInstance().getHomesManager().addGuild(guild,home,player);
        }
    }

    private Set<Cords> chunks(UUID uuid) {
        Set<Cords> chunks = new HashSet<>();
        for (Cords cords : this.chunks.keySet()){
            if (this.chunks.get(cords).equals(uuid)) {
                chunks.add(cords);
            }
        }
        return chunks;
    }

    public void homeRemove(Player player, String home) {
    }

    public void setHomeRelocate(Player player, String home) {
    }

    public void setHomeRename(Player player, String oldName, String newName) {
    }

    public void homeTeleport(Player player, String home) {
    }

    public void invite(Player player, String name) {
        OddPlayer target = OddJob.getInstance().getPlayerManager().get(name);
        Guild guild = getGuildByMember(player.getUniqueId());
        Role role = roles.get(player.getUniqueId());
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }
        if (!role.equals(guild.getPermissionInvite())) {
            MessageManager.guilds_invite_denied(player, guild);
            return;
        }
        Request.invite(guild, target);
    }

    public void setArea(CommandSender sender, String zone) {
        Zone z = Zone.valueOf(zone);
    }

    public void setOpen(Player player, String open) {
        boolean setOpen = Boolean.parseBoolean(open);
        if (!hasRole(player.getUniqueId(), Role.Master)) return;
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }
        guild.setOpen(setOpen);
        MessageManager.guilds_successfully_set_open(player, setOpen);
    }

    private boolean hasRole(UUID player, Role role) {
        if (roles.get(player).getLevel() >= role.getLevel()) return true;
        MessageManager.guilds_privileges(player);
        return false;
    }

    public void rename(Player player, String name) {
        if (!hasRole(player.getUniqueId(), Role.Master)) return;
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }
        guild.setName(name);
        MessageManager.guilds_successfully_set_name(player, name);
    }

    public void unClaim(Player player) {
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }
    }

    public void unClaim(Player player, Guild guild) {
    }

    public HashMap<UUID, UUID> getMembers() {
        return members;
    }

    public void setRoles(HashMap<UUID, Role> roles) {
        this.roles = roles;
    }

    public void setMembers(HashMap<UUID, UUID> members) {
        this.members = members;
    }
}

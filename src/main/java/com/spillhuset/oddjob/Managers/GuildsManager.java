package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.GuildSQL;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.Managers;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

public class GuildsManager extends Managers {
    /**
     * Guild UUID | Guild
     */
    private final HashMap<UUID, Guild> guilds = new HashMap<>();

    /**
     * Chunk | Guild UUID
     */
    private HashMap<Chunk, UUID> chunks = new HashMap<>();

    /**
     * Player UUID | Guild UUID
     */
    public HashMap<UUID, UUID> members = new HashMap<>();

    public HashMap<Chunk, UUID> getChunks() {
        return chunks;
    }

    public HashMap<UUID, Role> getRoles() {
        return roles;
    }

    public HashMap<UUID, UUID> getPending() {
        return pending;
    }

    public HashMap<UUID, UUID> getInvites() {
        return invites;
    }

    /**
     * Player UUID | Guild Role
     */
    public HashMap<UUID, Role> roles = new HashMap<>();
    private HashMap<UUID, UUID> pending = new HashMap<>();
    private HashMap<UUID, UUID> invites = new HashMap<>();

    public GuildsManager() {
    }

    public void loadGuilds() {
        int i = GuildSQL.loadGuild(null);
        if (i == 0) {
            create(Bukkit.getConsoleSender(), "SafeZone");
            i++;
        }
        OddJob.getInstance().log("loaded guilds: " + i);
    }

    public void loadChunks() {
        this.chunks = GuildSQL.loadChunks();
        OddJob.getInstance().log("loaded guild-chunks: " + this.chunks.size());
    }

    public void loadMembers() {
        GuildSQL.loadMembersRoles();
        OddJob.getInstance().log("loaded guild-members: " + this.members.size());
    }

    public boolean create(CommandSender sender, String name) {
        boolean affected = false;
        Player owner = null;
        name = ChatColor.stripColor(name.toLowerCase().trim());
        name = (name.length() > 15) ? name.substring(0, 15) : name;

        if (sender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (members.containsKey(uuid)) {
                MessageManager.guilds_already_associated(sender, guilds.get(members.get(uuid)).getName());
                return false;
            }
            owner = player;
        }

        for (Guild guild : guilds.values()) {
            if (guild.getName().equalsIgnoreCase(name)) {
                MessageManager.guilds_name_already_exists(name, sender);
                return false;
            }
        }

        Guild guild = new Guild(name, owner);
        guilds.put(guild.getUuid(), guild);
        if (owner != null) {
            members.put(owner.getUniqueId(), guild.getUuid());
            roles.put(owner.getUniqueId(), Role.Master);
            saveMembersRoles();
        }
        saveGuild(guild);
        OddJob.getInstance().log("Saved guild");
        MessageManager.guilds_created(sender, name);
        return affected;
    }

    public void loadGuild(Guild guild) {
        this.guilds.put(guild.getUuid(), guild);
    }

    public void saveGuild(Guild guild) {
        GuildSQL.save(guild);
        OddJob.getInstance().log("saved guild: " + guild.getName());
    }

    public void saveChunks() {
        int i = GuildSQL.saveChunks(chunks);
        OddJob.getInstance().log("saved guild-chunks: " + i);
    }

    public void saveMembersRoles() {
        int i = GuildSQL.saveMembersRoles(members, roles);
        OddJob.getInstance().log("saved guild-members: " + i);
    }

    public void loadStart() {
        loadGuilds();
        loadChunks();
        loadMembers();
    }

    public HashMap<UUID, Guild> getGuilds() {
        return guilds;
    }

    public HashMap<UUID, UUID> getMembers() {
        return members;
    }

    public void info(CommandSender sender, String arg) {
        Guild guild = getGuild(arg);
        if (guild == null) {
            MessageManager.guilds_not_found(sender, arg);
            return;
        }
        MessageManager.guilds_info(sender, guild, getGuildMaster(guild), null, null, null, null);
        //TODO mods,members,pending,invites
    }

    private OddPlayer getGuildMaster(Guild guild) {
        UUID uuid = guild.getUuid();
        for (UUID member : members.keySet()) {
            if (members.get(member) == uuid) {
                if (roles.get(member) == Role.Master) {
                    return OddJob.getInstance().getPlayerManager().get(member);
                }
            }
        }
        return null;
    }

    private Guild getGuild(String arg) {
        for (Guild guild : guilds.values()) {
            if (guild.getName().equalsIgnoreCase(arg)) {
                return guild;
            }
        }
        return null;
    }

    public void claim(Player player) {
        OddJob.getInstance().log("claim");
        Chunk chunk = player.getLocation().getChunk();
        Guild guild = getGuildByMember(player.getUniqueId());
        Role role = roles.get(player.getUniqueId());

        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }

        if (role != Role.Master) {
            MessageManager.guilds_need_permission(player, role);
            return;
        }

        UUID owned = chunks.get(chunk);
        if (owned != null) {
            if (owned == guild.getUuid()) {
                MessageManager.guilds_claim_already(player);
                return;
            }
            MessageManager.guilds_claims_by_else(player);
            return;
        }
        claim(guild, chunk);
        saveChunks();
    }

    private void claim(Guild guild, Chunk chunk) {
        OddJob.getInstance().log("claimed");
        chunks.put(chunk, guild.getUuid());
        MessageManager.guilds_claims_claimed(guild, chunk);
    }

    private Guild getGuildByMember(UUID uniqueId) {
        return getGuild(members.get(uniqueId));
    }

    private Guild getGuild(UUID uuid) {
        return guilds.get(uuid);
    }

    public Guild getGuildByChunk(@Nonnull Chunk chunk) {
        if (chunks.containsKey(chunk))        return getGuildByUuid(chunks.get(chunk));
        else return getGuildByZone(Zone.WILD);
    }

    public Guild getGuildByUuid(@Nonnull UUID guild) {
        return guilds.get(guild);
    }

    public Guild getGuildByZone(Zone zone) {
        for (Guild guild : guilds.values()) {
            if (guild.getZone() == zone) {
                return guild;
            }
        }
        return null;
    }
}

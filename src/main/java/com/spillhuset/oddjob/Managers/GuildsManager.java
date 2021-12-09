package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.GuildSQL;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.Managers;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;
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
    private HashMap<UUID, UUID> members = new HashMap<>();

    /**
     * Player UUID | Guild Role
     */
    private HashMap<UUID, Role> roles = new HashMap<>();
    private HashMap<UUID, UUID> pending = new HashMap<>();
    private HashMap<UUID, UUID> invites = new HashMap<>();

    public void loadGuilds() {
        int i = GuildSQL.loadGuild(null);
        OddJob.getInstance().log("loaded guilds: " + i);
    }

    public void loadChunks() {
        this.chunks = GuildSQL.loadChunks();
        OddJob.getInstance().log("loaded guild-chunks: " + this.chunks.size());
    }

    public void loadMembers() {
        this.members = GuildSQL.loadMembers();
        OddJob.getInstance().log("loaded guild-members: " + this.members.size());
    }

    public void loadRoles() {
        this.roles = GuildSQL.loadRoles();
        OddJob.getInstance().log("loaded guild-roles: " + this.roles.size());
    }

    public void loadPending() {
        this.pending = GuildSQL.loadPending();
        OddJob.getInstance().log("loaded guild-pending: " + this.pending.size());
    }

    public void loadInvites() {
        this.invites = GuildSQL.loadInvites();
        OddJob.getInstance().log("loaded guild-invites: " + this.invites.size());
    }

    public boolean create(CommandSender sender, String name) {
        boolean affected = false;

        name = ChatColor.stripColor(name.toLowerCase().trim().substring(0, 15));

        for (Guild guild : guilds.values()) {
            if (guild.getName().equals(name)) {
                MessageManager.guilds_name_already_exists(name, sender);
            }
        }

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
        int i = GuildSQL.saveMembersRoles(members,roles);
        OddJob.getInstance().log("saved guild-chunks: " + i);
    }
    public void saveChunks() {
        int i = GuildSQL.saveChunks(chunks);
        OddJob.getInstance().log("saved guild-chunks: " + i);
    }
    public void saveChunks() {
        int i = GuildSQL.saveChunks(chunks);
        OddJob.getInstance().log("saved guild-chunks: " + i);
    }
    public void saveChunks() {
        int i = GuildSQL.saveChunks(chunks);
        OddJob.getInstance().log("saved guild-chunks: " + i);
    }

    public HashMap<UUID, String> getGuilds() {
        HashMap<UUID, String> guildList = new HashMap<>();
        for (UUID uuid : guilds.keySet()) {
            guildList.put(uuid, guilds.get(uuid).getName());
        }
        return guildList;
    }

    public HashMap<UUID, UUID> getMembers() {
        return members;
    }
}

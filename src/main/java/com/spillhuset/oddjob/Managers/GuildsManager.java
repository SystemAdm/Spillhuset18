package com.spillhuset.oddjob.Managers;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.Region;
import com.spillhuset.oddjob.Enums.*;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.GuildSQL;
import com.spillhuset.oddjob.SQL.HomesSQL;
import com.spillhuset.oddjob.Utils.Guild;
import com.spillhuset.oddjob.Utils.Managers;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;

public class GuildsManager extends Managers {
    public HashMap<UUID, UUID> autoClaim = new HashMap<>();
    public HashMap<UUID, UUID> autoUnclaim = new HashMap<>();
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
    private final List<UUID> disband = new ArrayList<>();

    public HashMap<Chunk, UUID> getChunks() {
        return chunks;
    }

    public HashMap<UUID, Role> getRoles() {
        return roles;
    }

    /**
     * Player UUID | Guild Role
     */
    public HashMap<UUID, Role> roles = new HashMap<>();

    public GuildsManager() {
    }

    public void loadGuilds() {
        int i = GuildSQL.loadGuild(null);
        if (i < 5) {
            create(Bukkit.getConsoleSender(), "SafeZone", Zone.SAFE);
            create(Bukkit.getConsoleSender(), "WarZone", Zone.WAR);
            create(Bukkit.getConsoleSender(), "WildZone", Zone.WILD);
            create(Bukkit.getConsoleSender(), "ArenaZone", Zone.ARENA);
            create(Bukkit.getConsoleSender(), "JailZone", Zone.JAIL);
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

    /**
     * Guild created from Command
     *
     * @param sender CommandSender
     * @param name   String name of the guild
     */
    public void create(CommandSender sender, String name) {
        create(sender, name, Zone.GUILD);
    }

    /**
     * Guild created from Command or script to create default
     *
     * @param sender CommandSender
     * @param name   String name of the guild
     * @param zone   Zone of the guild
     */
    public void create(CommandSender sender, String name, Zone zone) {
        Player owner = null;
        name = ChatColor.stripColor(name.toLowerCase().trim());
        name = (name.length() > 15) ? name.substring(0, 15) : name;

        // Check if already has guild
        if (sender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (members.containsKey(uuid)) {
                MessageManager.guilds_already_associated(sender, guilds.get(members.get(uuid)).getName());
                return;
            }
            owner = player;
        }

        // Check name of the guild
        for (Guild guild : guilds.values()) {
            if (guild.getName().equalsIgnoreCase(name)) {
                MessageManager.guilds_name_already_exists(name, sender);
                return;
            }
        }

        Guild guild = new Guild(name, owner, zone);
        guilds.put(guild.getUuid(), guild);

        // Sets owner of the guild
        if (owner != null) {
            members.put(owner.getUniqueId(), guild.getUuid());
            roles.put(owner.getUniqueId(), Role.Master);
            saveMembersRoles();
        }

        // Save
        saveGuild(guild);
        OddJob.getInstance().log("Saved guild");
        MessageManager.guilds_created(sender, name);
    }

    /**
     * Load a specific guild from the database
     *
     * @param guild Guild
     */
    public void loadGuild(Guild guild) {
        this.guilds.put(guild.getUuid(), guild);
    }

    /**
     * Save a specific guild
     *
     * @param guild Guild
     */
    public void saveGuild(Guild guild) {
        GuildSQL.save(guild);
        OddJob.getInstance().log("saved guild: " + guild.getName());
    }

    /**
     * Save chunks
     */
    public void saveChunks() {
        int i = GuildSQL.saveChunks(chunks);
        OddJob.getInstance().log("saved guild-chunks: " + i);
    }

    /**
     * Save members and roles
     */
    public void saveMembersRoles() {
        int i = GuildSQL.saveMembersRoles(members, roles);
        OddJob.getInstance().log("saved guild-members: " + i);
    }

    /**
     * Loading scripts
     */
    public void loadStart() {
        loadGuilds();
        loadChunks();
        loadMembers();
    }

    /**
     * Gets a HashMap of guilds
     *
     * @return HashMap of guilds
     */
    public HashMap<UUID, Guild> getGuilds() {
        return guilds;
    }

    /**
     * Gets a HashMap of members
     *
     * @return HashMap of members
     */
    public HashMap<UUID, UUID> getMembers() {
        return members;
    }

    /**
     * Shows info about a guild
     *
     * @param sender CommandSender
     * @param name   String name of guild
     */
    public void info(CommandSender sender, String name) {
        Guild guild = (name == null && sender instanceof Player player) ? OddJob.getInstance().getGuildsManager().getGuildByMember(player.getUniqueId()) : getGuild(name);
        if (guild == null) {
            MessageManager.guilds_not_found(sender, name);
            return;
        }
        List<String> members = new ArrayList<>();
        for (UUID uuid : this.members.keySet()) {
            if (this.members.get(uuid).equals(guild.getUuid())) {
                members.add(OddJob.getInstance().getPlayerManager().get(uuid).getName());
            }
        }
        MessageManager.guilds_info(sender, guild, getGuildMaster(guild), getPending(guild.getUuid()), getInvites(guild.getUuid()), members);
    }

    /**
     * Listing players asking to join the guild
     *
     * @param uuid Guild UUID
     * @return List of players want to join the guild
     */
    private List<OddPlayer> getPending(UUID uuid) {
        List<OddPlayer> oddPlayers = new ArrayList<>();
        for (UUID player : GuildSQL.getPending(uuid, GuildType.uuid)) {
            oddPlayers.add(OddJob.getInstance().getPlayerManager().get(player));
        }
        return oddPlayers;
    }

    /**
     * Listing players invited to join the guild
     *
     * @param uuid Guild UUID
     * @return List of players invited to join the guild
     */
    private List<OddPlayer> getInvites(UUID uuid) {
        List<OddPlayer> oddPlayers = new ArrayList<>();
        for (UUID player : GuildSQL.getInvite(uuid, GuildType.uuid)) {
            oddPlayers.add(OddJob.getInstance().getPlayerManager().get(player));
        }
        return oddPlayers;
    }

    /**
     * Find the GuildMaster
     *
     * @param guild Guild
     * @return OddPlayer with role Master of the guild
     */
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

    /**
     * Find the guild from just a name
     *
     * @param name Guild name
     * @return Guild
     */
    private Guild getGuild(String name) {
        for (Guild guild : guilds.values()) {
            if (guild.getName().equalsIgnoreCase(name)) {
                return guild;
            }
        }
        return null;
    }

    /**
     * Claim chunks for a specified guild
     *
     * @param player Player (Location)
     * @param guild  Guild
     */
    public void claim(Player player, Guild guild) {
        // TODO cost
        // TODO limit
        Chunk chunk = player.getLocation().getChunk();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        boolean valid = true;
        StringBuilder sb = new StringBuilder();

        // Checking if any other guild is near
        for (int x = chunkX - 2; x <= chunkX + 2; x++) {
            for (int z = chunkZ - 2; z <= chunkX + 2; z++) {
                World world = Bukkit.getWorld(player.getWorld().getUID());
                if (world != null) {
                    Chunk test = world.getChunkAt(x, z);
                    if (chunks.containsKey(test) && !chunks.get(chunk).equals(guild.getUuid())) {
                        sb.append(getGuildByChunk(test).getName()).append(",");
                        valid = false;
                    }
                }
            }
        }

        // Has no claims from earlier
        if (!chunks.containsValue(guild.getUuid())) {
            claim(guild, chunk, true);
            return;
        }


        if (valid) {
            valid = false;
            // Are the claims connected
            for (int x = chunkX - 1; x <= chunkX + 1; x++) {
                for (int z = chunkZ - 1; z <= chunkX + 1; z++) {
                    World world = Bukkit.getWorld(player.getWorld().getUID());
                    if (world != null) {
                        Chunk test = world.getChunkAt(x, z);
                        if (chunks.containsKey(test) && chunks.get(chunk).equals(guild.getUuid())) {
                            valid = true;
                        }
                    }
                }
            }
            if (valid) {
                claim(guild, chunk, true);
            } else {
                MessageManager.guilds_claims_connected(player);
            }
        } else {
            MessageManager.guilds_claims_nearby(player, sb.toString());
        }
    }

    /**
     * UnClaim for a specific guild
     *
     * @param player Player (Location)
     * @param guild  Guild
     */
    public void unClaim(Player player, Guild guild) {
        unClaim(guild, player.getLocation().getChunk());
    }

    /**
     * Claim triggered from Command
     *
     * @param player Player (Location and Guild)
     */
    public void claim(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        Guild guild = getGuildByMember(player.getUniqueId());
        Role role = roles.get(player.getUniqueId());

        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        // Check has guild
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }

        // Check has role
        if (role != Role.Master) {
            MessageManager.guilds_need_permission(player, role);
            return;
        }

        // Check chunk
        UUID owned = chunks.get(chunk);
        if (owned != null) {
            if (owned == guild.getUuid()) {
                MessageManager.guilds_claim_already(player);
                return;
            }
            MessageManager.guilds_claims_by_else(player);
            return;
        }
        boolean allowed = false;
        World world = chunk.getWorld();
        for (String a : OddJob.getInstance().getConfig().getStringList("guilds.world")) {
            UUID b = UUID.fromString(a);
            if (b.equals(world.getUID())) {
                allowed = true;
            }
        }
        if (!allowed) {
            MessageManager.guilds_world_disallowed(player, world.getName());
            return;
        }

        // Check limit
        if (guild.getMaxClaims() <= guild.getClaims()) {
            MessageManager.guilds_claims_too_many(player);
            return;
        }

        boolean valid = true;
        StringBuilder sb = new StringBuilder();

        // Checking if any other guild is near
        for (int x = chunkX - 2; x <= chunkX + 2; x++) {
            for (int z = chunkZ - 2; z <= chunkX + 2; z++) {
                Chunk test = world.getChunkAt(x, z);
                if (chunks.containsKey(test) && !chunks.get(chunk).equals(guild.getUuid())) {
                    sb.append(getGuildByChunk(test).getName()).append(",");
                    valid = false;
                }
            }
        }

        // Has no claims from earlier
        if (!chunks.containsValue(guild.getUuid())) {
            claim(guild, chunk, true);
            return;
        }


        if (valid) {
            valid = false;
            // Are the claims connected
            for (int x = chunkX - 1; x <= chunkX + 1; x++) {
                for (int z = chunkZ - 1; z <= chunkX + 1; z++) {
                    Chunk test = world.getChunkAt(x, z);
                    if (chunks.containsKey(test) && chunks.get(chunk).equals(guild.getUuid())) {
                        valid = true;
                    }
                }
            }
            if (valid) {
                claim(guild, chunk, true);
            } else {
                MessageManager.guilds_claims_connected(player);
            }
        } else {
            MessageManager.guilds_claims_nearby(player, sb.toString());
        }

        claim(guild, chunk, true);
        saveChunks();
    }

    /**
     * UnClaim triggered from Command
     *
     * @param player Player (Location and Guild)
     */
    public void unClaim(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        Guild guild = getGuildByMember(player.getUniqueId());
        Role role = roles.get(player.getUniqueId());

        // Check guild
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }

        // Check role
        if (role != Role.Master) {
            MessageManager.guilds_need_permission(player, role);
            return;
        }

        // Check owner
        UUID owned = chunks.get(chunk);
        if (owned != null) {
            if (owned.equals(guild.getUuid())) {
                unClaim(guild, chunk);
                return;
            }
            MessageManager.guilds_claims_by_else(player);
            return;
        }
        MessageManager.guilds_claim_nope(player);
        saveChunks();
    }

    /**
     * Force claim
     *  @param guild Guild
     * @param chunk Chunk
     * @param response Boolean response to the guild?
     */
    private void claim(Guild guild, Chunk chunk, boolean response) {
        chunks.put(chunk, guild.getUuid());
        if (response) MessageManager.guilds_claims_claimed(guild, chunk);
        saveChunks();
    }

    /**
     * Force UnClaim
     *
     * @param guild Guild
     * @param chunk Chunk
     */
    private void unClaim(Guild guild, Chunk chunk) {
        chunks.remove(chunk);
        GuildSQL.removeChunk(guild, chunk);
        MessageManager.guilds_claims_claimed(guild, chunk);
    }

    /**
     * Get guild based on UUID of the player
     *
     * @param player UUID of the player
     * @return Guild
     */
    public Guild getGuildByMember(UUID player) {
        return getGuild(members.get(player));
    }

    /**
     * Get guild based on the UUID of the guild
     *
     * @param guild UUID of the guild
     * @return Guild
     */
    private Guild getGuild(UUID guild) {
        return guilds.get(guild);
    }

    /**
     * Get guild based on the chunk
     *
     * @param chunk Chunk
     * @return Guild
     */
    public Guild getGuildByChunk(@Nonnull Chunk chunk) {
        if (chunks.containsKey(chunk)) return getGuildByUuid(chunks.get(chunk));
        else return getGuildByZone(Zone.WILD);
    }

    /**
     * Get guild based on the UUID of the guild
     *
     * @param guild UUID of the guild
     * @return Guild
     */
    public Guild getGuildByUuid(@Nonnull UUID guild) {
        return guilds.get(guild);
    }

    /**
     * Get guild based on the zone
     *
     * @param zone Zone
     * @return Guild
     */
    public Guild getGuildByZone(Zone zone) {
        for (Guild guild : guilds.values()) {
            if (guild.getZone() == zone) {
                return guild;
            }
        }
        return null;
    }

    public void autoClaim(Player player, UUID guild) {
        if (!autoClaim.containsKey(player.getUniqueId())) {
            autoClaim.put(player.getUniqueId(), guild);
            claim(getGuild(guild), player.getLocation().getChunk(), true);
        } else {
            autoClaim.remove(player.getUniqueId());
        }
    }

    public void autoUnClaim(Player player, UUID guild) {
        if (!autoUnclaim.containsKey(player.getUniqueId())) {
            autoUnclaim.put(player.getUniqueId(), guild);
            unClaim(getGuild(guild), player.getLocation().getChunk());
        } else {
            autoUnclaim.remove(player.getUniqueId());
        }
    }

    public Guild getGuildByName(String name) {
        for (Guild guild : guilds.values()) {
            if (guild.getName().equalsIgnoreCase(name)) {
                return guild;
            }
        }
        return null;
    }

    public void setHome(Player player, String name) {
        Guild guild = getGuildByMember(player.getUniqueId());

        if (guild != null) {
            Role role = roles.get(player.getUniqueId());
            if (role == Role.Master) {
                Chunk chunk = player.getLocation().getChunk();
                if (chunks.get(chunk) != null && chunks.get(chunk).equals(guild.getUuid())) {
                    MessageManager.guilds_set_spawn_success(player, player.getLocation(), guild);
                } else {
                    MessageManager.guilds_set_spawn_error_in_chunk(player);
                }
            } else {
                MessageManager.guilds_error_role(player);
            }
        } else {
            MessageManager.guilds_not_associated(player);
        }
    }

    public void home(Player player, String name) {
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild != null) {
            Location location = guild.getHome(name);
            if (location != null) {
                OddJob.getInstance().getTeleportManager().teleport(player, location, Plugin.guilds);
            } else {
                MessageManager.guilds_spawn_not_set(player);
            }
        } else {
            MessageManager.guilds_not_associated(player);
        }
    }

    public void invite(Player player, String name) {
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }
        // Guild found!

        OddPlayer oddPlayer = PlayerManager.getPlayerByName(name);
        if (oddPlayer == null) {
            MessageManager.errors_find_player(Plugin.guilds, name, player);
            return;
        }
        // Player found!

        List<UUID> invited = GuildSQL.getInvite(oddPlayer.getUuid(), GuildType.player);
        if (invited.contains(guild.getUuid())) {
            MessageManager.guilds_already_invited_this(player, oddPlayer);
            return;
        }
        // Not already invited to the guild!

        if (!invited.isEmpty()) {
            MessageManager.guilds_already_invited(player, oddPlayer);
            // Already invited to another guild
        }

        List<UUID> pending = GuildSQL.getPending(oddPlayer.getUuid(), GuildType.player);
        if (pending.contains(guild.getUuid())) {
            MessageManager.guilds_already_pending_this(player, oddPlayer);
            join(guild, oddPlayer);
            return;
        }
        //- join
        // Not already pending to join the guild

        GuildSQL.invite(oddPlayer.getUuid(), guild.getUuid());
        MessageManager.guilds_invite_sent(player, oddPlayer, guild);
        // Invite sent
    }

    private void join(Guild guild, OddPlayer oddPlayer) {
        if (members.containsKey(oddPlayer.getUuid())) {
            members.remove(oddPlayer.getUuid());
            roles.remove(oddPlayer.getUuid());
        }
        MessageManager.guilds_pending_welcome_guild(oddPlayer, guild);
        members.put(oddPlayer.getUuid(), guild.getUuid());
        roles.put(oddPlayer.getUuid(), Role.Members);
        saveMembersRoles();

        removeInvites(oddPlayer);
        removePending(oddPlayer);
        MessageManager.guilds_pending_welcome(oddPlayer, guild);
    }

    /**
     * Removing pending request from the database
     *
     * @param sender    CommandSender
     * @param guild     UUID of the guild
     * @param oddPlayer OddPlayer of the requesting target
     */
    private void declinePending(CommandSender sender, UUID guild, OddPlayer oddPlayer) {
        MessageManager.guilds_declined_pending(getGuild(guild), oddPlayer);
        GuildSQL.removePending(oddPlayer.getUuid(), guild, GuildType.uuid);
    }

    /**
     * Removes all pending join requests the player has.
     *
     * @param oddPlayer OddPlayer with pending
     */
    private void removePending(OddPlayer oddPlayer) {
        if (GuildSQL.removePending(oddPlayer.getUuid(), null, GuildType.player)) {
            MessageManager.guilds_pending_removed(oddPlayer.getUuid());
        }
    }

    /**
     * Removing the invitation from the database
     *
     * @param sender    CommandSender
     * @param guild     UUID of the Guild
     * @param oddPlayer OddPlayer of the invited target
     */
    private void declineInviteGuild(CommandSender sender, UUID guild, OddPlayer oddPlayer) {
        MessageManager.guilds_invite_declined(sender, oddPlayer);
        GuildSQL.removeInvites(oddPlayer.getUuid(), guild, GuildType.uuid);
    }

    /**
     * Removes all invitation the player has.
     *
     * @param oddPlayer OddPlayer with invitations
     */
    private void removeInvites(OddPlayer oddPlayer) {
        if (GuildSQL.removeInvites(oddPlayer.getUuid(), null, GuildType.player)) {
            MessageManager.guilds_invites_removed(oddPlayer.getUuid());
        }
    }

    public void join(Player player, String guildName) {
        UUID already = getMembers().get(player.getUniqueId());
        if (already != null) {
            MessageManager.guilds_already_associated(player, OddJob.getInstance().getGuildsManager().getGuildByUuid(already).getName());
            return;
        }
        // Player has no guild!

        Guild guild = getGuildByName(guildName);
        if (guild == null) {
            MessageManager.guilds_not_found(player, guildName);
            return;
        }
        // Guild found!

        OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(player.getUniqueId());

        if (guild.isOpen()) {
            join(guild, oddPlayer);
            MessageManager.guilds_joined_open_guild(player, guild);
            return;
        }
        //- join
        // Guild is not open!

        if (guild.isInvited_only()) {
            MessageManager.guilds_invited_only(player, guild);
            return;
        }
        // Guild is not invited only!

        if (getPending(guild.getUuid()).contains(oddPlayer)) {
            MessageManager.guilds_already_pending_join(player, guild);
            return;
        }
        // Not already pending to join the guild

        if (getInvites(guild.getUuid()).contains(oddPlayer)) {
            join(guild, oddPlayer);
            MessageManager.guilds_already_invited_join(player, guild);
            return;
        }
        //- join
        // Not already invited to join the guild

        setPending(guild, oddPlayer);
        MessageManager.guilds_pending_set(player, guild);
        // Pending sent
    }

    private void setPending(Guild guild, OddPlayer oddPlayer) {
        GuildSQL.setPending(guild, oddPlayer);
    }

    public void acceptInvite(Player sender, @Nullable String name) {
        UUID uuid = sender.getUniqueId();
        List<UUID> guilds = GuildSQL.getInvite(uuid, GuildType.player);
        if (guilds.isEmpty()) {
            MessageManager.guilds_no_invites(sender);
        } else if (name == null) {
            if (guilds.size() > 1) {
                MessageManager.guilds_more_invites(sender);
            } else {
                join(getGuild(guilds.get(0)), OddJob.getInstance().getPlayerManager().get(uuid));
            }
        } else {
            for (Guild guild : getGuilds().values()) {
                if (guild.getName().equalsIgnoreCase(name)) {
                    if (guilds.contains(guild.getUuid())) {
                        join(guild, OddJob.getInstance().getPlayerManager().get(uuid));
                        return;
                    }
                }
            }
            MessageManager.guilds_no_invites_from(sender, name);
        }
    }

    /**
     * A player denies an invitation to join a guild
     *
     * @param sender Player invited
     * @param name   Optional String name of the guild
     */
    // guilds deny <guild>
    public void denyInvite(Player sender, @Nullable String name) {
        UUID uuid = sender.getUniqueId();
        List<UUID> guilds = GuildSQL.getInvite(uuid, GuildType.player);
        if (guilds.isEmpty()) {
            MessageManager.guilds_no_invites(sender);
        } else if (name == null) {
            if (guilds.size() > 1) {
                MessageManager.guilds_more_invites(sender);
            } else {
                declineInvite(sender, getGuild(guilds.get(0)));
            }
        } else {
            for (Guild guild : getGuilds().values()) {
                if (guild.getName().equalsIgnoreCase(name)) {
                    if (guilds.contains(guild.getUuid())) {
                        declineInvite(sender, guild);
                        return;
                    }
                }
            }
            MessageManager.guilds_no_invites_from(sender, name);
        }
    }

    private void declineInvite(Player sender, Guild guild) {
        GuildSQL.removeInvites(sender.getUniqueId(), guild.getUuid(), GuildType.uuid);
    }

    public void acceptPending(CommandSender sender, UUID guild, @Nullable String name) {
        List<UUID> players = GuildSQL.getPending(guild, GuildType.uuid);
        if (players.isEmpty()) {
            MessageManager.guilds_no_pending(sender);
        } else if (name == null) {
            if (players.size() > 1) {
                MessageManager.guilds_more_pending(sender);
            } else {
                OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(players.get(0));
                join(getGuild(guild), oddPlayer);
            }
        } else {
            for (String string : OddJob.getInstance().getPlayerManager().listString()) {
                if (string.equalsIgnoreCase(name)) {
                    OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(string);
                    if (players.contains(oddPlayer.getUuid())) {
                        join(getGuild(guild), oddPlayer);

                        return;
                    }
                }
            }
            MessageManager.guilds_no_pending_from(sender, name);
        }
    }

    public void denyPending(CommandSender sender, UUID guild, @Nullable String name) {
        List<UUID> players = GuildSQL.getPending(guild, GuildType.uuid);
        if (players.isEmpty()) {
            MessageManager.guilds_no_pending(sender);
        } else if (name == null) {
            if (players.size() > 1) {
                MessageManager.guilds_more_pending(sender);
            } else {
                OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(players.get(0));
                declinePending(sender, guild, oddPlayer);
            }
        } else {
            for (String string : OddJob.getInstance().getPlayerManager().listString()) {
                if (string.equalsIgnoreCase(name)) {
                    OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(string);
                    if (players.contains(oddPlayer.getUuid())) {
                        declinePending(sender, guild, oddPlayer);
                        return;
                    }
                }
            }
            MessageManager.guilds_no_pending_from(sender, name);
        }
    }

    public void buyClaims(CommandSender sender) {
        Player player = (Player) sender;
        Guild guild = getGuild(getMembers().get(player.getUniqueId()));

        // Check guild
        if (guild == null) {
            MessageManager.guilds_not_associated(sender);
            return;
        }

        // Check role
        Role role = getRoles().get(player.getUniqueId());
        if (role != Role.Master) {
            MessageManager.guilds_error_role(sender);
            return;
        }

        Plu plu = Plu.GUILDS_CLAIMS;

        int count = guild.getClaims();
        int bought = guild.getBoughtClaims();
        int max = guild.getMaxClaims();

        double sum = Math.pow(plu.getMultiplier(), bought) * plu.getValue();
        boolean trans = OddJob.getInstance().getCurrencyManager().sub(sender, Account.guild, guild.getUuid(), sum);

        if (trans) {
            guild.incMaxClaims();
            MessageManager.guilds_bought_claims(sender, guild, count, max + 1);
            saveGuild(guild);
        }
    }

    public void buyHomes(CommandSender sender) {
        Player player = (Player) sender;
        Guild guild = getGuildByMember(player.getUniqueId());

        // Check guild
        if (guild == null) {
            MessageManager.guilds_not_associated(sender);
            return;
        }

        // Check role
        Role role = getRoles().get(player.getUniqueId());
        if (role != Role.Master) {
            MessageManager.guilds_error_role(sender);
            return;
        }

        // Price tag
        Plu plu = Plu.GUILDS_HOMES;

        int count = OddJob.getInstance().getHomeManager().getList(guild.getUuid()).size();                              // Vi har nå: .0.
        int bought = guild.getBoughtHomes();
        int max = guild.getMaxHomes();                                                                                  // Vi starter med (START = 10) (DB = 0) (MEDLEM = *5)

        double sum = Math.pow(plu.getMultiplier(), bought) * plu.getValue();

        boolean trans = OddJob.getInstance().getCurrencyManager().sub(sender, Account.guild, guild.getUuid(), sum);
        if (trans) {
            guild.incMaxHomes();
            MessageManager.guilds_bought_homes(sender, guild, count, max + 1);
            saveGuild(guild);
        }

    }

    public void setHomeRelocate(Player player, String name) {
        Guild guild = getGuildByMember(player.getUniqueId());
        Location location = player.getLocation();
        Chunk chunk = location.getChunk();
        List<String> list = OddJob.getInstance().getHomeManager().getList(guild.getUuid());

        // Check name
        if (!list.contains(name)) {
            MessageManager.guilds_no_homes_name(player, name);
            return;
        }

        // Check chunk
        UUID owner = getGuildByChunk(chunk).getUuid();
        if (owner != guild.getUuid()) {
            MessageManager.guilds_homes_inside(player);
            return;
        }

        // Check role
        Role role = getRoles().get(player.getUniqueId());
        if (role != Role.Master) {
            MessageManager.guilds_error_role(player);
            return;
        }

        OddJob.getInstance().getHomeManager().change(player, guild.getUuid(), name);
        MessageManager.guilds_home_relocated(player, guild, name);
    }

    public void setHomeRename(Player player, String oldName, String newName) {
        Guild guild = getGuildByMember(player.getUniqueId());
        Location location = player.getLocation();
        Chunk chunk = location.getChunk();
        List<String> list = OddJob.getInstance().getHomeManager().getList(guild.getUuid());

        // Check limit
        if (list.size() >= guild.getMaxHomes()) {
            MessageManager.guilds_max_homes_reached(player);
            return;
        }

        // Check old
        if (!list.contains(oldName)) {
            MessageManager.guilds_no_homes_name(player, oldName);
            return;
        }

        // Check new
        if (list.contains(newName)) {
            MessageManager.guilds_homes_name_already_exist(player, newName);
            return;
        }

        // Check chunk
        UUID owner = getGuildByChunk(chunk).getUuid();
        if (owner != guild.getUuid()) {
            MessageManager.guilds_homes_inside(player);
            return;
        }

        // Check role
        Role role = getRoles().get(player.getUniqueId());
        if (role != Role.Master) {
            MessageManager.guilds_error_role(player);
            return;
        }

        OddJob.getInstance().getHomeManager().rename(guild.getUuid(), oldName, newName);
        MessageManager.guilds_home_renamed(player, guild, oldName, newName);
    }

    public void setOpen(Player player, String arg) {
        Guild guild = getGuildByMember(player.getUniqueId());
        Role role = getRoles().get(player.getUniqueId());

        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }
        if (role != Role.Master) {
            MessageManager.guilds_error_role(player);
            return;
        }

        boolean open = Boolean.parseBoolean(arg);
        guild.setOpen(open);
        saveGuild(guild);
        MessageManager.guilds_set_open_success(guild, open);
    }

    public Plugin getPlugin() {
        return Plugin.guilds;
    }

    public void rename(Player player, String name) {
        Guild guild = getGuildByMember(player.getUniqueId());
        Role role = getRoles().get(player.getUniqueId());

        // Check guild
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }

        // Check role
        if (role != Role.Master) {
            MessageManager.guilds_error_role(player);
            return;
        }

        // Check names
        for (Guild test : guilds.values()) {
            if (test.getName().equalsIgnoreCase(name)) {
                MessageManager.errors_name(getPlugin(), player, name);
                return;
            }
        }

        guild.setName(name);
        MessageManager.guilds_set_name_success(guild, name);
    }

    public void disband(Player player, @Nullable String name) {
        Guild guild = getGuildByMember(player.getUniqueId());
        Role role = getRoles().get(player.getUniqueId());

        // Check guild
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }

        // Check role
        if (role != Role.Master) {
            MessageManager.guilds_error_role(player);
            return;
        }

        if (name == null) {
            MessageManager.guilds_disband(player);
            disband.add(guild.getUuid());
        } else {
            if (name.equals(guild.getName())) {
                MessageManager.guilds_disbanded(guild);
                disband(guild);
            }
        }
    }

    private void disband(Guild guild) {
        // Remove members and roles
        for (UUID uuid : members.keySet()) {
            if (members.get(uuid).equals(guild.getUuid())) {
                members.remove(uuid);
                roles.remove(uuid);
            }
        }
        saveMembersRoles();

        // Remove chunks
        for (Chunk chunk : chunks.keySet()) {
            if (chunks.get(chunk).equals(guild.getUuid())) {
                chunks.remove(chunk);
            }
        }

        saveChunks();
        guilds.remove(guild.getUuid());
        GuildSQL.disband(guild.getUuid());
    }

    public void leave(Player player) {
        Guild guild = getGuildByMember(player.getUniqueId());
        Role role = getRoles().get(player.getUniqueId());

        // Check guild
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }

        // Check role
        if (role == Role.Master) {
            int i = 0;
            for (UUID uuid : members.keySet()) {
                if (members.get(uuid).equals(guild.getUuid())) {
                    i++;
                }
            }
            if (i > 1) {
                MessageManager.guilds_leave_more(player);
                return;
            }
            MessageManager.guilds_leave_last(player);
            return;
        }

        MessageManager.guilds_leave(player);
        members.remove(player.getUniqueId());
        roles.remove(player.getUniqueId());
        saveMembersRoles();
        MessageManager.guilds_left(player, guild);
        GuildSQL.leave(player);
    }

    public void notify(Player player) {
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild == null) {
            return;
        }
        Role role = getRoles().get(player.getUniqueId());
        if (role == Role.Master) {
            List<OddPlayer> pending = getPending(guild.getUuid());
            if (pending.size() >= 1) {
                MessageManager.guilds_notify_pending(player, pending);
            }
        }
    }

    public void homeTeleport(Player player, String name) {
        Guild guild = getGuildByMember(player.getUniqueId());
        Location location = HomesSQL.get(guild.getUuid(), name);

        // Check name
        if (location == null) {
            MessageManager.guilds_no_homes_name(player, name);
            return;
        }

        OddJob.getInstance().getTeleportManager().teleport(player, location, Plugin.guilds);
    }

    public void homeAdd(Player player, String name) {
        Guild guild = getGuildByMember(player.getUniqueId());
        Location location = player.getLocation();
        Chunk chunk = location.getChunk();
        List<String> list = OddJob.getInstance().getHomeManager().getList(guild.getUuid());

        // Check limit
        if (list.size() >= guild.getMaxHomes()) {
            MessageManager.guilds_max_homes_reached(player);
            return;
        }

        // Check name
        if (list.contains(name)) {
            MessageManager.guilds_homes_name_already_exist(player, name);
            return;
        }

        // Check chunk
        UUID owner = getGuildByChunk(chunk).getUuid();
        if (owner != guild.getUuid()) {
            claim(player, guild);
            MessageManager.guilds_homes_inside(player);
            return;
        }

        // Check role
        Role role = getRoles().get(player.getUniqueId());
        if (role != Role.Master) {
            MessageManager.guilds_error_role(player);
            return;
        }

        OddJob.getInstance().getHomeManager().addGuild(player, guild.getUuid(), name);
    }

    public void homeRemove(Player player, String name) {
        Guild guild = getGuildByMember(player.getUniqueId());
        List<String> list = OddJob.getInstance().getHomeManager().getList(guild.getUuid());

        // Check name
        if (!list.contains(name)) {
            MessageManager.guilds_no_homes_name(player, name);
            return;
        }

        // Check role
        Role role = getRoles().get(player.getUniqueId());
        if (role != Role.Master) {
            MessageManager.guilds_error_role(player);
            return;
        }

        OddJob.getInstance().getHomeManager().delGuild(player, guild.getUuid(), name);

    }

    public List<String> list() {
        List<String> list = new ArrayList<>();
        for (Guild guild : guilds.values()) {
            list.add(guild.getName());
        }
        return list;
    }

    public void setArea(CommandSender sender, String type) {
        Player player = (Player) sender;

        Zone guildType = null;
        for (Zone t : Zone.values()) {
            if (t.name().equalsIgnoreCase(type)) {
                guildType = t;
            }
        }

        if (guildType == null) {
            MessageManager.errors_guild_type(sender, type);
            return;
        }
        Guild guild = OddJob.getInstance().getGuildsManager().getGuildByZone(guildType);

        try {
            Region selection = OddJob.getInstance().getWorldEdit().getSession(player).getSelection();

            Set<BlockVector2> chs = selection.getChunks();
            World world = Bukkit.getWorld(player.getWorld().getUID());
            if (world != null) {
                for (BlockVector2 ch : chs) {
                    Chunk chunk = world.getChunkAt(ch.getX(), ch.getZ());
                    claim(guild, chunk,false);
                }
            }
        } catch (IncompleteRegionException ignored) {
            MessageManager.guilds_errors_set_area(sender);
        }

    }
}

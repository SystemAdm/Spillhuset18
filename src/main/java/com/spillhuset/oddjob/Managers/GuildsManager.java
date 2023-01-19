package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.CurrencySQL;
import com.spillhuset.oddjob.SQL.GuildSQL;
import com.spillhuset.oddjob.Utils.*;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GuildsManager extends Managers {
    /**
     * UUID Guild, Guild
     */
    private HashMap<UUID, Guild> guilds = new HashMap<>();
    /**
     * UUID Player, Role
     */
    private HashMap<UUID, Role> roles = new HashMap<>();
    /**
     * UUID Player, UUID Guild
     */
    private HashMap<UUID, UUID> members = new HashMap<>();
    /**
     * Cords, UUID Guild
     */
    private HashMap<Cords, UUID> chunks = new HashMap<>();

    public void buyClaims(CommandSender sender) {
        Player player = (Player) sender;
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild == null) {
            MessageManager.guilds_not_associated(sender);
            return;
        }
        Plu plu = Plu.GUILDS_CLAIMS;
        int bought = guild.getBoughtClaims() + 1;
        double price = (bought * plu.getValue()) * plu.getMultiplier() + (bought * plu.getValue());
        if (!OddJob.getInstance().getCurrencyManager().checkBank(guild.getUuid(), price)) {
            MessageManager.insufficient_funds(sender);
            return;
        }
        OddJob.getInstance().getCurrencyManager().subBank(guild.getUuid(), price);
        guild.incBoughtClaims();
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
        return guilds;
    }

    public void leave(Player player) {
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild != null) {
            members.remove(player.getUniqueId());
            roles.remove(player.getUniqueId());
            MessageManager.guilds_left(player, guild);
            GuildSQL.removeMember(player.getUniqueId());
        } else
            MessageManager.guilds_not_associated(player);

    }

    public void loadGuild(Guild guild) {
        guilds.put(guild.getUuid(), guild);
    }

    public void info(Player player) {
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }
        info(player, guild.getName());
    }

    public HashMap<UUID, Role> getRoles() {
        return roles;
    }

    public void info(CommandSender sender, String name) {
        String master = "";
        if (name == null) {
            if (sender instanceof Player player) {
                Guild guild = getGuildByMember(player.getUniqueId());
                if (guild == null) {
                    return;
                }
                int claims = 0;
                for (UUID uuid : chunks.values()) {
                    if (uuid.equals(guild.getUuid())) claims++;
                }
                StringBuilder sbMods = new StringBuilder();
                StringBuilder sbMembers = new StringBuilder();
                for (UUID pUUID : members.keySet()) {
                    if (members.get(pUUID).equals(guild.getUuid())) {
                        switch (roles.get(pUUID)) {
                            case Master -> master = OddJob.getInstance().getPlayerManager().get(pUUID).getName();
                            case Mods ->
                                    sbMods.append(OddJob.getInstance().getPlayerManager().get(pUUID).getName()).append(",");
                            case Members ->
                                    sbMembers.append(OddJob.getInstance().getPlayerManager().get(pUUID).getName()).append(",");
                        }
                    }
                }
                int homes = OddJob.getInstance().getHomesManager().getList(guild.getUuid()).size();
                MessageManager.guilds_info_your(sender, guild, master, sbMods.toString(), sbMembers.toString(), homes, claims);
                return;
            }
            return;
        }
        Guild guild = getGuildByName(name);
        if (guild == null) {
            MessageManager.guilds_not_found(sender, name);
            return;
        }
        int memberCount = 0;
        int claims = 0;
        for (UUID pUUID : members.keySet()) {
            if (members.get(pUUID).equals(guild.getUuid())) memberCount++;
            if (roles.get(pUUID).equals(Role.Master))
                master = OddJob.getInstance().getPlayerManager().get(pUUID).getName();
        }
        for (UUID uuid : chunks.values()) {
            if (uuid.equals(guild.getUuid())) claims++;
        }
        MessageManager.guilds_info_other(sender, guild, master, memberCount, claims);
    }

    public List<String> list() {
        List<String> list = new ArrayList<>();
        for (UUID uuid : guilds.keySet()) {
            list.add(guilds.get(uuid).getName());
        }
        return list;
    }

    @Nullable
    public Guild getGuildByMember(UUID player) {
        return getGuildByUUID(members.get(player));
    }

    @Nullable
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

    public void acceptInvite(Player player, Guild guild) {
        members.put(player.getUniqueId(), guild.getUuid());
        roles.put(player.getUniqueId(), Role.Members);
        save(true, null);
        MessageManager.guilds_invited_welcome(guild, player);
        GuildSQL.removeInvites(player.getUniqueId(), guild.getUuid());
    }

    public void acceptPending(OddPlayer player, Guild guild) {
        members.put(player.getUuid(), guild.getUuid());
        roles.put(player.getUuid(), Role.Members);
        save(true, null);
        MessageManager.guilds_pending_welcome(guild, player);
        GuildSQL.removePending(player.getUuid(), guild.getUuid());
    }

    public void buyHomes(Player player) {
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }
        double price = Plu.GUILDS_HOMES.getValue() * (guild.getBoughtHomes() + 1 * Plu.GUILDS_HOMES.getMultiplier());
        boolean has = (OddJob.getInstance().getCurrencyManager().checkBank(guild.getUuid(),price));
        MessageManager.guilds_buy_homes(player,price,has);
    }
    public void buyHomesConfirm(Player player) {
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }
        double price = Plu.GUILDS_HOMES.getValue() * (guild.getBoughtHomes() + 1 * Plu.GUILDS_HOMES.getMultiplier());
        if (!OddJob.getInstance().getCurrencyManager().checkBank(guild.getUuid(),price)) {
            MessageManager.insufficient_funds(player);
            return;
        }
        OddJob.getInstance().getCurrencyManager().subBank(guild.getUuid(),price);
        guild.incBoughtHomes();
        save(guild);
        price = Plu.GUILDS_HOMES.getValue() * (guild.getBoughtHomes() + 1 * Plu.GUILDS_HOMES.getMultiplier());
        MessageManager.guilds_homes_bought(player,price,guild.getMaxHomes());
    }

    public void claim(Player player, boolean outpost) {
        // find guild
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild == null) {
            MessageManager.guilds_not_associated(player);
            return;
        }

        // find role
        Role role = getRoles().get(player.getUniqueId());
        if (role.getLevel() < Role.Mods.getLevel()) {
            MessageManager.guilds_role_needed(player, guild, Role.Mods);
            return;
        }

        Chunk chunk = player.getLocation().getChunk();
        for (Cords cords : chunks.keySet()) {
            // standing in chunk
            if (cords.getWorld().equals(chunk.getWorld().getUID()) &&
                    cords.getX() == chunk.getX() && cords.getZ() == chunk.getZ()) {
                if (cords.getGuild().equals(guild.getUuid())) {
                    MessageManager.guilds_claimed_you(player);
                    return;
                }
                // check if claimed already
                Guild claimed = getGuildByUUID(cords.getGuild());
                if (claimed != null) MessageManager.guilds_claimed(claimed, player);
                return;
            }
        }

        // is there any other guild within 2 chunks
        if (claimsNear(player.getLocation(), guild)) {
            MessageManager.guilds_claimed_near(player);
            return;
        }

        int countClaims = 0;
        // count the claimed chunks
        for (UUID uuid : chunks.values()) {
            if (guild.getUuid().equals(uuid)) countClaims++;
        }

        // reached limit
        if (countClaims != 0 && countClaims >= guild.getMaxClaims()) {
            MessageManager.guilds_claiming_max_reached(player);
            return;
        }

        // outpost
        if (outpost) {
            GuildSQL.saveChunk(player.getLocation().getChunk(), guild);
            guild.useOutpost();
            MessageManager.guilds_claiming_outpost(player, chunk, guild);
        }

        // is the claims connected
        if (countClaims != 0) {
            UUID comp = nearChunk(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID());
            if (comp == null || !comp.equals(guild.getUuid())) {
                MessageManager.guilds_claimed_connected(player);
                return;
            }
        }

        // save them all
        chunks.put(new Cords(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID(), guild.getUuid()), guild.getUuid());
        GuildSQL.saveChunk(chunk, guild);
        MessageManager.guilds_claiming(player, chunk, guild);
    }

    /**
     * Find connecting claim
     *
     * @param x     Chunk x coords
     * @param z     Chunk z coords
     * @param world Chunk world
     * @return UUID of guild
     */
    @Nullable
    public UUID nearChunk(int x, int z, UUID world) {
        for (Cords cords : chunks.keySet()) {
            if ((cords.getX() == x - 1 && cords.getZ() == z && cords.getWorld() == world) ||
                    (cords.getX() == x + 1 && cords.getZ() == z && cords.getWorld() == world) ||
                    (cords.getX() == x && cords.getZ() == z - 1 && cords.getWorld() == world) ||
                    (cords.getX() == x && cords.getZ() == z + 1 && cords.getWorld() == world)) {
                return cords.getGuild();
            }
        }
        return null;
    }

    /**
     * Find guild within limit
     *
     * @param location Location to search from
     * @param guild    Guild to compare
     * @return boolean if in reach
     */
    private boolean claimsNear(Location location, Guild guild) {
        Chunk chunk = location.getChunk();

        for (int x = chunk.getX() - 2; x <= chunk.getX() + 2; x++) {
            for (int z = chunk.getZ() - 2; z <= chunk.getZ() + 2; z++) {
                for (Cords cords : chunks.keySet()) {
                    if (cords.getWorld() == chunk.getWorld().getUID() &&
                            cords.getX() == x && cords.getZ() == z) {
                        return !cords.getGuild().equals(guild.getUuid());
                    }
                }
            }
        }
        return false;
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

    public void create() {
        if (getGuildByName("SafeZone") == null) create("SafeZone", Zone.SAFE);
        if (getGuildByName("WarZone") == null) create("WarZone", Zone.WAR);
        if (getGuildByName("ArenaZone") == null) create("ArenaZone", Zone.ARENA);
        if (getGuildByName("WildZone") == null) create("WildZone", Zone.WILD);
        save(false,null);
    }
    public void create(String name, Zone zone) {
        Guild guild = new Guild(UUID.randomUUID(), name, zone);
        GuildSQL.save(guild);
    }

    public void create(Player player, String name) {
        if (getGuildByName("SafeZone") == null) create("SafeZone", Zone.SAFE);
        if (getGuildByName("WarZone") == null) create("WarZone", Zone.WAR);
        if (getGuildByName("ArenaZone") == null) create("ArenaZone", Zone.ARENA);
        if (getGuildByName("WildZone") == null) create("WildZone", Zone.WILD);

        if (getGuildByName(name) != null) {
            MessageManager.guilds_exists(player, name);
            return;
        }
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild != null) {
            MessageManager.guilds_already_associated(player, guild.getName());
            return;
        }
        UUID uuid = UUID.randomUUID();
        guild = new Guild(uuid, name);
        guilds.put(uuid, guild);
        members.put(player.getUniqueId(), uuid);
        roles.put(player.getUniqueId(), Role.Master);
        MessageManager.guilds_created(player, name);
        save(true, guild);
    }

    private void save(boolean memberRole, @Nullable Guild guild) {
        if (guild != null) GuildSQL.save(guild);
        if (memberRole) GuildSQL.saveMembersRoles(members, roles);
    }

    /**
     * @param target
     * @param guild
     */
    public void denyInvite(Player target, Guild guild) {
        GuildSQL.removeInvites(target.getUniqueId(), guild.getUuid());
        MessageManager.guilds_invited_denied(target, guild);
    }

    public void denyPending(OddPlayer player, Guild guild) {
        GuildSQL.removePending(player.getUuid(), guild.getUuid());
        MessageManager.guilds_pending_denied(player, guild);
    }

    public void disband(Player player) {
        List<UUID> players = new ArrayList<>();
        Guild guild = getGuildByMember(player.getUniqueId());
        if (guild != null) {
            for (UUID uuidPlayer : members.keySet()) {
                if (members.get(uuidPlayer).equals(guild.getUuid())) {
                    roles.remove(uuidPlayer);
                    players.add(uuidPlayer);
                }
            }
            for (UUID uuid : players) {
                members.remove(uuid);
            }
            guilds.remove(guild.getUuid());
            GuildSQL.disband(guild.getUuid());
            MessageManager.guilds_disband_success(player);
        }
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
            if (chunk.getWorld().getUID() != cords.getWorld()) {
                continue;
            }
            if (chunk.getX() != cords.getX()) {
                continue;
            }
            if (chunk.getZ() != cords.getZ()) {
                continue;
            }
            OddJob.getInstance().getHomesManager().addGuild(guild, home, player);
        }
    }

    private Set<Cords> chunks(UUID uuid) {
        Set<Cords> chunks = new HashSet<>();
        for (Cords cords : this.chunks.keySet()) {
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
        if (role.getLevel() < guild.getPermissionInvite().getLevel()) {
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
        Role role = getRoles().get(player.getUniqueId());
        if (role.getLevel() < Role.Mods.getLevel()) {
            MessageManager.guilds_role_needed(player, guild, Role.Mods);
            return;
        }
        Chunk chunk = player.getLocation().getChunk();
        Cords remove = null;
        for (Cords cords : chunks.keySet()) {
            if (cords.getWorld().equals(chunk.getWorld().getUID()) &&
                    cords.getX() == chunk.getX() && cords.getZ() == chunk.getZ()) {
                if (cords.getGuild().equals(guild.getUuid())) {
                    remove = cords;
                    break;
                }
            }
        }
        if (remove != null) {
            chunks.remove(remove);
            GuildSQL.removeChunk(guild,remove);
            MessageManager.guilds_unClaiming(player, chunk, guild);
        } else
            OddJob.getInstance().log("something wrong");
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

    public double getBank(UUID uuid) {
        return CurrencySQL.getBank(uuid);
    }

    public boolean hasHome(UUID uuid) {
        return !OddJob.getInstance().getHomesManager().getList(uuid).isEmpty();
    }

    public void load() {
        GuildSQL.loadGuild(null);
        GuildSQL.loadMembersRoles();
        chunks = GuildSQL.loadChunks();
    }

    public void save(Guild guild) {
        GuildSQL.save(guild);
    }

    public List<UUID> getPending(UUID uuid, boolean guild) {
        return GuildSQL.getPending(uuid, guild);
    }

    public Guild getGuild(UUID uuid) {
        return getGuilds().get(uuid);
    }

    public List<UUID> getInvites(UUID uuid, boolean guild) {
        return GuildSQL.getInvite(uuid, guild);
    }

    public Guild getGuildByZone(Zone zone) {
        for (UUID uuid : guilds.keySet()) {
            if (guilds.get(uuid).getZone().equals(zone)) {
                return getGuild(uuid);
            }
        }
        return null;
    }


}

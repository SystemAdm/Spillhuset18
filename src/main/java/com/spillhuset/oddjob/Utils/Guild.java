package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.Managers.ConfigManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.CurrencySQL;
import com.spillhuset.oddjob.SQL.HomesSQL;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Guild {
    private final UUID uuid;
    private final int defaultClaims = ConfigManager.isSet("guilds.default.claims") ? ConfigManager.getInt("guilds.default.claims") : 10;
    private final int defaultOutpost = ConfigManager.isSet("guilds.default.outposts") ? ConfigManager.getInt("guilds.default.outposts") : 1;
    private final int defaultHomes = ConfigManager.isSet("guilds.default.homes") ? ConfigManager.getInt("guilds.default.homes") : 1;
    private int boughtClaims = 0;
    private int boughtOutposts = 0;
    private int boughtHomes = 0;
    private int usedOutposts = 0;
    private boolean spawnMobs = false;
    private boolean open = false;
    private boolean invited_only = false;
    private boolean friendlyFire = false;
    private Role permissionKick = Role.Members;
    private Role permissionInvite = Role.Members;
    private String name;
    private Zone zone = Zone.GUILD;

    /**
     * Created from loading
     *  @param uuid UUID
     * @param name String name
     */
    public Guild(UUID uuid, String name, Zone zone, int boughtClaims, int boughtHomes, boolean spawnMobs, boolean open, boolean invited_only, boolean friendly_fire, Role permission_kick, Role permission_invite, int boughtOutposts, int usedOutposts) {
        this.uuid = uuid;
        this.name = name;
        this.zone = zone;
        this.boughtClaims = boughtClaims;
        this.boughtHomes = boughtHomes;
        this.spawnMobs = spawnMobs;
        this.open = open;
        this.invited_only = invited_only;
        this.friendlyFire = friendly_fire;
        this.permissionKick = permission_kick;
        this.permissionInvite = permission_invite;
        this.boughtOutposts = boughtOutposts;
        this.usedOutposts = usedOutposts;
    }

    /**
     * Created from Command
     *
     * @param name   String name
     * @param player Player GuildMaster
     */
    public Guild(String name, Player player, Zone zone) {
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.zone = zone;

        if (player != null) {
            OddJob.getInstance().getCurrencyManager().initiateGuild(uuid);
        }
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nonnull
    public Zone getZone() {
        return this.zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Location getHome(@Nullable String name) {
        if (name == null) name = "home";
        return HomesSQL.get(getUuid(), name);
    }

    public void setHome(Location spawn, String name) {
        HomesSQL.add(getUuid(), spawn, name);
    }

    public boolean isSpawnMobs() {
        return spawnMobs;
    }

    public void setSpawnMobs(boolean spawnMobs) {
        this.spawnMobs = spawnMobs;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isInvited_only() {
        return invited_only;
    }

    public void setInvited_only(boolean invited_only) {
        this.invited_only = invited_only;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public Role getPermissionKick() {
        return permissionKick;
    }

    public void setPermissionKick(Role permissionKick) {
        this.permissionKick = permissionKick;
    }

    public Role getPermissionInvite() {
        return permissionInvite;
    }

    public void setPermissionInvite(Role permissionInvite) {
        this.permissionInvite = permissionInvite;
    }

    /**
     * Return current claims in size
     *
     * @return Integer size of claims
     */
    public int getClaims() {
        int i = 0;
        for (UUID uuid : OddJob.getInstance().getGuildsManager().getChunks().values()) {
            if (uuid.equals(getUuid())) {
                i++;
            }
        }
        return i;
    }

    /**
     * Returns the maximum amount of claims allowed
     *
     * @return Integer maximum number of claims
     */
    public int getMaxClaims() {
        int players = 0;
        int perMember = OddJob.getInstance().getConfig().getInt("guilds.multiplier.members.claims", 5);
        for (UUID uuid : OddJob.getInstance().getGuildsManager().getMembers().keySet()) {
            if (getUuid().equals(OddJob.getInstance().getGuildsManager().getMembers().get(uuid))) {
                players++;
            }
        }
        //          10       +       0      + (   1    *     5    ) = 15
        return defaultClaims + boughtClaims + (players * perMember);
    }

    /**
     * Increment number of bought claims
     */
    public void incMaxClaims() {
        boughtClaims++;
    }

    public void incMaxHomes() {
        boughtHomes++;
    }

    public void incMaxOutposts() {
        boughtOutposts++;
    }
    /**
     * Return current home size
     *
     * @return Integer size of homes
     */
    public int getHomes() {
        return HomesSQL.getList(getUuid()).size();
    }

    /**
     * Returns the maximum amount of homes
     *
     * @return Integer maximum number of homes
     */
    public int getMaxHomes() {
        return defaultHomes + boughtHomes;
    }

    public int getMaxOutposts() {
        int players = 0;
        int perMember = OddJob.getInstance().getConfig().getInt("guilds.multiplier.members.claims", 1);
        for (UUID uuid : OddJob.getInstance().getGuildsManager().getMembers().keySet()) {
            if (getUuid().equals(OddJob.getInstance().getGuildsManager().getMembers().get(uuid))) {
                players++;
            }
        }
        //          0       +       0      + (   1    *     1    ) = 15
        return defaultOutpost + boughtOutposts + (players * perMember);
    }


    public int getBoughtHomes() {
        return boughtHomes;
    }

    public int getBoughtClaims() {
        return boughtClaims;
    }

    public int getBoughtOutposts() {
        return boughtOutposts;
    }

    public double getCurrency() {
        return CurrencySQL.get(uuid, Account.guild);
    }

    public List<String> listHomes(UUID uuid) {
        return OddJob.getInstance().getHomeManager().getList(uuid);
    }

    public int getUsedOutposts() {
        return usedOutposts;
    }
}

package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Guild {
    private final UUID uuid;
    private int maxClaims = OddJob.getInstance().getConfig().getInt("guilds.default.max_claims");
    private Location spawn = null;
    private boolean spawnMobs;
    private boolean open;
    private boolean invited_only;
    private boolean friendlyFire;
    private Role permissionKick;
    private Role permissionInvite;
    private String name;
    private Zone zone;

    /**
     * Created from loading
     *
     * @param uuid UUID
     * @param name String name
     */
    public Guild(UUID uuid, String name, Zone zone, Location spawn, int maxClaims, boolean spawnMobs, boolean open, boolean invited_only, boolean friendly_fire, Role permission_kick, Role permission_invite) {
        this.uuid = uuid;
        this.name = name;
        this.zone = zone;
        this.maxClaims = maxClaims;
        this.spawn = spawn;
        this.spawnMobs = spawnMobs;
        this.open = open;
        this.invited_only = invited_only;
        this.friendlyFire = friendly_fire;
        this.permissionKick = permission_kick;
        this.permissionInvite = permission_invite;
    }

    /**
     * Created from Command
     *
     * @param name String name
     * @param player Player GuildMaster
     */
    public Guild(String name, Player player) {
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.zone = Zone.GUILD;

        if (player == null) {
            this.spawn = null;
        } else {
            this.spawn = player.getLocation();
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

    public Zone getZone() {
        return this.zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public int getMaxClaims() {
        return maxClaims;
    }

    public void setMaxClaims(int maxClaims) {
        this.maxClaims = maxClaims;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
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
}

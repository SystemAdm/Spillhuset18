package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.Managers.ConfigManager;
import com.spillhuset.oddjob.Managers.HomesManager;
import com.spillhuset.oddjob.OddJob;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Guild {
    private final UUID uuid;
    private String name;
    private Zone zone;
    private int boughtClaims;
    private int boughtHomes;
    private int boughtOutposts;
    private int usedOutposts;
    private boolean open;
    private boolean invitedOnly;
    private boolean friendlyFire;
    private boolean spawnMobs;
    private Role permissionInvite;
    private Role permissionKick;

    public Guild(UUID uuid, String name, Zone zone, int boughtClaims, int boughtHomes, boolean spawnMobs, boolean open, boolean invitedOnly, boolean friendlyFire, Role permissionKick, Role permissionInvite, int boughtOutposts, int usedOutposts) {
        this.uuid = uuid;
        this.name = name;
        this.zone = zone;
        this.boughtClaims = boughtClaims;
        this.boughtHomes = boughtHomes;
        this.spawnMobs = spawnMobs;
        this.open = open;
        this.invitedOnly = invitedOnly;
        this.friendlyFire = friendlyFire;
        this.permissionInvite = permissionInvite;
        this.permissionKick = permissionKick;
        this.boughtOutposts = boughtOutposts;
        this.usedOutposts = usedOutposts;
    }

    public Guild(UUID uuid, String name,Zone zone) {
        this.uuid = uuid;
        this.name = name;
        this.zone = zone;
        this.boughtClaims = 0;
        this.boughtHomes = 0;
        this.spawnMobs = false;
        this.open = false;
        this.invitedOnly = false;
        this.friendlyFire = false;
        this.permissionInvite = Role.Members;
        this.permissionKick = Role.Master;
        this.boughtOutposts = 0;
        this.usedOutposts = 0;
    }

    public Guild(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.zone = Zone.GUILD;
        this.boughtClaims = 0;
        this.boughtHomes = 0;
        this.spawnMobs = false;
        this.open = false;
        this.invitedOnly = false;
        this.friendlyFire = false;
        this.permissionInvite = Role.Members;
        this.permissionKick = Role.Master;
        this.boughtOutposts = 0;
        this.usedOutposts = 0;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isInvited_only() {
        return invitedOnly;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Zone getZone() {
        return zone;
    }

    public Role getPermissionKick() {
        return permissionKick;
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public Role getPermissionInvite() {
        return permissionInvite;
    }

    public int getBoughtClaims() {
        return boughtClaims;
    }

    public int getBoughtHomes() {
        return boughtHomes;
    }

    public boolean isSpawnMobs() {
        return spawnMobs;
    }

    public int getBoughtOutposts() {
        return boughtOutposts;
    }

    public int getUsedOutposts() {
        return usedOutposts;
    }

    public List<String> listHomes(UUID uuid) {
        return OddJob.getInstance().getHomesManager().getList(uuid);
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void useOutpost() {
        usedOutposts++;
    }
    public int getMaxClaims() {
        int max = ConfigManager.getInt("guilds.default.claims");
        max += getBoughtClaims();
        return max;
    }
    public int getMaxHomes() {
        int max = ConfigManager.getInt("guilds.default.homes");
        max += getBoughtHomes();
        return max;
    }

    public void incBoughtClaims() {
        boughtClaims++;
        OddJob.getInstance().getGuildsManager().save( this);
    }
}

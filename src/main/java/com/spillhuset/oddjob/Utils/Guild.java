package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Enums.Zone;
import com.spillhuset.oddjob.Managers.ConfigManager;
import com.spillhuset.oddjob.OddJob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Guild {
    private final UUID uuid;
    private String name;
    private final Zone zone;
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
    private boolean waterFlow;
    private boolean lavaFlow;

    public Guild(UUID uuid, String name, Zone zone, int boughtClaims, int boughtHomes, boolean spawnMobs, boolean open, boolean invitedOnly, boolean friendlyFire, Role permissionKick, Role permissionInvite, int boughtOutposts, int usedOutposts,boolean waterFlow,boolean lavaFlow) {
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
        this.waterFlow = waterFlow;
        this.lavaFlow = lavaFlow;
    }

    public Guild(UUID uuid, String name, Zone zone) {
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
        this.waterFlow = true;
        this.lavaFlow = true;
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
        this.waterFlow = false;
        this.lavaFlow = false;
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

    public List<String> listHomes() {
        return OddJob.getInstance().getHomesManager().getList(uuid);
    }

    public void setSpawnMobs(boolean spawnMobs) {
        this.spawnMobs = spawnMobs;
        save();
    }

    public void setInvitedOnly(boolean invitedOnly) {
        this.invitedOnly = invitedOnly;
        save();
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
        save();
    }

    public void setPermissionInvite(Role permissionInvite) {
        this.permissionInvite = permissionInvite;
        save();
    }

    public void setPermissionKick(Role permissionKick) {
        this.permissionKick = permissionKick;
        save();
    }

    public void setOpen(boolean open) {
        this.open = open;
        save();
    }

    public void setName(String name) {
        this.name = name;
        save();
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
        save();
    }

    public List<UUID> getMembers(UUID guild) {
        List<UUID> list = new ArrayList<>();
        HashMap<UUID, UUID> members = OddJob.getInstance().getGuildsManager().getMembers();
        for (UUID uuid : members.keySet()) {
            if (members.get(uuid).equals(guild)) {
                list.add(uuid);
            }
        }
        return list;
    }

    public void incBoughtHomes() {
        boughtHomes++;
        save();
    }

    public void incBoughtOutposts() {
        boughtOutposts++;
        save();
    }

    private void save() {
        OddJob.getInstance().getGuildsManager().save(this);
    }

    public void setFlowWater(boolean flow) {
        waterFlow = flow;
        save();
    }
    public boolean getFlowWater() {
        return waterFlow;
    }

    public boolean getFlowLava() {
        return lavaFlow;
    }

    public void setFlowLava(boolean flow) {
        lavaFlow = flow;
        save();
    }
}

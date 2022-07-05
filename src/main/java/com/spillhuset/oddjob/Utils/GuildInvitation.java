package com.spillhuset.oddjob.Utils;

import java.util.UUID;

public class GuildInvitation {
    private final UUID guild;
    private final UUID player;

    public GuildInvitation(UUID guild, UUID oddPlayer) {
        this.guild = guild;
        this.player = oddPlayer;
    }

    public UUID getGuildUUID() {
        return guild;
    }

    public UUID getPlayerUUID() {
        return player;
    }
}

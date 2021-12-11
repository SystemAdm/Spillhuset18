package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.OddJob;

public class Managers {
    protected boolean currency = false;
    protected boolean homes = false;
    protected boolean players = false;
    protected boolean teleport = false;

    public void load() {
        currency = OddJob.getInstance().getConfig().getBoolean("enabled.currency");
        homes = OddJob.getInstance().getConfig().getBoolean("enabled.homes");
        players = OddJob.getInstance().getConfig().getBoolean("enabled.players");
        teleport = OddJob.getInstance().getConfig().getBoolean("enabled.teleport");
    }
}


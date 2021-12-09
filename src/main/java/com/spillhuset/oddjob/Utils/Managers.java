package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.OddJob;

public class Managers {
    protected static boolean currency = false;
    protected static boolean homes = false;
    protected static boolean players = false;
    protected static boolean teleport = false;

    public static void load() {
        currency = OddJob.getInstance().getConfig().getBoolean("enabled.currency");
        homes = OddJob.getInstance().getConfig().getBoolean("enabled.homes");
        players = OddJob.getInstance().getConfig().getBoolean("enabled.players");
        teleport = OddJob.getInstance().getConfig().getBoolean("enabled.teleport");
    }
}


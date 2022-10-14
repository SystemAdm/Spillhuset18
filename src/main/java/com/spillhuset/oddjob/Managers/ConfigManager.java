package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.OddJob;

public class ConfigManager {

    public static boolean isSet(String name) {
        return OddJob.getInstance().getConfig().isSet(name);
    }

    public static int getInt(String name) {
        return OddJob.getInstance().getConfig().getInt(name);
    }

    public static void load() {

    }

    public static double getDouble(String name) {
        return OddJob.getInstance().getConfig().getDouble(name);
    }
}

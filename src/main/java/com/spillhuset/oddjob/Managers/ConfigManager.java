package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Response;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class ConfigManager {
    private static final double version = Double.parseDouble(OddJob.getInstance().getDescription().getVersion());

    public static void load() {
        FileConfiguration config = OddJob.getInstance().getConfig();

        if (config.getDouble("version") < version) {
            OddJob.getInstance().log("creating new config");
            config.set("version", version);
            if (!config.isSet("debug")) config.set("debug", true);
            if (!config.isSet("server_unique_id")) config.set("server_unique_id", UUID.randomUUID().toString());

            if (!config.isSet("enabled.homes")) config.set("enabled.homes", true);
            if (!config.isSet("enabled.currency")) config.set("enabled.currency", false);

            if (!config.isSet("currency.initial.pocket")) config.set("currency.initial.pocket", 200D);
            if (!config.isSet("currency.initial.bank")) config.set("currency.initial.bank", 200D);
            if (!config.isSet("currency.initial.guild")) config.set("currency.initial.guild", 200D);

            if (!config.isSet("sql.type")) config.set("sql.type", "mysql");
            if (!config.isSet("sql.prefix")) config.set("sql.prefix", "mine_");
            if (!config.isSet("sql.hostname")) config.set("sql.hostname", "localhost");
            if (!config.isSet("sql.port")) config.set("sql.port", 3306);
            if (!config.isSet("sql.minecraft")) config.set("sql.database", "minecraft");
            if (!config.isSet("sql.username")) config.set("sql.username", "root");
            if (!config.isSet("sql.password")) config.set("sql.password", "");

            if (!config.isSet("homes.ladder.default")) config.set("homes.ladder.default", 5);

            if (!config.isSet("homes.log.set")) config.set("homes.log.set", true);
            if (!config.isSet("homes.response")) config.set("homes.response", Response.ACTIONBAR.name());

            save();
        }
    }

    public static void save() {
        OddJob.getInstance().saveConfig();
        load();
    }

    public static int get(String string) {
        FileConfiguration config = OddJob.getInstance().getConfig();
        return config.getInt(string, 0);
    }
}

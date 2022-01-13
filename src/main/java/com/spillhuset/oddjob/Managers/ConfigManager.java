package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Response;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
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

            // Currency
            if (!config.isSet("currency.initial.pocket")) config.set("currency.initial.pocket", 200D);
            if (!config.isSet("currency.initial.bank")) config.set("currency.initial.bank", 200D);
            if (!config.isSet("currency.initial.guild")) config.set("currency.initial.guild", 200D);
            if (!config.isSet("currency.response")) config.set("currency.response", Response.ACTIONBAR.name());

            // SQL
            if (!config.isSet("sql.type")) config.set("sql.type", "mysql");
            if (!config.isSet("sql.prefix")) config.set("sql.prefix", "mine_");
            if (!config.isSet("sql.hostname")) config.set("sql.hostname", "localhost");
            if (!config.isSet("sql.port")) config.set("sql.port", 3306);
            if (!config.isSet("sql.database")) config.set("sql.database", "minecraft");
            if (!config.isSet("sql.username")) config.set("sql.username", "root");
            if (!config.isSet("sql.password")) config.set("sql.password", "");

            // Homes
            if (!config.isSet("homes.ladder.default")) config.set("homes.ladder.default", 5);
            if (!config.isSet("homes.log.set")) config.set("homes.log.set", true);
            if (!config.isSet("homes.response")) config.set("homes.response", Response.ACTIONBAR.name());
            if (!config.isSet("homes.world")) {
                List<String> list = new ArrayList<>();
                World world = Bukkit.getWorld("world");
                if (world != null) {
                    list.add(world.getUID().toString());
                }
                config.set("homes.world", list);
            }

            // Guilds
            if (!config.isSet("guilds.default.list")) config.set("guilds.default.list", 10);
            if (!config.isSet("guilds.default.claims")) config.set("guilds.default.claims", 10);
            if (!config.isSet("guilds.default.homes")) config.set("guilds.default.homes", 1);
            if (!config.isSet("guilds.multiplier.members.claims")) config.set("guilds.multiplier.members.claims", 5);
            if (!config.isSet("guilds.response")) config.set("guilds.response", Response.ACTIONBAR.name());
            if (!config.isSet("guilds.world")) {
                List<String> list = new ArrayList<>();
                World world = Bukkit.getWorld("world");
                if (world != null) {
                    list.add(world.getUID().toString());
                }
                config.set("guilds.world", list);
            }

            // Teleports
            if (!config.isSet("teleports.response")) config.set("teleports.response", Response.ACTIONBAR.name());

            // Warps
            if (!config.isSet("warps.response")) config.set("warps.response", Response.ACTIONBAR.name());

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

    public static double getDouble(String s) {
        return OddJob.getInstance().getConfig().getDouble(s);
    }

    public static int getInt(String s) {
        return OddJob.getInstance().getConfig().getInt(s);
    }

    public static boolean isSet(String s) {
        return OddJob.getInstance().getConfig().isSet(s);
    }
}

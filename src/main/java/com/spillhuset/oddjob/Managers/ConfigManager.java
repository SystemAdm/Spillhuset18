package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.OddJob;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class ConfigManager {

    public static boolean isSet(String name) {
        return OddJob.getInstance().getConfig().isSet(name);
    }

    public static int getInt(String name) {
        return OddJob.getInstance().getConfig().getInt(name);
    }
    public static String getString(String name) {
        return OddJob.getInstance().getConfig().getString(name);
    }

    public static void setConfig(String name, Object value) {
        FileConfiguration config = OddJob.getInstance().getConfig();
        if (!config.isSet(name)) config.set(name,value);
    }
    public static void load() {

        setConfig("currency.initial.pocket",1000);
        setConfig("currency.initial.bank",1000);
        setConfig("currency.initial.guild",1000);
        setConfig("homes.default",5);

        setConfig("sql.type","mysql");
        setConfig("sql.hostname","localhost");
        setConfig("sql.port",3306);
        setConfig("sql.database","minecraft");
        setConfig("sql.username","root");
        setConfig("sql.password","");

        setConfig("server.id", UUID.randomUUID().toString());

        setConfig("plugin.homes",true);
        setConfig("plugin.warps",false);
        setConfig("plugin.guilds",false);
        setConfig("plugin.shops",false);
        setConfig("plugin.currency",false);
        setConfig("plugin.locks",false);
        setConfig("plugin.teleport",false);
        setConfig("plugin.arena",false);
        setConfig("plugin.auctions",false);

        save();
    }

    public static double getDouble(String name) {
        return OddJob.getInstance().getConfig().getDouble(name);
    }

    public static boolean getBoolean(String configName) {
        return OddJob.getInstance().getConfig().getBoolean(configName);
    }

    public static void save() {
        OddJob.getInstance().saveConfig();
    }

    public static void reload() {
        OddJob.getInstance().reloadConfig();
    }

    public static String getString(String name, String def) {
        return OddJob.getInstance().getConfig().getString(name,def);
    }
}

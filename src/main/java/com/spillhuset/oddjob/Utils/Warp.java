package com.spillhuset.oddjob.Utils;

import org.bukkit.Location;

import java.util.UUID;

public class Warp {
    UUID uuid;
    Location location;
    double cost;
    String passwd;
    String name;

    public Warp(UUID uuid, Location location, double cost, String passwd, String name) {
        this.uuid = uuid;
        this.location = location;
        this.cost = cost;
        this.passwd = passwd;
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }


    public boolean matchPwd(String passwd) {
        return passwd.equals(this.passwd);
    }

    public String getName() {
        return name;
    }

    public boolean isProtected() {
        return !passwd.isEmpty();
    }

    public boolean hasCost() {
        return cost > 0.0;
    }

    public double getCost() {
        return cost;
    }
}

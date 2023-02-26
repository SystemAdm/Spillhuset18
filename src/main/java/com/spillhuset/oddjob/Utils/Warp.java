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

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getPasswd() {
        return this.passwd;
    }
}

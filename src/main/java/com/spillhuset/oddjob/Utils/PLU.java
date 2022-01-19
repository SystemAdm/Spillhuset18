package com.spillhuset.oddjob.Utils;

import org.bukkit.Material;

public class PLU {
    private final Material material;
    private double value = 2;
    private double rate = 2;

    public PLU(Material material) {
        this.material = material;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    public double getRate() {
        return this.rate;
    }

    public Material getMaterial() {
        return this.material;
    }
}

package com.spillhuset.oddjob.Enums;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Profession;
import org.bukkit.Material;

public class Price {
    Material material;
    double maximum;
    double normal;
    double minimum;
    Profession profession;
    int limit;
    boolean enabled;
    boolean buyAble;
    boolean sellAble;
    double resetValue;
    boolean singel;
    int stack;

    public Material getMaterial() {
        return material;
    }

    public int getStack() {
        return stack;
    }

    public void setStack(int stack) {
        this.stack = stack;
        OddJob.getInstance().getShopsManager().save(this);
    }

    public boolean isSingel() {
        return singel;
    }

    public void setSingel(boolean singel) {
        this.singel = singel;
        OddJob.getInstance().getShopsManager().save(this);
    }

    public double getMaximum() {
        return maximum;
    }

    public void setMaximum(double maximum) {
        this.maximum = maximum;
        OddJob.getInstance().getShopsManager().save(this);
    }

    public double getNormal() {
        return normal;
    }

    public void setNormal(double normal) {
        this.normal = normal;
        OddJob.getInstance().getShopsManager().save(this);
    }

    public double getMinimum() {
        return minimum;
    }

    public void setMinimum(double minimum) {
        this.minimum = minimum;
        OddJob.getInstance().getShopsManager().save(this);
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
        OddJob.getInstance().getShopsManager().save(this);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
        OddJob.getInstance().getShopsManager().save(this);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        OddJob.getInstance().getShopsManager().save(this);
    }

    public double getResetValue() {
        return resetValue;
    }

    public void setResetValue(double resetValue) {
        this.resetValue = resetValue;
        OddJob.getInstance().getShopsManager().save(this);
    }

    public Price(Material material, double priceMaximum, double priceNormal, double priceMinimum, Profession profession, int limit, boolean enabled, boolean buyAble, boolean sellAble, double resetValue, boolean singel, int stackSize) {
        this.material = material;
        this.maximum = priceMaximum;
        this.normal = priceNormal;
        this.minimum = priceMinimum;
        this.profession = profession;
        this.limit = limit;
        this.enabled = enabled;
        this.buyAble = buyAble;
        this.sellAble = sellAble;
        this.resetValue = resetValue;
        this.singel = singel;
        this.stack = stackSize;
    }

    public boolean isBuyAble() {
        return buyAble;
    }

    public void setBuyAble(boolean buyAble) {
        this.buyAble = buyAble;
        OddJob.getInstance().getShopsManager().save(this);
    }

    public boolean isSellAble() {
        return sellAble;
    }

    public void setSellAble(boolean sellAble) {
        this.sellAble = sellAble;
        OddJob.getInstance().getShopsManager().save(this);
    }
}

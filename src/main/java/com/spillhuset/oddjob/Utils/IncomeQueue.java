package com.spillhuset.oddjob.Utils;

import java.util.UUID;

public class IncomeQueue {
    double income = 0;
    UUID uuid;

    public IncomeQueue(double value, UUID uuid) {
        this.income = value;
        this.uuid = uuid;
    }

    public double value() {
        return income;
    }

    public void add(double value) {
        this.income += value;
    }

    public UUID getUuid() {
        return uuid;
    }
}

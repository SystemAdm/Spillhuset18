package com.spillhuset.oddjob.Enums;

public enum Plu {
    HOMES_SET(200);

    public double value;

    Plu(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}

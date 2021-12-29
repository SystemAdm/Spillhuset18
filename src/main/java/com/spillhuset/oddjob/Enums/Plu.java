package com.spillhuset.oddjob.Enums;

public enum Plu {
    HOMES_SET(200),
    TELEPORT_REQUEST(50),
    DEFAULT(10);

    public final double value;

    Plu(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}

package com.spillhuset.oddjob.Utils;

public class Tool {
    public static double round(double value) {
        long factor = (long) Math.pow(10, 0);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}

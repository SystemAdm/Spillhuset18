package com.spillhuset.oddjob.Enums;

public enum Account {
    pocket("pocket"), bank("bank"), guild("bank");

    final String type;

    Account(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

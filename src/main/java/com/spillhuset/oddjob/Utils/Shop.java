package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.SQL.ShopsSQL;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Shop {
    private final String uuid;
    private String name;
    private boolean open = false;
    private List<Profession> strict = new ArrayList<>();

    public Shop(String uuid, String name, boolean open, List<Profession> strict) {
        this.uuid = uuid;
        this.name = name;
        this.open = open;
        this.strict = strict;
    }

    public void addProfession(String prof) {
        try {
            Profession profession = Profession.valueOf(prof);
            strict.add(profession);
        } catch (NullPointerException ignored) {

        }
    }

    public void removeProfession(String prof) {
        try {
            Profession profession = Profession.valueOf(prof);
            strict.remove(profession);
        } catch (NullPointerException ignored) {

        }
    }

    public List<String> listProfessions() {
        List<String> list = new ArrayList<>();
        for (Profession profession : strict) {
            list.add(profession.name());
        }
        return list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        ShopsSQL.saveShop(this);
    }

    public Shop(String uuid,String name) {
        this.uuid = uuid;
        this.name = name;
    }


    public String getUuid() {
        return this.uuid;
    }
}

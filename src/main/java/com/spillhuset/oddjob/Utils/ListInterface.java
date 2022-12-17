package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.SQL.HomesSQL;
import com.spillhuset.oddjob.SQL.PlayerSQL;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface ListInterface {
    static void listHomes(List<String> list, UUID player, String name) {
        for (String home : HomesSQL.getList(player)) {
            if(home.toLowerCase().startsWith(name.toLowerCase())) {
                list.add(home);
            }
        }
    }

    static void playerList(List<String> list, String player, String me) {
        for (String name : PlayerSQL.getList()) {
            if (name.equalsIgnoreCase(me)) {
                continue;
            }
            if (player.toLowerCase().startsWith(player.toLowerCase())) {
                list.add(name);
            }
        }
    }
}

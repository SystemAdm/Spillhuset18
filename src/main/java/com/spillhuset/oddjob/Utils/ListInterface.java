package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.OddJob;

import java.util.List;
import java.util.UUID;

public interface ListInterface {

    static void listHomes(List<String> list, UUID uuid, String string) {
        List<String> homes = OddJob.getInstance().getHomeManager().getList(uuid);
        for (String home : homes) {
            if (string.isEmpty() || home.startsWith(string)) {
                list.add(home);
            }
        }
    }

    static void playerList(List<String> list, String string) {
        List<String> players = OddJob.getInstance().getPlayerManager().listString();
        for (String player : players) {
            if (string.isEmpty() || player.startsWith(string)) {
                list.add(player);
            }
        }
    }
}

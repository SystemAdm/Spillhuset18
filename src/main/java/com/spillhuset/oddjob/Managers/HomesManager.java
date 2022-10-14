package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.HomesSQL;
import com.spillhuset.oddjob.Utils.OddPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HomesManager {

    public List<String> getList(UUID uuid) {
        return HomesSQL.getList(uuid);
    }

    public void buy(Player player) {
        OddPlayer oddPlayer = OddJob.getInstance().getPlayerManager().get(player.getUniqueId());
        int bought = oddPlayer.getBoughtHomes();
        int max = oddPlayer.getMaxHomes();

        if (bought > max) {
            // TODO max
            return;
        }

        Plu plu = Plu.PLAYER_HOMES;

        double price = (bought * plu.getValue()) * plu.getMultiplier();

        if (!OddJob.getInstance().getCurrencyManager().get(player.getUniqueId())).checkBank(price)
    }
}

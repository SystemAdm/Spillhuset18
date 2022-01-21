package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.SQL.AuctionsSQL;
import com.spillhuset.oddjob.Utils.Tool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AuctionsManager {
    public void sell(Player seller, ItemStack itemStack, double bid, double buyout, int timeout) {
        AuctionsSQL.sell(seller.getUniqueId(), Tool.jsonEncode(itemStack),bid,buyout,timeout);
    }
}

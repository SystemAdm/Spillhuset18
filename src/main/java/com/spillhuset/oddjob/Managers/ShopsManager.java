package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.SQL.CurrencySQL;
import com.spillhuset.oddjob.SQL.ShopsSQL;
import com.spillhuset.oddjob.Utils.PLU;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopsManager {
    public void sell(Player player) {
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        Material material = itemStack.getType();
        //    8
        int count = itemStack.getAmount();

        PLU plu = ShopsSQL.get(material);
        //       16            2           8
        double amount = plu.getValue() * count;

        CurrencySQL.add(player.getUniqueId(), amount, Account.pocket);
        //                              8           2
        ShopsSQL.transaction(material,count, plu.getValue());
        //                  2                 2 * 8       / 1000
        plu.setValue(plu.getValue() - ((plu.getRate() * count) / 1000));
        player.getInventory().removeItem(itemStack);
        player.updateInventory();
        MessageManager.shops_sell(player,material,count,amount);
        ShopsSQL.set(plu);
    }
}

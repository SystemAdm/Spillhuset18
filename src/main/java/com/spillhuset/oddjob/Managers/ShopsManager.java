package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Price;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.ShopsSQL;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.Shop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ShopsManager {

    private final double INDEX_SELL =1.01;
    private final double INDEX_BUY = 0.09;
    private final double DIFFERENCE = 1.3;

    private final HashMap<UUID, UUID> trades = new HashMap<>();
    private final HashMap<UUID, Integer> values = new HashMap<>();
    public HashMap<String, Shop> shops = new HashMap<>();
    private final HashMap<String, Inventory> inventories = new HashMap<>();
    public HashMap<String, Price> priceList = new HashMap<>();


    public ShopsManager() {
        loadShops();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private ItemStack createItem(Price plu, int amount) {
        if (!plu.isEnabled()) return new ItemStack(Material.AIR);
        ItemStack is = new ItemStack(Material.valueOf(plu.getMaterial().name()), amount);
        ItemMeta im = is.getItemMeta();
        if (im == null) return new ItemStack(Material.AIR);
        List<String> lore = new ArrayList<>();
        if (plu.isSellAble()) {
            lore.add("Left Click to Sell: " + ChatColor.GOLD + plu.getNormal() * amount);
        } else lore.add("");
        if (plu.isBuyAble()) {
            lore.add("Right Click to Buy: " + ChatColor.GOLD + plu.getNormal() * DIFFERENCE * amount);
        } else lore.add("");
        OddJob.getInstance().log("buyable: "+plu.isBuyAble());
        OddJob.getInstance().log("normal: "+plu.getNormal());
        OddJob.getInstance().log("diff: "+DIFFERENCE);
        OddJob.getInstance().log("new: "+plu.getNormal()*DIFFERENCE);
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    private void loadShops() {
        priceList = ShopsSQL.loadPrices();
        shops = ShopsSQL.load();
        for (Shop shop : shops.values()) {
            int i = 0;
            Inventory inventory = (inventories.get(shop.getUuid()) != null) ? inventories.get(shop.getUuid()) : Bukkit.createInventory(null, 27, shop.getUuid() + " shop");
            for (Price plu : priceList.values()) {
                if (plu.getProfession() == null || (!shop.listProfessions().isEmpty() && !shop.listProfessions().contains(plu.getProfession().name()))) {
                    continue;
                }
                boolean singel = plu.isSingel();
                int stack = plu.getStack();
                if (!singel) inventory.setItem(i++, createItem(plu, stack));
                inventory.setItem(i++, createItem(plu, 1));
            }
            inventories.put(shop.getUuid(), inventory);
        }
    }

    private void saveShops() {
        ShopsSQL.save(shops);
    }

    public void commonShop(CommandSender sender, String arg) {
        Player player = (Player) sender;
        for (Shop shop : shops.values()) {
            if (shop.getName().equalsIgnoreCase(arg)) {
                player.openInventory(inventories.get(shop.getUuid()));
                return;
            }
        }
    }

    public void sell(Player player) {
        sell(player, player.getInventory().getItemInMainHand().getType(), player.getInventory().getItemInMainHand().getAmount(), priceList.get(player.getInventory().getItemInMainHand().getType().name()).getNormal());
    }

    public void sell(Player player, Material type, int amount, double price) {
        OddJob.getInstance().log("selling");
        Price plu = null;
        for (Price unit : priceList.values()) {
            if (unit.getMaterial().name().equalsIgnoreCase(type.name())) {
                plu = unit;
                break;
            }
        }
        if (plu == null) {
            OddJob.getInstance().log("wrong: " + type.name());
            return;
        }
        OddJob.getInstance().log("priceList");
        if (!plu.isEnabled()) {
            MessageManager.shops_not_sellable(player, type);
            return;
        }
        OddJob.getInstance().log("enabled");
        if (!plu.isSellAble() || plu.getNormal() == 0) {
            MessageManager.shops_not_sellable(player, type);
        } else {
            OddJob.getInstance().log("sellable");
            // Finding value for next transaction
            double temp = round(Math.max(plu.getNormal() * INDEX_SELL, plu.getMinimum()), 2);
            // Calculating the price
            price = price * amount;
            OddJob.getInstance().log("min: " + temp);
            // Creating item
            ItemStack is = new ItemStack(type, amount);
            // Does the player have the item
            if (player.getInventory().contains(is)) {
                // Making money transaction
                OddJob.getInstance().getCurrencyManager().add(player, Account.bank, price);
                // Finds the first item in players inventory
                int i = player.getInventory().first(is);
                // Removes the item from the player inventory
                player.getInventory().setItem(i, null);
                MessageManager.shops_sold_info(player, type, amount, price);
                // Setting the new transaction value
                plu.setNormal(temp);
                // Updates shop
                update();
            }
        }
    }

    public void update() {
        loadShops();
    }

    public void buy(Player player, ItemStack item) {

    }

    public void buy(Player player, Material material, int amount, double price) {
        Price plu = null;
        for (Price unit : priceList.values()) {
            if (unit.getMaterial().name().equalsIgnoreCase(material.name())) {
                plu = unit;
                break;
            }
        }
        if (plu == null) {
            OddJob.getInstance().log("wrong: " + material.name());
            return;
        }
        // Is enabled
        if (!plu.isEnabled()) {
            MessageManager.shops_not_sellable(player, material);
            return;
        }
        // Is buy able
        if (!plu.isBuyAble() || plu.getNormal() == 0) {
            MessageManager.shops_not_sellable(player, material);
        } else {
            double temp = round(Math.min(plu.getNormal() * INDEX_BUY, plu.getMaximum()), 2);

            ItemStack is = new ItemStack(material, amount);
            int i = player.getInventory().firstEmpty();
            if (i >= 0) {
                price = price * amount;
                OddJob.getInstance().getCurrencyManager().sub(player, Account.bank, price);
                player.getInventory().setItem(i, is);
                MessageManager.shops_bought_info(player, is, amount, price);
                plu.setNormal(temp);
            } else {
                player.closeInventory();
                MessageManager.shops_inventory_full(player);
            }
        }
    }

    public void getPrice(Player player, ItemStack item) {
        Price plu = null;
        for (Price unit : priceList.values()) {
            if (unit.getMaterial().name().equalsIgnoreCase(item.getType().name())) {
                plu = unit;
                break;
            }
        }
        if (plu == null) {
            OddJob.getInstance().log("wrong: " + item.getType().name());
            return;
        }
        if (!plu.isEnabled()) {
            MessageManager.shops_not_sellable(player, item.getType());
            return;
        }
        if (!plu.isSellAble() || plu.getNormal() == 0) {
            MessageManager.shops_not_sellable(player, item.getType());
        } else {
            int amount = item.getAmount();
            double priceSell = Math.round(plu.getNormal() * amount);
            double priceBuy = Math.round(priceSell * 1.1);
            double tempSell = Math.round(Math.max(plu.getNormal() * 0.9, plu.getMinimum()));
            double tempBuy = Math.round(Math.min((plu.getNormal() * 1.1) * 1.1, plu.getMaximum()));

            MessageManager.shops_price_sell(player, item, plu.getNormal(), amount, priceSell, tempSell);
            MessageManager.shops_price_buy(player, item, Math.round(plu.getNormal() * 1.1), amount, priceBuy, tempBuy);
        }
    }

    public void tradeRequest(CommandSender sender, Player player) {
        UUID trader = ((Player) sender).getUniqueId();

        // traded with earlier
        UUID old = trades.get(trader);
        OddPlayer target = OddJob.getInstance().getPlayerManager().get(old);
        if (old != null) {
            if (old.equals(player.getUniqueId())) {
                // trade cancelled
                MessageManager.shops_trade_cancelled(sender, target);
                //todo cancel
                trades.remove(trader);
                return;
            } else {
                // trade changed from old to new
                MessageManager.shops_trade_changed(sender, player, target);
            }
            // trade with old aborted
            MessageManager.shops_trade_aborted(sender, target);
        }

        MessageManager.shops_trade_created(sender, player);
        trades.put(trader, player.getUniqueId());
    }

    public ItemStack incOne() {
        ItemStack incOne = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta targetAddOneMeta = incOne.getItemMeta();
        targetAddOneMeta.setDisplayName("+10");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Click to add " + ChatColor.GOLD + 1 + ChatColor.GREEN + " to current value");
        targetAddOneMeta.setLore(lore);
        incOne.setItemMeta(targetAddOneMeta);
        return incOne;
    }

    public ItemStack incTen() {
        ItemStack incTen = new ItemStack(Material.GOLD_INGOT);
        ItemMeta targetAddTenMeta = incTen.getItemMeta();
        targetAddTenMeta.setDisplayName("x10");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Click to multiply by " + ChatColor.GOLD + 10 + ChatColor.GREEN + " to current value");
        targetAddTenMeta.setLore(lore);
        incTen.setItemMeta(targetAddTenMeta);
        return incTen;
    }

    public ItemStack decOne() {
        ItemStack decOne = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta targetDecOneMeta = decOne.getItemMeta();
        targetDecOneMeta.setDisplayName("-1");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED + "Click to sub " + ChatColor.GOLD + 1 + ChatColor.RED + " to current value");
        targetDecOneMeta.setLore(lore);
        decOne.setItemMeta(targetDecOneMeta);
        return decOne;
    }

    public ItemStack decTen() {
        ItemStack decTen = new ItemStack(Material.GOLD_INGOT);
        ItemMeta targetDecTenMeta = decTen.getItemMeta();
        targetDecTenMeta.setDisplayName("/10");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED + "Click to divide by " + ChatColor.GOLD + 10 + ChatColor.RED + " to current value");
        targetDecTenMeta.setLore(lore);
        decTen.setItemMeta(targetDecTenMeta);
        return decTen;
    }

    public void tradeAccept(Player target, Player trader) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Trading " + trader.getUniqueId());
        inventory.setItem(9, incTen());
        inventory.setItem(10, incOne());
        inventory.setItem(12, total(target.getUniqueId()));
        inventory.setItem(14, total(trader.getUniqueId()));
        inventory.setItem(16, decOne());
        inventory.setItem(17, decTen());
    }

    private ItemStack total(UUID uniqueId) {
        int i = values.get(uniqueId) != null ? values.get(uniqueId) : 0;
        ItemStack targetTotal = new ItemStack(Material.GOLD_INGOT);
        ItemMeta targetTotalMeta = targetTotal.getItemMeta();
        targetTotalMeta.setDisplayName(ChatColor.GOLD + String.valueOf(i));
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Current value: " + ChatColor.GOLD + i);
        targetTotalMeta.setLore(lore);
        targetTotal.setItemMeta(targetTotalMeta);
        return targetTotal;
    }

    public boolean tradeAction(Player player, Inventory inventory, ItemStack itemStack) {
        switch (itemStack.getItemMeta().getDisplayName()) {
            case "+1": {
                values.put(player.getUniqueId(), values.get(player.getUniqueId()) + 1);
            }
            case "-1": {
                values.put(player.getUniqueId(), values.get(player.getUniqueId()) - 1);
            }
            case "*10": {
                values.put(player.getUniqueId(), values.get(player.getUniqueId()) * 10);
            }
            case "/10": {
                values.put(player.getUniqueId(), values.get(player.getUniqueId()) / 10);
            }
        }

        return false;
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (Shop shop : shops.values()) {
            names.add(shop.getName());
        }
        return names;
    }

    public void create(CommandSender sender,String name) {
        for (Shop shop : shops.values()) {
            if (shop.getName().equalsIgnoreCase(name)) {
                MessageManager.shops_exists(sender,name);
                return;
            }
        }
        String uuid = UUID.randomUUID().toString().split("-")[0];
        Shop shop = new Shop(uuid, name);
        shops.put(uuid, shop);

        ShopsSQL.saveShop(shop);
        MessageManager.shops_created(sender,name);
    }

    @Nullable
    public Shop get(String uuid) {
        return shops.get(uuid);
    }


    public void addItem(CommandSender sender, Material name) {
        ShopsSQL.addItem(name);
        loadShops();
    }

    public void save(Price price) {
        ShopsSQL.savePrice(price);
    }
}

package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Price;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.SQL.ShopsSQL;
import com.spillhuset.oddjob.Utils.OddPlayer;
import com.spillhuset.oddjob.Utils.Shop;
import com.spillhuset.oddjob.Utils.TradeMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
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

    final double INDEX_SELL = 1.01;
    final double INDEX_BUY = 0.09;
    final double DIFFERENCE = 1.3;

    private final HashMap<UUID, UUID> tradeRequests = new HashMap<>();
    public final HashMap<UUID, Double> values = new HashMap<>();
    public HashMap<String, Shop> shops = new HashMap<>();
    public List<TradeMenu> tradeActive = new ArrayList<>();
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
        OddJob.getInstance().log("buyable: " + plu.isBuyAble());
        OddJob.getInstance().log("normal: " + plu.getNormal());
        OddJob.getInstance().log("diff: " + DIFFERENCE);
        OddJob.getInstance().log("new: " + plu.getNormal() * DIFFERENCE);
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

    public void tradeRequest(OddPlayer trading_oddplayer, OddPlayer want_to_trade_with) {
        UUID trading_uuid = trading_oddplayer.getUuid();
        Player sender = Bukkit.getPlayer(trading_oddplayer.getUuid());
        // traded with earlier
        UUID old_trading_with = tradeRequests.get(trading_uuid);
        if (old_trading_with != null) {
            OddJob.getInstance().log("old trade");
            if (old_trading_with.equals(want_to_trade_with.getUuid())) {
                // trade cancelled
                OddJob.getInstance().log("trade cancelled");
                MessageManager.shops_trade_cancelled(sender, want_to_trade_with);
                //todo cancel
                tradeRequests.remove(trading_uuid);
                return;
            } else {
                // trade changed from old to new
                OddJob.getInstance().log("trade changed");
                MessageManager.shops_trade_changed(sender, OddJob.getInstance().getPlayerManager().get(old_trading_with), want_to_trade_with);
            }
            // trade with old aborted
            OddJob.getInstance().log("request sent");
            MessageManager.shops_trade_aborted(sender, OddJob.getInstance().getPlayerManager().get(old_trading_with));
        }

        MessageManager.shops_trade_created(sender, want_to_trade_with);
        tradeRequests.put(trading_uuid, want_to_trade_with.getUuid());
    }

    public ItemStack incOne() {
        ItemStack incOne = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta targetAddOneMeta = incOne.getItemMeta();
        if (targetAddOneMeta != null) {
            targetAddOneMeta.setDisplayName("+1");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + "Click to increase the current value with " + ChatColor.GOLD + 1);
            targetAddOneMeta.setLore(lore);
        }
        incOne.setItemMeta(targetAddOneMeta);
        return incOne;
    }

    public ItemStack incTen() {
        ItemStack incTen = new ItemStack(Material.GOLD_INGOT);
        ItemMeta targetAddTenMeta = incTen.getItemMeta();
        if (targetAddTenMeta != null) {
            targetAddTenMeta.setDisplayName("x10");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + "Click to multiply the current value with " + ChatColor.GOLD + 10);
            targetAddTenMeta.setLore(lore);
        }
        incTen.setItemMeta(targetAddTenMeta);
        return incTen;
    }

    public ItemStack decOne() {
        ItemStack decOne = new ItemStack(Material.IRON_NUGGET);
        ItemMeta targetDecOneMeta = decOne.getItemMeta();
        if (targetDecOneMeta != null) {
            targetDecOneMeta.setDisplayName("-1");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.RED + "Click to subtract the current value with " + ChatColor.GOLD + 1);
            targetDecOneMeta.setLore(lore);
        }
        decOne.setItemMeta(targetDecOneMeta);
        return decOne;
    }

    public ItemStack decTen() {
        ItemStack decTen = new ItemStack(Material.IRON_INGOT);
        ItemMeta targetDecTenMeta = decTen.getItemMeta();
        if (targetDecTenMeta != null) {
            targetDecTenMeta.setDisplayName("/10");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.RED + "Click to divide the current value with " + ChatColor.GOLD + 10);
            targetDecTenMeta.setLore(lore);
        }
        decTen.setItemMeta(targetDecTenMeta);
        return decTen;
    }

    public ItemStack clear() {
        ItemStack clear = new ItemStack(Material.IRON_INGOT);
        ItemMeta clearMeta = clear.getItemMeta();
        if (clearMeta != null) {
            clearMeta.setDisplayName(">0<");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click to clear to zero ");
            clearMeta.setLore(lore);
        }
        clear.setItemMeta(clearMeta);
        return clear;
    }

    public void tradeAccept(Player target, Player trader) {
        TradeMenu menu = new TradeMenu(trader.getUniqueId(), target.getUniqueId());
        tradeActive.add(menu);
        trader.openInventory(menu.getInventory());
        target.openInventory(menu.getInventory());
    }

    public ItemStack total(UUID player, boolean trader) {

        ItemStack targetTotal = new ItemStack(Material.COPPER_INGOT);
        ItemMeta targetTotalMeta = targetTotal.getItemMeta();
        if (targetTotalMeta != null) {
            targetTotalMeta.setDisplayName(Bukkit.getPlayer(player).getName() + " " + ChatColor.GOLD + ((trader) ? "You got top!" : "You got bottom!"));
            List<String> lore = new ArrayList<>();
            lore.add("You have " + ChatColor.GOLD + OddJob.getInstance().getCurrencyManager().get(player, Account.pocket) + ChatColor.RESET + " in your pocket");
            lore.add("Trade value: " + ChatColor.GOLD + values.get(player));
            targetTotalMeta.setLore(lore);
        }
        targetTotal.setItemMeta(targetTotalMeta);
        return targetTotal;
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        for (Shop shop : shops.values()) {
            names.add(shop.getName());
        }
        return names;
    }

    public void create(CommandSender sender, String name) {
        for (Shop shop : shops.values()) {
            if (shop.getName().equalsIgnoreCase(name)) {
                MessageManager.shops_exists(sender, name);
                return;
            }
        }
        String uuid = UUID.randomUUID().toString().split("-")[0];
        Shop shop = new Shop(uuid, name);
        shops.put(uuid, shop);

        ShopsSQL.saveShop(shop);
        MessageManager.shops_created(sender, name);
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

    public ItemStack notReady() {
        ItemStack clear = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta clearMeta = clear.getItemMeta();
        if (clearMeta != null) {
            clearMeta.setDisplayName("NOT READY");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click when ready to trade");
            clearMeta.setLore(lore);
        }
        clear.setItemMeta(clearMeta);
        return clear;
    }

    public ItemStack ready(String name) {
        ItemStack clear = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta clearMeta = clear.getItemMeta();
        if (clearMeta != null) {
            clearMeta.setDisplayName(name + " IS READY, ARE YOU?");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Click to confirm to trade");
            clearMeta.setLore(lore);
        }
        clear.setItemMeta(clearMeta);
        return clear;
    }
}

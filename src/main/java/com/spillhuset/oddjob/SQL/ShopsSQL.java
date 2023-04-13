package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Enums.Price;
import com.spillhuset.oddjob.Managers.MySQLManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Profession;
import com.spillhuset.oddjob.Utils.Shop;
import org.bukkit.Material;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ShopsSQL extends MySQLManager {
    public static void save(HashMap<String, Shop> shops) {
        int i = 0;
        try {
            connect();
            for (Shop shop : shops.values()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String profession : shop.listProfessions()) {
                    stringBuilder.append(profession).append(",");
                }
                preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_shops` WHERE `uuid` = ? AND `server` = ?");
                preparedStatement.setString(1, shop.getUuid());
                preparedStatement.setString(2, server);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    preparedStatement = connection.prepareStatement("UPDATE `mine_shops` SET `name` = ?, `open` = ?, `strict` = ? WHERE `uuid` = ? AND `server` = ?");
                } else {
                    preparedStatement = connection.prepareStatement("INSERT INTO `mine_shops` (`name`,`open`,`strict`,`uuid`,`server`) VALUES (?,?,?,?,?)");
                }
                preparedStatement.setString(1, shop.getName());
                preparedStatement.setInt(2, shop.isOpen() ? 1 : 0);
                preparedStatement.setString(3, !stringBuilder.isEmpty() ? stringBuilder.substring(0, stringBuilder.lastIndexOf(",")) : "");
                preparedStatement.setString(4, shop.getUuid());
                preparedStatement.setString(5, server);
                preparedStatement.executeUpdate();
            }
            i++;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().log("saved " + i + " shops");
    }

    public static HashMap<String, Shop> load() {
        int i = 0;
        HashMap<String, Shop> shops = new HashMap<>();
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_shops` WHERE `server` = ? ");
            preparedStatement.setString(1, server);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String uuid;
                String name;
                boolean open;
                List<Profession> strict = new ArrayList<>();

                uuid = resultSet.getString("uuid");
                name = resultSet.getString("name");
                open = resultSet.getInt("open") == 1;
                for (String t : resultSet.getString("strict").split(",")) {
                    for (Profession profession : Profession.values()) {
                        if (profession.name().equals(name)) strict.add(profession);
                    }
                }

                shops.put(uuid, new Shop(uuid, name, open, strict));
                i++;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().log("loaded " + i + " shops");
        return shops;
    }

    public static void saveShop(Shop shop) {
        try {
            connect();

            StringBuilder stringBuilder = new StringBuilder();
            for (String profession : shop.listProfessions()) {
                stringBuilder.append(profession).append(",");
            }
            preparedStatement = connection.prepareStatement("SELECT `uuid` FROM `mine_shops` WHERE `uuid` = ? AND `server` = ?");
            preparedStatement.setString(1, shop.getUuid());
            preparedStatement.setString(2, server);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_shops` SET `name` = ?, `open` = ?, `strict` = ? WHERE `uuid` = ? AND `server` = ?");
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_shops` (`name`,`open`,`strict`,`uuid`,`server`) VALUES (?,?,?,?,?)");
            }
            preparedStatement.setString(1, shop.getName());
            preparedStatement.setInt(2, shop.isOpen() ? 1 : 0);
            preparedStatement.setString(3, !stringBuilder.isEmpty() ? stringBuilder.substring(0, stringBuilder.lastIndexOf(",")) : "");
            preparedStatement.setString(4, shop.getUuid());
            preparedStatement.setString(5, server);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        OddJob.getInstance().log("saved shop");
    }

    public static HashMap<String, Price> loadPrices() {
        HashMap<String, Price> prices = new HashMap<>();

        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_prices` WHERE `server` = ?");
            preparedStatement.setString(1, server);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                prices.put(resultSet.getString("material"), new Price(
                        Material.valueOf(resultSet.getString("material")),
                        resultSet.getDouble("maximum"),
                        resultSet.getDouble("normal"),
                        resultSet.getDouble("minimum"),
                        !Objects.equals(resultSet.getString("profession"), "") ? Profession.valueOf(resultSet.getString("profession")) : null,
                        resultSet.getInt("limit"),
                        resultSet.getInt("enabled") == 1,
                        resultSet.getInt("buyAble") == 1,
                        resultSet.getInt("sellAble") == 1,
                        resultSet.getDouble("reset"),
                        resultSet.getInt("singel") == 1,
                        resultSet.getInt("stack")
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }

        return prices;
    }

    public static void addItem(Material name) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_prices` (`material`,`server`) VALUES (?,?)");
            preparedStatement.setString(1, name.name());
            preparedStatement.setString(2, server);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void savePrice(Price price) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_prices` SET `maximum` = ?, `normal` = ?, `minimum` = ?, `profession` = ?, `limit` = ?, `enabled` = ?, `buyable` = ?, `sellable` = ?, `reset` = ?, `singel` = ?, `stack` = ? WHERE `material` = ? AND `server` = ?");
            preparedStatement.setDouble(1, price.getMaximum());
            preparedStatement.setDouble(2, price.getNormal());
            preparedStatement.setDouble(3, price.getMinimum());
            preparedStatement.setString(4, (price.getProfession() != null) ? price.getProfession().name(): "");
            preparedStatement.setInt(5, price.getLimit());
            preparedStatement.setInt(6, price.isEnabled() ? 1 : 0);
            preparedStatement.setInt(7, price.isBuyAble() ? 1 : 0);
            preparedStatement.setInt(8, price.isSellAble() ? 1 : 0);
            preparedStatement.setDouble(9, price.getResetValue());
            preparedStatement.setInt(10, price.isSingel() ? 1 : 0);
            preparedStatement.setInt(11, price.getStack());
            preparedStatement.setString(12, price.getMaterial().name());
            preparedStatement.setString(13, server);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }
}
package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;
import com.spillhuset.oddjob.Utils.GMIBSerialization;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.SQLException;

public class GMISql extends MySQLManager {

    public static void setInventory(Player player, GameMode gameMode) {
        try {
            connect();
            String inventory = GMIBSerialization.toDatabase(player.getInventory().getContents());
            String enderChest = GMIBSerialization.toDatabase(player.getEnderChest().getContents());
            String armor = GMIBSerialization.toDatabase(player.getInventory().getArmorContents());
            float xp = player.getExp();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_inventories` WHERE `uuid` = ? AND `gamemode` = ?");
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, gameMode.name());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_inventories` SET inventory = ?,`xp` = ?, `armor` = ?, `enderchest` = ? WHERE `uuid` = ? AND `gamemode` = ?");
                preparedStatement.setString(1, inventory);
                preparedStatement.setString(2, player.getUniqueId().toString());
                preparedStatement.setString(3, gameMode.name());
                preparedStatement.setFloat(4, xp);
                preparedStatement.setString(5, armor);
                preparedStatement.setString(6, enderChest);
                preparedStatement.executeUpdate();
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_inventories` (`uuid`,`gamemode`,`inventory`,`xp`,`armor`,`enderchest`) VALUES (?,?,?,?,?,?)");
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.setString(2, gameMode.name());
                preparedStatement.setString(3, inventory);
                preparedStatement.setFloat(4, xp);
                preparedStatement.setString(5, armor);
                preparedStatement.setString(6, enderChest);
                preparedStatement.executeUpdate();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }
    public static boolean getInventory(Player player, GameMode gameMode) {
        boolean ok = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_inventories` WHERE `uuid` = ? AND `gamemode` = ?");
            preparedStatement.setString(1,player.getUniqueId().toString());
            preparedStatement.setString(2, gameMode.name());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String savedInventory = resultSet.getString("inventory");
                String savedArmor = resultSet.getString("armor");
                String savedEnderChest = resultSet.getString("enderchest");

                int xp = resultSet.getInt("xp");
                player.setExp(xp);

                ItemStack[] stacks = GMIBSerialization.fromDatabase(savedInventory);
                player.getInventory().setContents(stacks);

                if (savedArmor != null) {
                    ItemStack[] armor = GMIBSerialization.fromDatabase(savedArmor);
                    player.getInventory().setArmorContents(armor);
                }
                if (savedEnderChest != null) {
                    ItemStack[] enderChest = GMIBSerialization.fromDatabase(savedEnderChest);
                    player.getEnderChest().setContents(enderChest);
                }
                ok = true;
            }
        }catch (SQLException | IOException ex) {
            ex.printStackTrace();
        }finally {
            close();
        }
        return ok;
    }

    public static void setInventory(Player player) {
        setInventory(player,player.getGameMode());
    }

    public static void getInventory(Player player) {
        getInventory(player,player.getGameMode());
    }
}

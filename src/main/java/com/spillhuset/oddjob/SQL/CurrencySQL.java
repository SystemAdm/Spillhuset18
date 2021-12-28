package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.Managers.ConfigManager;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.Managers.MySQLManager;

import java.sql.SQLException;
import java.util.UUID;

public class CurrencySQL extends MySQLManager {
    public static boolean has(UUID uuid) {
        boolean has = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_balances` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                has = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return has;
    }

    public static void initial(UUID uuid) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_balances` (`uuid`,`pocket`,`bank`) VALUES (?,?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setDouble(2, ConfigManager.getDouble("currency.initial.pocket"));
            preparedStatement.setDouble(3, ConfigManager.getDouble("currency.initial.bank"));
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void add(UUID uuid, Plu plu, Account account) {
        add(uuid, plu.getValue(), account);
    }

    public static void add(UUID uuid, double value, Account account) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `" + account.name() + "` = `" + account.name() + "` + ? WHERE `uuid` = ?");
            preparedStatement.setDouble(1, value);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        MessageManager.queueIncome(uuid,value);
    }
}

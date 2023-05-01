package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Managers.MySQLManager;

import java.sql.SQLException;
import java.util.UUID;

public class CurrencySQL extends MySQLManager {
    public static void set(UUID uuid, Account account, double value) {
        String query = "SELECT " + account.getType() + " FROM `mine_balances` WHERE `uuid` = ?";
        String update = "UPDATE `mine_balances` SET " + account.getType() + " = ? WHERE `uuid` = ?";
        String insert = "INSERT INTO `mine_balances` (" + account.getType() + ",`uuid`) VALUES (?,?)";
        try {
            connect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement(update);
            } else {
                preparedStatement = connection.prepareStatement(insert);
            }
            preparedStatement.setDouble(1, value);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static boolean has(UUID player, Account account, double value) {
        String query = "SELECT " + account.getType() + " FROM `mine_balances` WHERE `uuid` = ?";
        boolean ret = false;
        double bank;
        try {
            connect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, player.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                bank = resultSet.getDouble(account.name());
                if (bank >= value) {
                    ret = true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }

    public static double get(UUID uuid, Account account) {
        String query = "SELECT " + account.getType() + " FROM `mine_balances` WHERE `uuid` = ?";
        double value = 0.0;
        try {
            connect();
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                value = resultSet.getDouble(account.name());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return value;
    }
}


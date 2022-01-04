package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plu;
import com.spillhuset.oddjob.Managers.ConfigManager;
import com.spillhuset.oddjob.Managers.MySQLManager;

import java.sql.SQLException;
import java.util.UUID;

public class CurrencySQL extends MySQLManager {
    /**
     * Checks if the account exists
     *
     * @param uuid UUID of the account
     * @return Boolean if exists
     */
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

    /**
     * Initializing player account
     *
     * @param uuid UUID of the account
     */
    public static void initializePlayer(UUID uuid) {
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

    /**
     * Adds a value of a PLU to the account holder
     *
     * @param uuid    UUID of account
     * @param plu     PriceListUnit of transaction
     * @param account Account type
     */
    public static void add(UUID uuid, Plu plu, Account account) {
        add(uuid, plu.getValue(), account);
    }

    /**
     * Adds a value to the given account
     *
     * @param uuid    UUID of account
     * @param amount  Double amount
     * @param account Account type
     */
    public static void add(UUID uuid, double amount, Account account) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET " + account.name() + " = " + account.name() + " + ? WHERE `uuid` = ?");
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    /**
     * Makes a subtraction from the given account
     *
     * @param account Account type
     * @param uuid    UUID of account
     * @param amount  Double amount
     * @return Boolean if success
     */
    public static boolean sub(Account account, UUID uuid, double amount) {
        boolean ret = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET " + account.name() + " = " + account.name() + " - ? WHERE `uuid` = ?");
            preparedStatement.setDouble(1, amount);
            preparedStatement.setString(2, uuid.toString());
            ret = preparedStatement.executeUpdate() >= 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }

    /**
     * Check if account holder has the requested amount
     *
     * @param account Account type
     * @param uuid    UUID of account
     * @param amount  Double transaction amount
     * @return Boolean if success
     */
    public static boolean has(Account account, UUID uuid, double amount) {
        boolean ret = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_balances` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                ret = resultSet.getDouble(account.name()) >= amount;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return ret;
    }

    public static void initializeGuild(UUID uuid) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_balances` (`uuid`,`bank`,`guild`) VALUES (?,?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setDouble(2, ConfigManager.getDouble("currency.initial.guild"));
            preparedStatement.setInt(3, 1);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }
}

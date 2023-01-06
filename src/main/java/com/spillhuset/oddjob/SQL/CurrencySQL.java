package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;

import java.sql.SQLException;
import java.util.UUID;

public class CurrencySQL extends MySQLManager {
    public static boolean hasBank(UUID player, double value) {
        boolean ret = false;
        double bank;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `bank` FROM `mine_balances` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                bank = resultSet.getDouble("bank");
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

    public static void subBank(UUID player, double value) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `bank` = `bank` - ? WHERE `uuid` = ?");
            preparedStatement.setDouble(1, value);
            preparedStatement.setString(2, player.toString());
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static boolean hasPocket(UUID player, double value) {
        boolean ret = false;
        double bank;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `pocket` FROM `mine_balances` WHERE `uuid` = ?");
            preparedStatement.setString(1, player.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                bank = resultSet.getDouble("pocket");
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

    public static void subPocket(UUID player, double value) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `pocket` = `pocket` - ? WHERE `uuid` = ?");
            preparedStatement.setDouble(1, value);
            preparedStatement.setString(2, player.toString());
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static double getPocket(UUID uuid) {
        double value = 0.0;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `pocket` FROM `mine_balances` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                value = resultSet.getDouble("pocket");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return value;
    }

    public static double getBank(UUID uuid) {
        double value = 0.0;
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT `bank` FROM `mine_balances` WHERE `uuid` = ?");
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                value = resultSet.getDouble("bank");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return value;
    }

    public static void addBank(UUID uuid, double value) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `bank` = `bank` + ? WHERE `uuid` = ?");
            preparedStatement.setDouble(1, value);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void addPocket(UUID uuid, double value) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("UPDATE `mine_balances` SET `pocket` = `pocket` + ? WHERE `uuid` = ?");
            preparedStatement.setDouble(1, value);
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }
}


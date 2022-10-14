package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;
import org.bukkit.Material;

import java.sql.SQLException;

public class ShopsSQL extends MySQLManager {

    public static PLU get(Material material) {
        PLU plu = new PLU(material);
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_plu` WHERE `material` = ?");
            preparedStatement.setString(1, material.name());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                plu.setValue(resultSet.getDouble("value"));
                plu.setRate(resultSet.getDouble("rate"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return plu;
    }

    public static void set(PLU plu) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("SELECT * FROM `mine_plu` WHERE `material` = ?");
            preparedStatement.setString(1, plu.getMaterial().name());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                preparedStatement = connection.prepareStatement("UPDATE `mine_plu` SET `value` = ?, `rate` = ? WHERE `material` = ?");
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO `mine_plu` (`value`,`rate`,`material`) VALUES (?,?,?)");
            }
            preparedStatement.setDouble(1, plu.getValue());
            preparedStatement.setDouble(2, plu.getRate());
            preparedStatement.setString(3, plu.getMaterial().name());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }

    public static void transaction(Material material, int count, double value) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO `mine_trans` (`material`,`price`,`counting`,timetag) VALUES (?,?,?,UNIX_TIMESTAMP())");
            preparedStatement.setString(1, material.name());
            preparedStatement.setDouble(2, value);
            preparedStatement.setInt(3, count);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }
}

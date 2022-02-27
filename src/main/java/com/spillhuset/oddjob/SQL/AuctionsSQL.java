package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;

import java.sql.SQLException;
import java.util.UUID;

public class AuctionsSQL extends MySQLManager {

    public static boolean sell(UUID player, String string, double bid, double buyout, int timeout) {
        boolean success = false;
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO mine_auction (seller, item, value, expire,time,buyout) VALUES (?,?,?,?,?,?)");
            preparedStatement.setString(1,player.toString());
            preparedStatement.setString(2,string);
            preparedStatement.setDouble(3,bid);
            preparedStatement.setDouble(4,timeout);
            preparedStatement.setLong(5,System.currentTimeMillis()/1000);
            preparedStatement.setDouble(6,buyout);
            preparedStatement.executeUpdate();
            success = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
        return success;
    }
}

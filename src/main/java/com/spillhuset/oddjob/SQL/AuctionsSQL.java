package com.spillhuset.oddjob.SQL;

import com.spillhuset.oddjob.Managers.MySQLManager;

import java.sql.SQLException;
import java.util.UUID;

public class AuctionsSQL extends MySQLManager {

    public static void sell(UUID uniqueId, String jsonEncode, double bid, double buyout, int timeout) {
        try {
            connect();
            preparedStatement = connection.prepareStatement("INSERT INTO mine_auction (seller, item, value, expire,time,buyout) VALUES (?,?,?,?,?,?)");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            close();
        }
    }
}

package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.OddJob;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQLManager {
    private static String hostname = "localhost";
    private static int port = 3306;
    private static String database = "minecraft";
    private static String username = "root";
    private static String password = "";
    private static String type = "mysql";

    protected static String server = ConfigManager.getString("server.id");

    protected static Connection connection;
    protected static PreparedStatement preparedStatement;
    protected static ResultSet resultSet;
    private static DataSource dataSource;

    public MySQLManager() {

    }

    public static void connect() throws SQLException {
        FileConfiguration config = OddJob.getInstance().getConfig();

        hostname = config.getString("sql.hostname");
        port = config.getInt("sql.port");
        database = config.getString("sql.database");
        username = config.getString("sql.username");
        password = config.getString("sql.password");
        type = config.getString("sql.type");

        getDataSource();
        if (connection == null) {
            connection = dataSource.getConnection();
        }
        //OddJob.getInstance().log("connection: ok");
    }

    public static void getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:" + type + "://" + hostname + ":" + port + "/" + database);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setAutoCommit(true);
            config.setMaximumPoolSize(10);
            OddJob.getInstance().log(config.getJdbcUrl());
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            dataSource = new HikariDataSource(config);
            //OddJob.getInstance().log("pools: ok");
        }
    }

    public static void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (preparedStatement != null) {
                preparedStatement.close();
                preparedStatement = null;
            }
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //OddJob.getInstance().log("close: ok");

    }

    public static String serialize(final ItemStack item) {
        /**/
        return null;
    }

    public static ItemStack deserialize(final String string) {
        /*
        if (string == null || string.equals("empty")) {
            return null;
        }
        NBTTagCompound comp = MojangsonParser.parse(string);
        net.minecraft.world.item.ItemStack cis = net.minecraft.world.item.ItemStack.a(comp);
        return CraftItemStack.asBukkitCopy(cis);*/
        return null;
    }

    public static String assemble(List<UUID> list) {
        StringBuilder stringBuilder = new StringBuilder();
        if (list.isEmpty()) return "";
        for (UUID uuid : list) {
            stringBuilder.append(uuid.toString()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        return stringBuilder.toString();
    }

    public static List<UUID> deAssemble(String string) {
        List<UUID> list = new ArrayList<>();
        if (string.isEmpty()) return list;
        for (String unit : string.split(",")) {
            list.add(UUID.fromString(unit));
        }
        return list;
    }
}

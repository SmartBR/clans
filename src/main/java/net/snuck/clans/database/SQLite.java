package net.snuck.clans.database;

import net.snuck.clans.Main;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite implements IData{

    private Connection connection;

    public Connection getConnection() {
        return this.connection;
    }

    public void open() {
        String url = "";
        File file = new File(Main.getPlugin().getDataFolder(), "storage.db");
        url = "jdbc:sqlite:" + file;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package net.snuck.clans.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL implements IData {

    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String database;
    private Connection connection;

    public MySQL(String host, int port, String user, String password, String database) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void open() {
        String url = "";
        try {
            url = "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase();
            connection = DriverManager.getConnection(url, getUser(), getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (this.connection != null && !(this.connection.isClosed())) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }
}

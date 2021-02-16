package net.snuck.clans.database;

import lombok.Data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
@Data
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

    public void open() {
        try {
            String url = "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase();
            connection = DriverManager.getConnection(url, getUser(), getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

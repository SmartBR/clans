package net.snuck.clans.database;

import java.sql.Connection;

public interface IData {

    Connection getConnection();
    void open();
    void close();

}

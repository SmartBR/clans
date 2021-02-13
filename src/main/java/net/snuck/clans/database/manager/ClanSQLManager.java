package net.snuck.clans.database.manager;

import net.snuck.clans.Main;
import net.snuck.clans.object.Clan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClanSQLManager {

    public static void deleteClanById(String id) {
        PreparedStatement st;
        try {
            Connection connection = Main.getIData().getConnection();
            st = connection.prepareStatement("DELETE FROM clans WHERE id = ?");
            st.setString(1, id);
            st.executeUpdate();

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertClan(String id, String tag, String name) {
        PreparedStatement st;
        try {

            Connection connection = Main.getIData().getConnection();
            st = connection.prepareStatement("INSERT INTO clans (id, tag, name) VALUES (?, ?, ?)");
            st.setString(1, id);
            st.setString(2, tag);
            st.setString(3, name);
            st.executeUpdate();
            st.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Clan getClanByName(String clanName) {
        PreparedStatement st;
        try {
            Connection connection = Main.getIData().getConnection();
            st = connection.prepareStatement("SELECT * FROM clans WHERE name = ?");
            st.setString(1, clanName);

            ResultSet rs = st.executeQuery();

            if(rs.next()) {
                return new Clan(rs.getString("id"), rs.getString("tag"), rs.getString("name"));
            }

            st.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Clan getClanById(String clanId) {
        PreparedStatement st;
        try {
            Connection connection = Main.getIData().getConnection();
            st = connection.prepareStatement("SELECT * FROM clans WHERE id = ?");
            st.setString(1, clanId);

            ResultSet rs = st.executeQuery();

            if(rs.next()) {
                return new Clan(rs.getString("id"), rs.getString("tag"), rs.getString("name"));
            }

            st.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean hasClan(String clanId) {
        PreparedStatement st;
        try {
            Connection connection = Main.getIData().getConnection();
            st = connection.prepareStatement("SELECT id FROM clans WHERE id = ?");
            st.setString(1, clanId);

            ResultSet rs = st.executeQuery();
            boolean next = rs.next();

            rs.close();
            st.close();

            return next;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasClanWithName(String name) {
        PreparedStatement st;
        try {
            Connection connection = Main.getIData().getConnection();
            st = connection.prepareStatement("SELECT name FROM clans WHERE name = ?");
            st.setString(1, name);

            ResultSet rs = st.executeQuery();
            boolean next = rs.next();

            rs.close();
            st.close();

            return next;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean hasClanWithTag(String tag) {
        PreparedStatement st;
        try {
            Connection connection = Main.getIData().getConnection();
            st = connection.prepareStatement("SELECT tag FROM clans WHERE tag = ?");
            st.setString(1, tag);

            ResultSet rs = st.executeQuery();
            boolean next = rs.next();

            rs.close();
            st.close();

            return next;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

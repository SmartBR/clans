package net.snuck.clans.database.manager;

import net.snuck.clans.Main;
import net.snuck.clans.object.Clan;
import net.snuck.clans.object.ClanPlayer;
import net.snuck.clans.type.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlayerSQLManager {

    public static boolean hasPlayer(String playerId) {

        PreparedStatement st;
        try {
            Connection connection = Main.getiData().getConnection();
            st = connection.prepareStatement("SELECT uuid FROM users WHERE uuid = ?");
            st.setString(1, playerId);

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

    public static void insertPlayer(String playerId, String clanId, Role role) {
        PreparedStatement st;
        try {
            Connection connection = Main.getiData().getConnection();
            st = connection.prepareStatement("INSERT INTO users (uuid, clan_id, role) VALUES (?, ?, ?)");
            st.setString(1, playerId);
            st.setString(2, clanId);
            st.setString(3, role.toString());
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deletePlayer(String playerId) {
        PreparedStatement st;
        try {
            Connection connection = Main.getiData().getConnection();
            st = connection.prepareStatement("DELETE FROM users WHERE uuid = ?");
            st.setString(1, playerId);
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<ClanPlayer> getAllPlayers(String clanId) {
        PreparedStatement st;

        List<ClanPlayer> result = new ArrayList<>();

        try {
            Connection connection = Main.getiData().getConnection();
            st = connection.prepareStatement("SELECT * FROM users WHERE clan_id = ?");
            st.setString(1, clanId);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                Clan clan = rs.getString("clan_id").equals("") ? null : Main.getClanCache().get(rs.getString("clan_id"));

                ClanPlayer cp = new ClanPlayer(rs.getString("uuid"), rs.getString("clan_id"), Role.valueOf(rs.getString("role")), clan);

                result.add(cp);
            }

            st.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ClanPlayer getPlayer(String playerId) {
        PreparedStatement st;

        try {
            Connection connection = Main.getiData().getConnection();
            st = connection.prepareStatement("SELECT * FROM users WHERE uuid = ?");
            st.setString(1, playerId);
            ResultSet rs = st.executeQuery();

            if(rs.next()) {
                if(rs.getString("clan_id").equals("") || rs.getString("clan_id") == null) {
                    return new ClanPlayer(rs.getString("uuid"), rs.getString("clan_id"), Role.valueOf(rs.getString("role")), null);
                } else {
                    Clan playerClan = ClanSQLManager.getClanById(rs.getString("clan_id"));
                    return new ClanPlayer(rs.getString("uuid"), rs.getString("clan_id"), Role.valueOf(rs.getString("role")), playerClan);
                }
            }

            st.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createTable(String table, String columns) {
        try {
            Connection connection = Main.getiData().getConnection();
            PreparedStatement st = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `" + table + "` (" + columns + ")");
            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

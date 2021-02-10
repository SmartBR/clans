package net.snuck.clans.database.manager;

import net.snuck.clans.Main;
import net.snuck.clans.object.Clan;
import net.snuck.clans.object.ClanPlayer;
import net.snuck.clans.object.Invite;
import net.snuck.clans.type.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CacheManager {

    public static void loadUsersCache() {
        PreparedStatement st;
        try {
            Connection connection = Main.getiData().getConnection();
            st = connection.prepareStatement("SELECT * FROM users");

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Main.getPlayerCache().put(rs.getString("uuid"), new ClanPlayer(rs.getString("uuid"), rs.getString("clan_id"), Role.valueOf(rs.getString("role")), ClanSQLManager.getClanById(rs.getString("clan_id"))));
            }

            st.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadClansCache() {
        PreparedStatement st;
        try {
            Connection connection = Main.getiData().getConnection();
            st = connection.prepareStatement("SELECT * FROM clans");

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Main.getClanCache().put(rs.getString("id"), new Clan(rs.getString("id"), rs.getString("tag"), rs.getString("name")));
            }

            st.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadInvitesCache() {
        PreparedStatement st;
        try {
            Connection connection = Main.getiData().getConnection();
            st = connection.prepareStatement("SELECT * FROM invites");

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                ClanPlayer cp = Main.getPlayerCache().get(rs.getString("player_id"));
                Clan clan = Main.getClanCache().get(rs.getString("clan_id"));

                Main.getInviteCache().put(cp, new Invite(cp, clan));
            }

            st.close();
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveInvitesCache() {
        Main.getInviteCache().forEach(((clanPlayer, invite) -> {
            invite.save();
        }));
    }

    public static List<ClanPlayer> getPlayersFromClan(String clanId) {

        List<ClanPlayer> list = new ArrayList<>();

        Main.getPlayerCache().forEach((uuid, cp) -> {
            if(cp.getClanId().equals(clanId)) {
                list.add(cp);
            }
        });
        return list;
    }

    public static void saveClansCache() {
        Main.getClanCache().forEach((id, clan) -> {
            clan.save();
        });
    }

    public static void saveUsersCache() {
        Main.getPlayerCache().forEach((uuid, clanPlayer) -> clanPlayer.save());
    }

}

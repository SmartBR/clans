package net.snuck.clans.database.manager;

import net.snuck.clans.Main;
import net.snuck.clans.object.Invite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InviteSQLManager {

    public static void insertInvite(Invite invite) {
        PreparedStatement st;
        try {
            Connection connection = Main.getiData().getConnection();
            st = connection.prepareStatement("INSERT INTO invites (player_id, clan_id) VALUES (?, ?)");
            st.setString(1, invite.getReceiver().getId());
            st.setString(2, invite.getInvitedTo().getId());
            st.executeUpdate();

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeInvite(String playerId, String clanId) {
        PreparedStatement st;
        try {
            Connection connection = Main.getiData().getConnection();
            st = connection.prepareStatement("DELETE FROM invites WHERE player_id = ? AND clan_id = ?");
            st.setString(1, playerId);
            st.setString(2, clanId);

            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasInvite(Invite invite) {
        PreparedStatement st;
        try {
            Connection connection = Main.getiData().getConnection();
            st = connection.prepareStatement("SELECT * FROM invites WHERE player_id = ? AND clan_id = ?");
            st.setString(1, invite.getReceiver().getId());
            st.setString(2, invite.getInvitedTo().getId());

            ResultSet rs = st.executeQuery();
            boolean result = rs.next();

            st.close();
            rs.close();

            return result;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

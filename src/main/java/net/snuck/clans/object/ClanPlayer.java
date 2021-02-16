package net.snuck.clans.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.snuck.clans.Main;
import net.snuck.clans.database.manager.InviteSQLManager;
import net.snuck.clans.type.Role;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@AllArgsConstructor @Data
public class ClanPlayer {

    private String id;
    private String clanId;
    private Role role;
    private Clan clan;

    public boolean hasClan() {
        return !getClanId().equals("");
    }

    public boolean hasInviteForClan(String clanId) {
        Clan invitedTo = Main.getClanCache().get(clanId);

        Invite invite = new Invite(this, invitedTo);
        return InviteSQLManager.hasInvite(invite);
    }

    public void save() {
        PreparedStatement st;
        try {
            Connection connection = Main.getIData().getConnection();
            st = connection.prepareStatement("UPDATE users SET clan_id = ?, role = ? WHERE uuid = ?");
            st.setString(1, this.clanId);
            st.setString(2, this.role.toString());
            st.setString(3, this.id);

            st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ClanPlayer{" +
                "id='" + id + '\'' +
                ", clanId='" + clanId + '\'' +
                ", role=" + role +
                ", clan=" + clan +
                '}';
    }
}

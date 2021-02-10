package net.snuck.clans.object;


import net.snuck.clans.Main;
import net.snuck.clans.database.manager.InviteSQLManager;
import net.snuck.clans.type.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClanPlayer {

    private String id;
    private String clanId;
    private Role role;
    private Clan clan;

    public ClanPlayer(String id, String clanId, Role role, Clan clan) {
        this.id = id;
        this.clanId = clanId;
        this.role = role;
        this.clan = clan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClanId() {
        return clanId;
    }

    public void setClanId(String clanId) {
        this.clanId = clanId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

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
            Connection connection = Main.getiData().getConnection();
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

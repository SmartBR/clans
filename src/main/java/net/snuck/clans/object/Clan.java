package net.snuck.clans.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.snuck.clans.Main;
import net.snuck.clans.database.manager.ClanSQLManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@AllArgsConstructor @Data
public class Clan {

    private String id;
    private String tag;
    private String name;

    public void delete() {
        PreparedStatement st;
        try {
            Connection connection = Main.getIData().getConnection();
            st = connection.prepareStatement("DELETE FROM clans WHERE id = ?");
            st.setString(1, this.id);
            st.executeUpdate();

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        PreparedStatement st;
        try {
            if(!ClanSQLManager.hasClan(this.id)) {
                ClanSQLManager.insertClan(this.id, this.tag, this.name);
                return;
            }

            Connection connection = Main.getIData().getConnection();
            st = connection.prepareStatement("UPDATE clans SET id = ?, tag = ?, name = ? WHERE id = ?");
            st.setString(1, this.id);
            st.setString(2, this.tag);
            st.setString(3, this.name);
            st.setString(4, this.id);
            st.executeUpdate();

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Clan{" +
                "id='" + id + '\'' +
                ", tag='" + tag + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

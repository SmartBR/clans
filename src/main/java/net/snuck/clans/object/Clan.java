package net.snuck.clans.object;


import net.snuck.clans.Main;
import net.snuck.clans.database.manager.ClanSQLManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Clan {

    private String id;
    private String tag;
    private String name;

    public Clan(String id, String tag, String name) {
        this.id = id;
        this.tag = tag;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void delete() {
        PreparedStatement st;
        try {
            Connection connection = Main.getiData().getConnection();
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
            } else {

                Connection connection = Main.getiData().getConnection();
                st = connection.prepareStatement("UPDATE clans SET id = ?, tag = ?, name = ? WHERE id = ?");
                st.setString(1, this.id);
                st.setString(2, this.tag);
                st.setString(3, this.name);
                st.setString(4, this.id);
                st.executeUpdate();

                st.close();
            }
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

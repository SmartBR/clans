package net.snuck.clans.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.snuck.clans.database.manager.InviteSQLManager;

@AllArgsConstructor @Data
public class Invite {

    private ClanPlayer receiver;
    private Clan invitedTo;

    public void save() {
        if(!InviteSQLManager.hasInvite(this)) {
            InviteSQLManager.insertInvite(this);
        }
    }

    @Override
    public String toString() {
        return "Invite{" +
                "receiver=" + receiver +
                ", invitedTo=" + invitedTo +
                '}';
    }
}

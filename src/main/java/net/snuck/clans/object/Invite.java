package net.snuck.clans.object;

import net.snuck.clans.database.manager.InviteSQLManager;

public class Invite {

    private ClanPlayer receiver;
    private Clan invitedTo;

    public Invite(ClanPlayer receiver, Clan invitedTo) {
        this.receiver = receiver;
        this.invitedTo = invitedTo;
    }

    public ClanPlayer getReceiver() {
        return receiver;
    }

    public void setReceiver(ClanPlayer receiver) {
        this.receiver = receiver;
    }

    public Clan getInvitedTo() {
        return invitedTo;
    }

    public void setInvitedTo(Clan invitedTo) {
        this.invitedTo = invitedTo;
    }

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

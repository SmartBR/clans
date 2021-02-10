package net.snuck.clans.api.invite;

import net.snuck.clans.database.manager.InviteSQLManager;
import net.snuck.clans.object.Invite;

public class InviteSqlAPI {

    public static void insertInvite(Invite invite) {
        InviteSQLManager.insertInvite(invite);
    }

    public static void deleteInvite(String playerId, String clanId) {
        InviteSQLManager.removeInvite(playerId, clanId);
    }

    public static void deleteInvite(Invite invite) {
        InviteSQLManager.removeInvite(invite.getReceiver().getId(), invite.getInvitedTo().getId());
    }

    public static boolean hasInvite(Invite invite) {
        return InviteSQLManager.hasInvite(invite);
    }

}

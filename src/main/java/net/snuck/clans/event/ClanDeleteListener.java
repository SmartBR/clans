package net.snuck.clans.event;

import net.snuck.clans.api.event.ClanDeleteEvent;
import net.snuck.clans.database.manager.InviteSQLManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClanDeleteListener implements Listener {

    @EventHandler
    public void on(ClanDeleteEvent e) {
        InviteSQLManager.removeInviteWithClanId(e.getClan().getId());
    }

}

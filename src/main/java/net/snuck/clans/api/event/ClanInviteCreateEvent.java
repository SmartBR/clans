package net.snuck.clans.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.snuck.clans.object.Clan;
import net.snuck.clans.object.Invite;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class ClanInviteCreateEvent extends Event {

    @Getter private final HandlerList handlerList = new HandlerList();

    @Getter private Player player;
    @Getter private Clan clan;
    @Getter private Invite invite;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}

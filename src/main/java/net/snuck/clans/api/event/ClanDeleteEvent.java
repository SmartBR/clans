package net.snuck.clans.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.snuck.clans.object.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
public class ClanDeleteEvent extends Event {

    @Getter private final HandlerList handlerList = new HandlerList();

    @Getter private Player player;
    @Getter private Clan clan;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}

package net.snuck.clans.event;

import net.snuck.clans.Main;
import net.snuck.clans.object.ClanPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeftListener implements Listener {

    @EventHandler
    public void on(PlayerQuitEvent e) {

        Player p = e.getPlayer();
        ClanPlayer cp = Main.getPlayerCache().get(p.getUniqueId().toString());

        cp.save();

        Main.getPlayerCache().remove(p.getUniqueId().toString());

    }

}

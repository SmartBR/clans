package net.snuck.clans.event;

import net.snuck.clans.Main;
import net.snuck.clans.object.ClanPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageListener implements Listener {

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {

            Player p = (Player) e.getDamager();
            Player target = (Player) e.getEntity();

            ClanPlayer cp = Main.getPlayerCache().get(p.getUniqueId().toString());
            ClanPlayer targetCp = Main.getPlayerCache().get(target.getUniqueId().toString());

            if(cp.getClan() == targetCp.getClan()) {
                e.setCancelled(true);
            }
        }
    }

}

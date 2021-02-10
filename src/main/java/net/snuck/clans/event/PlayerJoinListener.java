package net.snuck.clans.event;

import net.snuck.clans.Main;
import net.snuck.clans.database.manager.PlayerSQLManager;
import net.snuck.clans.type.Role;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void on(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        if(!(PlayerSQLManager.hasPlayer(uuid.toString()))) {
            PlayerSQLManager.insertPlayer(p.getUniqueId().toString(), "", Role.NO_CLAN);
        }

        if(!(Main.getPlayerCache().containsKey(uuid.toString()))) {
            Main.getPlayerCache().put(uuid.toString(), PlayerSQLManager.getPlayer(uuid.toString()));

            System.out.println(Main.getPlayerCache().get(uuid.toString()).toString());
        }
    }

}

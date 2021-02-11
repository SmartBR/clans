package net.snuck.clans.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.snuck.clans.database.manager.PlayerSQLManager;
import net.snuck.clans.object.ClanPlayer;
import org.bukkit.OfflinePlayer;

public class ClanExpansion extends PlaceholderExpansion {

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "clans";
    }

    @Override
    public String getAuthor() {
        return "snuck";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {

        ClanPlayer cp = PlayerSQLManager.getPlayer(player.getUniqueId().toString());

        if(identifier.equals("clanTag")) {
            if(cp != null) {
                if (cp.hasClan()) {
                    return cp.getClan().getTag();
                } else {
                    return "";
                }
            }

        }

        if(identifier.equals("clanName")) {
            if(cp != null) {
                if(cp.hasClan()) {
                    return cp.getClan().getName();
                } else {
                    return "§7No clan";
                }
            }
        }

        return null;
    }
}

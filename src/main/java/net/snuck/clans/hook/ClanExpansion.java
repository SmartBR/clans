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
        String noClan = "§7No clan";

        if (cp == null || !cp.hasClan()) return noClan;

        String result;
        switch (identifier) {
            case "clanTag":
                result = cp.getClan().getTag();
                break;
            case "clanName":
                result = cp.getClan().getName();
                break;
            default:
                result = noClan;
        }
        return result;
    }
}

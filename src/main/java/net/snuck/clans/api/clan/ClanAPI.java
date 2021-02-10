package net.snuck.clans.api.clan;

import net.snuck.clans.Main;
import net.snuck.clans.object.Clan;

public class ClanAPI {

    public static void saveAllClans() {
        Main.getClanCache().forEach((id, clan) -> {
            clan.save();
        });
    }

    public static Clan getClanById(String id) {
        return Main.getClanCache().get(id);
    }

    public static void insertClan(Clan clan) {
        Main.getClanCache().put(clan.getId(), clan);
    }

    public static void deleteClan(String clanId) {
        Main.getClanCache().remove(clanId);
    }

    public static boolean hasClan(String clanId) {
        return Main.getClanCache().containsKey(clanId);
    }
}

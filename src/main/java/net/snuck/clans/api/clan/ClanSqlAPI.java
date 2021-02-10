package net.snuck.clans.api.clan;

import net.snuck.clans.database.manager.ClanSQLManager;
import net.snuck.clans.object.Clan;

public class ClanSqlAPI {

    public static Clan getClanById(String id) {
        return ClanSQLManager.getClanById(id);
    }

    public static Clan getClanByName(String name) {
        return ClanSQLManager.getClanByName(name);
    }

    public static boolean hasClanWithId(String id) {
        return ClanSQLManager.hasClan(id);
    }

    public static boolean hasClanWithName(String name) {
        return ClanSQLManager.hasClanWithName(name);
    }

    public static boolean hasClanWithTag(String tag) {
        return ClanSQLManager.hasClanWithTag(tag);
    }

    public static void insertClan(String id, String tag, String name) {
        ClanSQLManager.insertClan(id, tag, name);
    }

    public static void removeClan(Clan clan) {
        clan.delete();
    }

}

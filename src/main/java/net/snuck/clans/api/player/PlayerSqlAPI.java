package net.snuck.clans.api.player;

import net.snuck.clans.database.manager.PlayerSQLManager;
import net.snuck.clans.object.ClanPlayer;
import net.snuck.clans.type.Role;

import java.util.List;

public class PlayerSqlAPI {

    public static boolean hasPlayer(String id) {
        return PlayerSQLManager.hasPlayer(id);
    }

    public static void removePlayer(String id) {
        PlayerSQLManager.deletePlayer(id);
    }

    public static void insertPlayer(String id, String clanId, Role role) {
        PlayerSQLManager.insertPlayer(id, clanId, role);
    }

    public static List<ClanPlayer> getAllPlayersFromClan(String clanId) {
        return PlayerSQLManager.getAllPlayers(clanId);
    }

}

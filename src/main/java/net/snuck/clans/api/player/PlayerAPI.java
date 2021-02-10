package net.snuck.clans.api.player;

import net.snuck.clans.Main;
import net.snuck.clans.object.ClanPlayer;

public class PlayerAPI {

    public static void saveAllPlayers() {
        Main.getPlayerCache().forEach((id, cp) -> {
            cp.save();
        });
    }

    public static ClanPlayer getPlayer(String playerId) {
        return Main.getPlayerCache().get(playerId);
    }

    public static void insertPlayer(ClanPlayer clanPlayer) {
        Main.getPlayerCache().put(clanPlayer.getId(), clanPlayer);
    }

    public static void deletePlayer(String playerId) {
        Main.getPlayerCache().remove(playerId);
    }

    public static boolean hasPlayer(String playerId) {
        return Main.getPlayerCache().containsKey(playerId);
    }

}

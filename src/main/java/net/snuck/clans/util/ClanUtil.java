package net.snuck.clans.util;

import net.snuck.clans.Main;
import net.snuck.clans.api.event.ClanDeleteEvent;
import net.snuck.clans.database.manager.CacheManager;
import net.snuck.clans.database.manager.PlayerSQLManager;
import net.snuck.clans.object.ClanPlayer;
import net.snuck.clans.type.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClanUtil {

    public static void deleteClan(Player p, ClanPlayer cp) {

        if(!cp.hasClan()) {
            p.sendMessage("§cOops! Looks like you don't have a clan.");
            return;
        }

        if(cp.getRole() != Role.LEADER) {
            p.sendMessage("§cOnly clan leader can execute this command.");
            return;
        }

        ClanDeleteEvent event = new ClanDeleteEvent(p, cp.getClan());
        Bukkit.getPluginManager().callEvent(event);
        Main.getClanCache().remove(cp.getClanId());
        cp.getClan().delete();

        Main.getPlayerCache().forEach((id, cacheMember) -> {
            cacheMember.setClan(null);
            cacheMember.setClanId("");
            cacheMember.setRole(Role.NO_CLAN);
            cacheMember.save();

            Player memberPlayer = Bukkit.getPlayer(UUID.fromString(cacheMember.getId()));

            if(memberPlayer != null) {
                memberPlayer.sendMessage("§cYour clan has been deleted.");
            }
        });

        for(ClanPlayer member : PlayerSQLManager.getAllPlayers(cp.getClanId())) {
            member.setClan(null);
            member.setClanId("");
            member.setRole(Role.NO_CLAN);
            member.save();
        }

        cp.setClan(null);
        cp.setClanId("");
        cp.setRole(Role.NO_CLAN);
        cp.save();

    }

    public static void exitClan(Player p, ClanPlayer cp) {

        String clanName = cp.getClan().getName();
        String clanTag = cp.getClan().getTag();

        if (!cp.hasClan()) {
            p.sendMessage("§cOops! Looks like you don't have a clan yet.");
            return;
        }

        if (cp.getRole() == Role.LEADER) {
            p.sendMessage("§cYou can't leave from your own clan.");
            return;
        }

        for (ClanPlayer member : CacheManager.getPlayersFromClan(cp.getClanId())) {
            Player memberPlayer = Bukkit.getPlayer(UUID.fromString(member.getId()));

            if (memberPlayer != null) {
                memberPlayer.sendMessage(String.format("§f%s §cleft from your clan.", p.getName()));
            }
        }

        cp.setClan(null);
        cp.setClanId("");
        cp.setRole(Role.NO_CLAN);
        cp.save();

        p.sendMessage(String.format("§cYou left from §f[%s] %s§c.", clanTag, clanName));

    }

}

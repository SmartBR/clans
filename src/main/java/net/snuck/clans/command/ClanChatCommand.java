package net.snuck.clans.command;

import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import net.snuck.clans.Main;
import net.snuck.clans.database.manager.CacheManager;
import net.snuck.clans.object.ClanPlayer;
import net.snuck.clans.type.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ClanChatCommand {

    @Command(name = "c",
            aliases = { "cc", "clanchat" },
            target = CommandTarget.PLAYER,
            permission = "clans.chat",
            usage = "c <message>",
            description = "Interacts with your clan via chat."
    )
    public void handleChatCommand(Context<Player> ctx, String[] message) {

        Player p = ctx.getSender();
        ClanPlayer cp = Main.getPlayerCache().get(p.getUniqueId().toString());

        if(!cp.hasClan()) {
            p.sendMessage("§cOops! Looks like you don't have a clan.");
            return;
        }

        if(cp.getRole() == Role.RECRUIT) {
            p.sendMessage("§cOnly members and up can type in the clan chat.");
            return;
        }

        String fullMessage = String.join(" ", message);

        for(ClanPlayer member : CacheManager.getPlayersFromClan(cp.getClanId())) {
            Player memberPlayer = Bukkit.getPlayer(UUID.fromString(member.getId()));

            if (memberPlayer != null) {
                memberPlayer.sendMessage(String.format("§f[§a%s§f] %s: %s", cp.getRole().getChatPrefix(), p.getName(), fullMessage));
            }
        }

    }

}

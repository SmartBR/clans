package net.snuck.clans.command;


import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import net.snuck.clans.Main;
import net.snuck.clans.database.manager.*;
import net.snuck.clans.gui.manager.ClanMenuManager;
import net.snuck.clans.object.*;
import net.snuck.clans.type.Role;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ClanCommand {

    @Command(name = "clan",
            target = CommandTarget.PLAYER,
            aliases = { "clans" },
            permission = "clans.openmenu",
            usage = "clan",
            description = "Open the clan menu"
    )
    public void handleCommand(Context<Player> ctx) {
        Player p = ctx.getSender();
        ClanPlayer clanPlayer = Main.getPlayerCache().get(p.getUniqueId().toString());



        ClanMenuManager.openMenu(p, clanPlayer.hasClan());
    }

    @Command(name = "clan.create",
            target = CommandTarget.PLAYER,
            permission = "clans.create",
            usage = "clan create <tag> <name>",
            description = "Creates a new clan"
    )
    public void handleCreateCommand(Context<Player> ctx, String clanTag, String clanName) {
        Player p = ctx.getSender();
        ClanPlayer cp = Main.getPlayerCache().get(p.getUniqueId().toString());

        if(!ClanSQLManager.hasClanWithName(clanName) && !ClanSQLManager.hasClanWithTag(clanTag) && !cp.hasClan()) {

            if(clanName.length() > 16) {
                p.sendMessage("§cThe clan name is too long, the characters limit is 16.");
                return;
            }

            if (clanTag.length() > 3) {
                p.sendMessage("§cThe clan tag is too long, the characters limit is 3.");
                return;
            }

            String clanId = UUID.randomUUID().toString();
            Clan clan = new Clan(clanId, clanTag, clanName);

            Main.getClanCache().put(clanId, clan);
            cp.setClanId(clanId);
            cp.setRole(Role.LEADER);
            cp.setClan(Main.getClanCache().get(clanId));

            // Saving the user just in case of some crashes.
            clan.save();
            cp.save();

            p.sendMessage(String.format("§aSuccessfully created §f[%s] %s§a!", cp.getClan().getTag(), cp.getClan().getName()));

        } else {
            p.sendMessage("§cOops! Looks like you already have a clan.");
        }
    }

    @Command(name = "clan.invite",
            target = CommandTarget.PLAYER,
            permission = "clans.invite",
            usage = "clan invite <player>",
            description = "Invites a user to your clan"
    )
    public void handleInviteCommand(Context<Player> ctx, Player target) {

        Player p = ctx.getSender();
        ClanPlayer senderCp = Main.getPlayerCache().get(p.getUniqueId().toString());

        if(!senderCp.hasClan()) {
            p.sendMessage("§cYou need to have a clan before execute this command. You can create a clan by typing §e/clan create <tag> <name>§c.");
            return;
        }

        if(!target.isOnline()) {
            p.sendMessage("§cThis player is currently not online, try again later.");
            return;
        }

        if(target == p) {
            p.sendMessage("§cOops! You can't invite yourself.");
            return;
        }

        ClanPlayer targetCp = Main.getPlayerCache().get(target.getUniqueId().toString());

        if(targetCp.getClanId().equals(senderCp.getClanId())) {
            p.sendMessage(String.format("§cOops! Looks like §f%s §cis already in your clan.", target.getName()));
            return;
        }

        if(targetCp.hasInviteForClan(senderCp.getClanId())) {
            p.sendMessage("§cThis player has already invited to join your clan, you can ask him to join.");
            return;
        }

        Invite invite = new Invite(targetCp, senderCp.getClan());
        invite.save();

        p.sendMessage(String.format("§f%s §awas successfully invited to your clan!", target.getName()));

        target.sendMessage(String.format("§e* You have been invited to join the §f[%s] %s §eclan.", senderCp.getClan().getTag(), senderCp.getClan().getName()));
        target.sendMessage(String.format("§fTo join it, you can type: §e/clan join %s", senderCp.getClan().getName()));
    }

    @Command(name = "clan.join",
            target = CommandTarget.PLAYER,
            permission = "clans.join",
            usage = "clan join <clan>",
            description = "Join into a clan that invited you."
    )
    public void handleJoinCommand(Context<Player> ctx, String clanName) {

        Player p = ctx.getSender();
        ClanPlayer cp = Main.getPlayerCache().get(p.getUniqueId().toString());

        if(cp.hasClan()) {
            p.sendMessage("§cYou're already in a clan.");
            return;
        }

        Clan invited = ClanSQLManager.getClanByName(clanName);

        if (invited != null) {
            String clanId = invited.getId();
            if(cp.hasInviteForClan(clanId)) {

                cp.setClan(invited);
                cp.setClanId(invited.getId());
                cp.setRole(Role.RECRUIT);
                cp.save();

                InviteSQLManager.removeInvite(p.getUniqueId().toString(), invited.getId());

                p.sendMessage(String.format("§aSuccessfully joined in the §f[%s] %s §aclan.", invited.getTag(), invited.getName()));

                for(ClanPlayer member : CacheManager.getPlayersFromClan(clanId)) {
                    Player memberPlayer = Bukkit.getPlayer(UUID.fromString(member.getId()));

                    if (memberPlayer != null) {
                        memberPlayer.sendMessage(String.format("§f%s §ajoined in your clan.", p.getName()));
                    }
                }

            } else {
                p.sendMessage("§cYou wasn't invited to this clan.");
            }
        } else {
            p.sendMessage("§cOops! Looks like this clan don't exists.");
        }

    }

    @Command(name = "clan.decline",
            target = CommandTarget.PLAYER,
            permission = "clans.decline",
            usage = "clan decline <clan>",
            description = "Declines an invite of a clan."
    )
    public void handleDeclineCommand(Context<Player> ctx, String clanName) {
        Player p = ctx.getSender();
        ClanPlayer cp = Main.getPlayerCache().get(p.getUniqueId().toString());

        Clan clan = ClanSQLManager.getClanByName(clanName);
        if (clan != null) {
            if(cp.hasInviteForClan(clan.getId())) {
                InviteSQLManager.removeInvite(p.getUniqueId().toString(), clan.getId());
                p.sendMessage(String.format("§cYou declined the invite from the clan §f[%s] %s§c.", clan.getTag(), clan.getName()));
            } else {
                p.sendMessage("§cYou don't have an invite for this clan.");
            }
        } else {
            p.sendMessage("§cOops! Looks like this clan don't exists.");
        }
    }

    @Command(name = "clan.delete",
            target = CommandTarget.PLAYER,
            permission = "clans.delete",
            usage = "clan delete",
            description = "Deletes your clan."
    )
    public void handleDeleteCommand(Context<Player> ctx) {
        Player p = ctx.getSender();
        ClanPlayer cp = Main.getPlayerCache().get(p.getUniqueId().toString());

        if(!cp.hasClan()) {
            p.sendMessage("§cOops! Looks like you don't have a clan.");
            return;
        }

        if(cp.getRole() != Role.LEADER) {
            p.sendMessage("§cOnly clan leader can execute this command.");
            return;
        }

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

    @Command(name = "clan.leave",
            target = CommandTarget.PLAYER,
            aliases = { "clan.exit" },
            permission = "clans.exit",
            usage = "clan exit",
            description = "Exit from your current clan."
    )
    public void handleExitCommand(Context<Player> ctx) {

        Player p = ctx.getSender();
        ClanPlayer cp = Main.getPlayerCache().get(p.getUniqueId().toString());

        String clanName = cp.getClan().getName();
        String clanTag = cp.getClan().getTag();

        if(!cp.hasClan()) {
            p.sendMessage("§cOops! Looks like you don't have a clan yet.");
            return;
        }

        if(cp.getRole() == Role.LEADER) {
            p.sendMessage("§cYou can't leave from your own clan.");
            return;
        }

        for(ClanPlayer member : CacheManager.getPlayersFromClan(cp.getClanId())) {
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

    @Command(name = "clan.help",
            target = CommandTarget.PLAYER,
            permission = "clans.help",
            usage = "clan help",
            description = "Show the help message."
    )
    public void handleHelpCommand(Context<Player> ctx) {
        Player p = ctx.getSender();

        p.sendMessage("§e/clan §f- shows the clan menu.");
        p.sendMessage("§e/clan create <tag> <name> §f- creates a clan.");
        p.sendMessage("§e/clan invite <player> §f- invites a player to your clan.");
        p.sendMessage("§e/clan join <clan> §f- joins into a clan that you've been invited for.");
        p.sendMessage("§e/clan decline <clan> §f- declines an invite of a clan.");
        p.sendMessage("§e/clan leave §f- leaves from your current clan.");
        p.sendMessage("§e/clan delete §f- deletes your clan, only for clan leaders.");
    }


}

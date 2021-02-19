package net.snuck.clans.command;


import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import net.snuck.clans.Main;
import net.snuck.clans.api.event.ClanCreateEvent;
import net.snuck.clans.api.event.ClanInviteCreateEvent;
import net.snuck.clans.api.event.ClanMemberAddEvent;
import net.snuck.clans.database.manager.*;
import net.snuck.clans.gui.manager.ClanMenuManager;
import net.snuck.clans.object.*;
import net.snuck.clans.type.Role;
import net.snuck.clans.util.ClanUtil;
import net.snuck.clans.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            if (clanName.length() > 16 || clanTag.length() != 3) {
                p.sendMessage(clanName.length() > 16 ? ConfigUtil.getString("messages.clan-name.too-long") : ConfigUtil.getString("messages-clan-name.invalid-tag"));
            }

            Pattern tagPattern = Pattern.compile("[^a-zA-Z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = tagPattern.matcher(clanTag);
            boolean tagFound = m.find();

            if(tagFound) {
                p.sendMessage(ConfigUtil.getString("messages.clan-name.special-characters-tag"));
                return;
            }

            Pattern namePattern = Pattern.compile("[^a-zA-Z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher nameMatcher = namePattern.matcher(clanName);
            boolean nameFound = nameMatcher.find();

            if(nameFound) {
                p.sendMessage(ConfigUtil.getString("messages.clan-name.special-characters"));
                return;
            }

            String clanId = UUID.randomUUID().toString();
            Clan clan = new Clan(clanId, clanTag.toUpperCase(), clanName);

            Main.getClanCache().put(clanId, clan);
            cp.setClanId(clanId);
            cp.setRole(Role.LEADER);
            cp.setClan(Main.getClanCache().get(clanId));

            // Saving the user just in case of some crashes.
            clan.save();
            cp.save();

            ClanCreateEvent event = new ClanCreateEvent(p, clan);
            Bukkit.getPluginManager().callEvent(event);
            p.sendMessage(ConfigUtil.getString("messages.actions.clan-created").replace("{tag}", cp.getClan().getTag()).replace("{name}", cp.getClan().getName()));

        } else {
            p.sendMessage(ConfigUtil.getString("errors.general.already-have-a-clan"));
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
            p.sendMessage(ConfigUtil.getString("messages.errors.invite.dont-have-clan"));
            return;
        }

        if(!target.isOnline()) {
            p.sendMessage(ConfigUtil.getString("messages.errors.invite.target-offline"));
            return;
        }

        if(target == p) {
            p.sendMessage(ConfigUtil.getString("messages.errors.invite.cannot-invite-yourself"));
            return;
        }

        ClanPlayer targetCp = Main.getPlayerCache().get(target.getUniqueId().toString());

        if(targetCp.hasClan()) {
            p.sendMessage(ConfigUtil.getString("messages.errors.invite.target-has-a-clan").replace("{target}", target.getName()));
            return;
        }

        if(targetCp.getClanId().equals(senderCp.getClanId())) {
            p.sendMessage(ConfigUtil.getString("messages.errors.invite.already-in-same-clan").replace("{target}", target.getName()));
            return;
        }

        if(targetCp.hasInviteForClan(senderCp.getClanId())) {
            p.sendMessage(ConfigUtil.getString("messages.errors.invite.target-already-invited"));
            return;
        }

        Invite invite = new Invite(targetCp, senderCp.getClan());
        invite.save();

        ClanInviteCreateEvent event = new ClanInviteCreateEvent(p, senderCp.getClan(), invite);
        Bukkit.getPluginManager().callEvent(event);
        p.sendMessage(ConfigUtil.getString("messages.invite.target-invited").replace("{target}", target.getName()));

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
            p.sendMessage(ConfigUtil.getString("messages.errors.general.already-have-a-clan"));
            return;
        }

        Clan invited = ClanSQLManager.getClanByName(clanName);
        if (invited == null) {
            p.sendMessage(ConfigUtil.getString("messages.errors.general.clan-dont-exists"));
            return;
        }

        String clanId = invited.getId();
        if (!cp.hasInviteForClan(clanId)) {
            p.sendMessage(ConfigUtil.getString("messages.errors.invite.was-not-invited"));
            return;
        }

        cp.setClan(invited);
        cp.setClanId(invited.getId());
        cp.setRole(Role.RECRUIT);
        cp.save();

        InviteSQLManager.removeInvite(p.getUniqueId().toString(), invited.getId());

        ClanMemberAddEvent event = new ClanMemberAddEvent(p, cp, invited);
        Bukkit.getPluginManager().callEvent(event);
        p.sendMessage(ConfigUtil.getString("messages.errors.invite.sender-joined").replace("{tag}", invited.getTag()).replace("{name}", invited.getName()));

        CacheManager.getPlayersFromClan(clanId).forEach(member -> {
            Player memberPlayer = Bukkit.getPlayer(UUID.fromString(member.getId()));

            if (memberPlayer != null) {
                memberPlayer.sendMessage(ConfigUtil.getString("messages.errors.invite.members-someone-joined").replace("{member}", p.getName()));
            }
        });
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
        if (clan == null) {
            p.sendMessage(ConfigUtil.getString("messages.errors.general.clan-dont-exists"));
            return;
        }

        if (!cp.hasInviteForClan(clan.getId())) {
            p.sendMessage(ConfigUtil.getString("messages.errors.general.was-not-invited"));
            return;
        }

        InviteSQLManager.removeInvite(p.getUniqueId().toString(), clan.getId());
        p.sendMessage(String.format("§cYou declined the invite from the clan §f[%s] %s§c.", clan.getTag(), clan.getName()));
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

        ClanUtil.deleteClan(p, cp);
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
        ClanUtil.exitClan(p, Main.getPlayerCache().get(p.getUniqueId().toString()));
    }

    @Command(name = "clan.help",
            target = CommandTarget.PLAYER,
            permission = "clans.help",
            usage = "clan help",
            description = "Show the help message."
    )
    public void handleHelpCommand(Context<Player> ctx) {
        Player p = ctx.getSender();

        p.sendMessage(new String[] {
                "§e/clan §f- shows the clan menu.",
                "§e/clan create <tag> <name> §f- creates a clan.",
                "§e/clan invite <player> §f- invites a player to your clan.",
                "§e/clan join <clan> §f- joins into a clan that you've been invited for.",
                "§e/clan decline <clan> §f- declines an invite of a clan.",
                "§e/clan leave §f- leaves from your current clan.",
                "§e/clan delete §f- deletes your clan, only for clan leaders."
        });
    }
}

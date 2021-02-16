package net.snuck.clans.util;

import net.snuck.clans.Main;
import net.snuck.clans.object.ClanPlayer;
import net.snuck.clans.type.Role;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import java.util.UUID;

public class PlayerUtil {

    public static void demote(Player player, ClanPlayer member) {
        ClanPlayer cp = Main.getPlayerCache().get(player.getUniqueId().toString());
        OfflinePlayer memberPlayer = Bukkit.getOfflinePlayer(UUID.fromString(member.getId()));

        if(cp.getRole() == Role.LEADER) {
            if(cp.getId().equals(member.getId())) {
                player.sendMessage("§cYou can't demote yourself.");
                return;
            }

            player.closeInventory();

            switch (member.getRole()) {
                case RECRUIT: {
                    player.sendMessage("§cThis player is a recruit.");
                    break;
                }

                case MEMBER: {
                    if(memberPlayer.isOnline()) {
                        ClanPlayer promoteTarget = Main.getPlayerCache().get(memberPlayer.getUniqueId().toString());
                        promoteTarget.setRole(Role.RECRUIT);
                        promoteTarget.save();
                    } else {
                        member.setRole(Role.RECRUIT);
                        member.save();
                    }
                    player.sendMessage(String.format("§aSuccessfully demoted §f%s §ato §f%s§a.", memberPlayer.getName(), member.getRole().getName()));
                    break;
                }

                case CAPTAIN: {
                    if(memberPlayer.isOnline()) {
                        ClanPlayer promoteTarget = Main.getPlayerCache().get(memberPlayer.getUniqueId().toString());
                        promoteTarget.setRole(Role.MEMBER);
                        promoteTarget.save();
                    } else {
                        member.setRole(Role.MEMBER);
                        member.save();
                    }
                    player.sendMessage(String.format("§aSuccessfully demoted §f%s §ato §f%s§a.", memberPlayer.getName(), member.getRole().getName()));
                    break;
                }

                case LEADER: {
                    player.sendMessage("§cYou can't demote a leader.");
                    break;
                }
            }
        } else if(cp.getRole() == Role.CAPTAIN) {
            if(member.getRole().getPermissionIndex() < cp.getRole().getPermissionIndex()) {
                switch (member.getRole()) {
                    case RECRUIT: {
                        player.sendMessage("§cThis player is a recruit.");
                        break;
                    }
                    case MEMBER: {
                        if(memberPlayer.isOnline()) {
                            ClanPlayer promoteTarget = Main.getPlayerCache().get(memberPlayer.getUniqueId().toString());
                            promoteTarget.setRole(Role.RECRUIT);
                            promoteTarget.save();
                        } else {
                            member.setRole(Role.RECRUIT);
                            member.save();
                        }
                        player.sendMessage(String.format("§aSuccessfully demoted §f%s §ato §f%s§a.", memberPlayer.getName(), member.getRole().getName()));
                        break;
                    }
                    default: {
                        player.sendMessage("§cYou can't demote this player.");
                        break;
                    }
                }

                return;
            }
            player.sendMessage("§cYou can't demote this player.");
        }
    }

    public static void promote(Player player, ClanPlayer member) {
        ClanPlayer cp = Main.getPlayerCache().get(player.getUniqueId().toString());
        OfflinePlayer memberPlayer = Bukkit.getOfflinePlayer(UUID.fromString(member.getId()));

        if(cp.getRole() == Role.LEADER || cp.getRole() == Role.CAPTAIN) {
            if(cp.getRole().getPermissionIndex() < member.getRole().getPermissionIndex()) {
                player.closeInventory();
                player.sendMessage("§cThat player has a superior role.");
                return;
            }

            if(cp.getRole().getPermissionIndex() == member.getRole().getPermissionIndex() + 1) {
                player.closeInventory();
                player.sendMessage("§cYou can't promote this player.");
                return;
            }

            if(cp.getId().equals(member.getId())) {
                player.sendMessage("§cYou can't promote yourself.");
                return;
            }

            player.closeInventory();

            switch (member.getRole()) {
                case RECRUIT: {
                    if(memberPlayer.isOnline()) {
                        ClanPlayer promoteTarget = Main.getPlayerCache().get(memberPlayer.getUniqueId().toString());
                        promoteTarget.setRole(Role.MEMBER);
                        promoteTarget.save();
                        player.sendMessage(String.format("§aSuccessfully promoted §f%s §ato §f%s§a.", memberPlayer.getName(), promoteTarget.getRole().getName()));
                    } else {
                        member.setRole(Role.MEMBER);
                        member.save();
                        player.sendMessage(String.format("§aSuccessfully promoted §f%s §ato §f%s§a.", memberPlayer.getName(), member.getRole().getName()));
                    }
                    break;
                }
                case MEMBER: {
                    if(memberPlayer.isOnline()) {
                        ClanPlayer promoteTarget = Main.getPlayerCache().get(memberPlayer.getUniqueId().toString());
                        promoteTarget.setRole(Role.CAPTAIN);
                        promoteTarget.save();
                        player.sendMessage(String.format("§aSuccessfully promoted §f%s §ato §f%s§a.", memberPlayer.getName(), promoteTarget.getRole().getName()));
                    } else {
                        member.setRole(Role.CAPTAIN);
                        member.save();
                        player.sendMessage(String.format("§aSuccessfully promoted §f%s §ato §f%s§a.", memberPlayer.getName(), member.getRole().getName()));
                    }
                    break;
                }
                case CAPTAIN: {
                    if(memberPlayer.isOnline()) {
                        ClanPlayer promoteTarget = Main.getPlayerCache().get(memberPlayer.getUniqueId().toString());
                        promoteTarget.setRole(Role.LEADER);
                        promoteTarget.save();
                        player.sendMessage(String.format("§aSuccessfully promoted §f%s §ato §f%s§a.", memberPlayer.getName(), promoteTarget.getRole().getName()));
                    } else {
                        member.setRole(Role.LEADER);
                        member.save();
                        player.sendMessage(String.format("§aSuccessfully promoted §f%s §ato §f%s§a.", memberPlayer.getName(), member.getRole().getName()));
                    }
                    break;
                }
                case LEADER: {
                    player.sendMessage("§cThis player is in the highest role.");
                }
                default: {
                    break;
                }
            }
        }

    }


}

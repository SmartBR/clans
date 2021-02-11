package net.snuck.clans.gui.manager;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import net.snuck.clans.Main;
import net.snuck.clans.composer.ItemBuilder;
import net.snuck.clans.database.manager.CacheManager;
import net.snuck.clans.database.manager.PlayerSQLManager;
import net.snuck.clans.object.ClanPlayer;
import net.snuck.clans.type.Role;
import net.snuck.clans.util.ChatAsker;
import net.snuck.clans.util.ClanUtil;
import net.snuck.clans.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class ClanMenuManager {

    private static final ChatAsker DISBAND_ASKER = ChatAsker.builder()
            .messages("", "§cAre you sure you want to disband your clan?", "  §7Type §cyes §7to confirm and §ccancel §7to cancel.")
            .onComplete((p, message) -> {

                if(message.equalsIgnoreCase("cancel")) {
                    p.sendMessage("§aClan disband cancelled.");
                    return;
                }
                if(message.equalsIgnoreCase("yes")) {
                    ClanUtil.deleteClan(p, Main.getPlayerCache().get(p.getUniqueId().toString()));
                }

            }).build();

    private static final ChatAsker LEAVE_ASKER = ChatAsker.builder()
            .messages("", "§cAre you sure you want to leave from your clan?", "  §7Type §cyes §7to confirm and §ccancel §7to cancel.")
            .onComplete((p, message) -> {

                if(message.equalsIgnoreCase("cancel")) {
                    p.sendMessage("§aClan disband cancelled.");
                    return;
                }
                if(message.equalsIgnoreCase("yes")) {
                    ClanUtil.exitClan(p, Main.getPlayerCache().get(p.getUniqueId().toString()));
                }
            }).build();

    // TODO add elevated operations like kicking players here.
    public static void openElevatedMembersMenu(Player player) {

        ClanPlayer cp = Main.getPlayerCache().get(player.getUniqueId().toString());

        ChestGui gui = new ChestGui(6, String.format("[%s] %s - MEMBERS", cp.getClan().getTag(), cp.getClan().getName()));
        gui.setOnGlobalClick((e) -> e.setCancelled(true));

        OutlinePane membersPane = new OutlinePane(1, 1, 4, 7, Pane.Priority.LOWEST);

        for(ClanPlayer member : PlayerSQLManager.getAllPlayers(cp.getClanId())) {

            String playerId = member.getId();

            OfflinePlayer memberPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerId));

            ItemStack playerHead = new ItemBuilder(Material.PLAYER_HEAD)
                    .setName("§7" + memberPlayer.getName())
                    .setLore("§7Role: §f" + member.getRole().getName(), "", "§7Left-click: §fpromote player", "§7Right-click: §fdemote player")
                    .build();
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            meta.setOwningPlayer(memberPlayer);
            playerHead.setItemMeta(meta);

            GuiItem headItem = new GuiItem(playerHead, (e) -> {

                if(e.getClick() == ClickType.LEFT) {
                    // PlayerUtil class handle all conditional structures.
                    PlayerUtil.promote(player, member);
                } else if(e.getClick() == ClickType.RIGHT) {
                    PlayerUtil.demote(player, member);
                }

            });

            membersPane.addItem(headItem);
        }

        gui.addPane(membersPane);

        OutlinePane backPane = new OutlinePane(0, 5, 1, 1);

        ItemStack backArrow = new ItemBuilder(Material.ARROW)
                .setName("§aBack")
                .build();

        backPane.addItem(new GuiItem(backArrow, (e) -> {
            openMenu(player, cp.hasClan());
        }));

        gui.addPane(backPane);

        gui.show(player);
    }

    public static void openMembersMenu(Player player) {
        ClanPlayer cp = Main.getPlayerCache().get(player.getUniqueId().toString());

        ChestGui gui = new ChestGui(6, String.format("[%s] %s - MEMBERS", cp.getClan().getTag(), cp.getClan().getName()));
        gui.setOnGlobalClick((e) -> e.setCancelled(true));

        OutlinePane membersPane = new OutlinePane(1, 1, 4, 7, Pane.Priority.LOWEST);

        for(ClanPlayer clanPlayer : PlayerSQLManager.getAllPlayers(cp.getClanId())) {

            String playerId = clanPlayer.getId();

            OfflinePlayer clanPlayerBukkit = Bukkit.getOfflinePlayer(UUID.fromString(playerId));

            ItemStack playerHead = new ItemBuilder(Material.PLAYER_HEAD)
                    .setName("§7" + clanPlayerBukkit.getName())
                    .setLore("§7Role: §f" + clanPlayer.getRole().getName())
                    .build();
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            meta.setOwningPlayer(clanPlayerBukkit);
            playerHead.setItemMeta(meta);

            membersPane.addItem(new GuiItem(playerHead));
        }

        gui.addPane(membersPane);

        OutlinePane backPane = new OutlinePane(0, 5, 1, 1);

        ItemStack backArrow = new ItemBuilder(Material.ARROW)
                .setName("§aBack")
                .build();

        backPane.addItem(new GuiItem(backArrow, (e) -> {
            openMenu(player, cp.hasClan());
        }));

        gui.addPane(backPane);

        gui.show(player);
    }

    public static void openMenu(Player player, boolean hasClan) {
        if(hasClan) {

            ClanPlayer cp = Main.getPlayerCache().get(player.getUniqueId().toString());

            ChestGui gui = new ChestGui(5, String.format("[%s] %s", cp.getClan().getTag(), cp.getClan().getName()));
            gui.setOnGlobalClick((e) -> e.setCancelled(true));

            ItemStack clanInfo = new ItemBuilder(Material.WRITABLE_BOOK)
                    .setName("§aClan information")
                    .setLore("§7Tag: §f" + cp.getClan().getTag(),
                            "§7Name: §f" + cp.getClan().getName(),
                            "§7Members: §f" + CacheManager.getPlayersFromClan(cp.getClanId()).size())
                    .build();

            ItemStack elevatedMembersInfo = new ItemBuilder(Material.PLAYER_HEAD)
                    .setName("§aMembers")
                    .setLore("§7Manage the members in your clan here.")
                    .build();
            SkullMeta meta = (SkullMeta) elevatedMembersInfo.getItemMeta();
            meta.setOwningPlayer(player);
            elevatedMembersInfo.setItemMeta(meta);

            ItemStack membersInfo = new ItemBuilder(Material.PLAYER_HEAD)
                    .setName("§aMembers")
                    .setLore("§7See the members in your clan here.")
                    .build();
            SkullMeta basicMeta = (SkullMeta) membersInfo.getItemMeta();
            basicMeta.setOwningPlayer(player);
            membersInfo.setItemMeta(basicMeta);

            OutlinePane menu = new OutlinePane(1, 1, 2, 1);

            GuiItem elevatedMembers = new GuiItem(elevatedMembersInfo, (e) -> {
                openElevatedMembersMenu(player);
            });
            GuiItem members = new GuiItem(membersInfo, (e) -> {
                openMembersMenu(player);
            });

            menu.addItem(new GuiItem(clanInfo));
            menu.addItem(cp.getRole() == Role.LEADER || cp.getRole() == Role.CAPTAIN ? elevatedMembers : members);

            gui.addPane(menu);

            OutlinePane exitOrDisbandPane = new OutlinePane(1, 3, 1, 1);

            ItemStack disbandStack = new ItemBuilder(Material.DARK_OAK_DOOR)
                    .setName("§cDisband clan")
                    .setLore("§7Click here to disband your clan.")
                    .build();

            ItemStack exitStack = new ItemBuilder(Material.DARK_OAK_DOOR)
                    .setName("§cExit clan")
                    .setLore("§7Click here to exit from your clan.")
                    .build();

            GuiItem disbandItem = new GuiItem(disbandStack, (e) -> {
                player.closeInventory();
                DISBAND_ASKER.addPlayer(player);
            });

            GuiItem exitItem = new GuiItem(exitStack, (e) -> {
                player.closeInventory();
                LEAVE_ASKER.addPlayer(player);
            });

            exitOrDisbandPane.addItem(cp.getRole().getPermissionIndex() == 3 ? disbandItem : exitItem);

            gui.addPane(exitOrDisbandPane);

            OutlinePane rankingPane = new OutlinePane(7, 1, 1, 1);

            ItemStack rankingItem = new ItemBuilder(Material.GOLD_NUGGET)
                    .setName("§6Clans ranking")
                    .setLore("§7See the server ranking here.")
                    .build();

            rankingPane.addItem(new GuiItem(rankingItem));

            gui.addPane(rankingPane);

            gui.show(player);

        } else {
            ChestGui noClanGui = new ChestGui(3, player.getName());
            noClanGui.setOnGlobalClick((e) -> e.setCancelled(true));

            ItemStack createClan = new ItemBuilder(Material.PAPER)
                    .setName("§aCreate a clan")
                    .setLore("§7Click here to create a clan.")
                    .build();

            OutlinePane menu = new OutlinePane(2, 1, 1, 1);

            menu.addItem(new GuiItem(createClan, (e) -> {
                e.getWhoClicked().closeInventory();
                player.sendMessage("§cYou can create a clan by typing §e/clan create <tag> <name>§c.");
            }));
            noClanGui.addPane(menu);

            noClanGui.show(player);
        }
    }

}

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
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;
import java.util.UUID;

public class ClanMenuManager {

    // TODO add elevated operations like kicking players here.
    public static void openElevatedMembersMenu(Player player) {

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

            OutlinePane bg = new OutlinePane(0, 0, 9, 5, Pane.Priority.LOWEST);
            bg.addItem(new GuiItem(new ItemBuilder(Material.GLASS_PANE).build()));
            bg.setRepeat(true);

            gui.addPane(bg);

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

            OutlinePane rolesPane = new OutlinePane(1, 2, 1, 1);

            ItemStack leatherRole = new ItemBuilder(Material.LEATHER_CHESTPLATE)
                    .setName("§aRole permissions")
                    .setLore("§7Manage all permissions for specific roles.")
                    .addItemFlag(ItemFlag.HIDE_DYE, ItemFlag.HIDE_ATTRIBUTES)
                    .build();
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) leatherRole.getItemMeta();
            leatherArmorMeta.setColor(Color.GREEN);
            leatherRole.setItemMeta(leatherArmorMeta);

            rolesPane.addItem(new GuiItem(leatherRole));

            gui.addPane(rolesPane);

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

            OutlinePane bg = new OutlinePane(0, 0, 9, 3, Pane.Priority.LOWEST);
            bg.addItem(new GuiItem(new ItemBuilder(Material.GLASS_PANE).build()));
            bg.setRepeat(true);

            noClanGui.addPane(bg);


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

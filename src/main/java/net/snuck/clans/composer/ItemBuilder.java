package net.snuck.clans.composer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {

    private ItemStack item;

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static ItemBuilder of(ItemStack item) {
        return new ItemBuilder(item);
    }

    public ItemBuilder compose(Consumer<ItemStack> consumer) {
        consumer.accept(item);
        return this;
    }

    public ItemBuilder composeMeta(Consumer<ItemMeta> consumer) {
        ItemMeta meta = item.getItemMeta();
        consumer.accept(meta);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setName(String name) {
        if(name == null || name.equalsIgnoreCase("nulo")) return this;
        composeMeta(meta -> meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name)));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (lore == null || lore.isEmpty() || lore.get(0).equalsIgnoreCase("nulo")) return this;
        composeMeta(meta -> meta.setLore(lore.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList())));
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder addLore(String... lore){
        return addLore(Arrays.asList(lore));
    }

    public ItemBuilder addLore(List<String> lore){
        composeMeta(meta -> {
            List<String> newLore = meta.getLore();
            newLore.addAll((lore));
            meta.setLore(newLore);
        });
        return this;
    }

    public ItemBuilder addGlow(boolean glow){
        compose(it -> it.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE,1));
        composeMeta(meta -> meta.addItemFlags(ItemFlag.HIDE_ENCHANTS));
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        compose(it -> it.setAmount(amount));
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        compose(it -> it.addUnsafeEnchantment(enchantment, level));
        return this;
    }

    public ItemBuilder addEnchantments(HashMap<Enchantment, Integer> enchantments) {
        compose(it -> it.addUnsafeEnchantments(enchantments));
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag... itemflag) {
        composeMeta(meta -> meta.addItemFlags(itemflag));
        return this;
    }

    public ItemBuilder setClickListener(JavaPlugin plugin, Consumer<PlayerInteractEvent> consumer) {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInteract(PlayerInteractEvent e) {
                if (e.hasItem() && e.getItem().isSimilar(item)) {
                    consumer.accept(e);
                }
            }
        }, plugin);
        return this;
    }

    public ItemStack build() {
        return this.item;
    }

}

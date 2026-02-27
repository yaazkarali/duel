package com.inferno.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack base) {
        this.item = base.clone();
        this.meta = item.getItemMeta();
    }

    public ItemBuilder name(String name) {
        meta.displayName(ColorUtil.color(name));
        return this;
    }

    public ItemBuilder name(Component name) {
        meta.displayName(name);
        return this;
    }

    public ItemBuilder lore(String... lines) {
        meta.lore(Arrays.stream(lines)
                .map(ColorUtil::color)
                .collect(Collectors.toList()));
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        meta.lore(lines.stream()
                .map(ColorUtil::color)
                .collect(Collectors.toList()));
        return this;
    }

    public ItemBuilder glow() {
        meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}

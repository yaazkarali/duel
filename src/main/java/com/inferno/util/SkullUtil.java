package com.inferno.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public final class SkullUtil {

    private SkullUtil() {}

    public static ItemStack getSkull(String playerName) {
        ItemStack skull = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
        meta.displayName(ColorUtil.color("&e" + playerName));
        skull.setItemMeta(meta);
        return skull;
    }

    public static ItemStack getSkull(UUID uuid) {
        ItemStack skull = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        skull.setItemMeta(meta);
        return skull;
    }

    public static ItemStack getSkull(UUID uuid, Component displayName) {
        ItemStack skull = new ItemStack(org.bukkit.Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        meta.displayName(displayName);
        skull.setItemMeta(meta);
        return skull;
    }
}

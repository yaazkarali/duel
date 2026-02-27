package com.inferno.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class KitApplier {

    /**
     * Apply a kit to a player: clear inventory, set armor, give weapon and items.
     */
    public static void apply(Player player, DuelKit kit) {
        player.getInventory().clear();

        PlayerInventory inv = player.getInventory();

        // Set armor
        if (kit.getHelmet() != null) inv.setHelmet(kit.getHelmet().clone());
        if (kit.getChestplate() != null) inv.setChestplate(kit.getChestplate().clone());
        if (kit.getLeggings() != null) inv.setLeggings(kit.getLeggings().clone());
        if (kit.getBoots() != null) inv.setBoots(kit.getBoots().clone());

        // Set weapon in main hand (slot 0)
        if (kit.getWeapon() != null) inv.setItem(0, kit.getWeapon().clone());

        // Add other items
        int slot = 1;
        List<ItemStack> items = kit.getItems();
        for (ItemStack item : items) {
            if (slot < 9) {
                inv.setItem(slot, item.clone());
                slot++;
            } else {
                // overflow to hotbar/main inventory
                inv.addItem(item.clone());
            }
        }

        player.updateInventory();
    }
}

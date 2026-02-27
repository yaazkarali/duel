package com.inferno.kit;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DuelKit {

    private final String id;
    private final String displayName;
    private final String description;
    private final Material icon;
    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;
    private final ItemStack weapon;
    private final List<ItemStack> items;

    public DuelKit(String id, String displayName, String description, Material icon,
                   ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots,
                   ItemStack weapon, List<ItemStack> items) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
        this.weapon = weapon;
        this.items = items;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public Material getIcon() { return icon; }
    public ItemStack getHelmet() { return helmet; }
    public ItemStack getChestplate() { return chestplate; }
    public ItemStack getLeggings() { return leggings; }
    public ItemStack getBoots() { return boots; }
    public ItemStack getWeapon() { return weapon; }
    public List<ItemStack> getItems() { return items; }

    public ItemStack[] getArmor() {
        return new ItemStack[]{ boots, leggings, chestplate, helmet };
    }
}

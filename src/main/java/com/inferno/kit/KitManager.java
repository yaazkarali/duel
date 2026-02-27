package com.inferno.kit;

import com.inferno.Inferno;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitManager {

    private final Inferno plugin;
    private final Map<String, DuelKit> kits = new LinkedHashMap<>();

    public KitManager(Inferno plugin) {
        this.plugin = plugin;
    }

    public void load() {
        kits.clear();
        FileConfiguration cfg = plugin.getConfigManager().getKits();
        ConfigurationSection section = cfg.getConfigurationSection("kits");
        if (section == null) return;

        for (String id : section.getKeys(false)) {
            String name = section.getString(id + ".display-name", id);
            String desc = section.getString(id + ".description", "");
            Material icon = parseMaterial(section.getString(id + ".icon"), Material.IRON_SWORD);

            ItemStack helmet = parseArmor(section, id + ".armor.helmet");
            ItemStack chestplate = parseArmor(section, id + ".armor.chestplate");
            ItemStack leggings = parseArmor(section, id + ".armor.leggings");
            ItemStack boots = parseArmor(section, id + ".armor.boots");
            ItemStack weapon = parseArmor(section, id + ".weapon");

            List<ItemStack> items = new ArrayList<>();
            List<Map<?, ?>> itemList = section.getMapList(id + ".items");
            for (Map<?, ?> itemMap : itemList) {
                String mat = (String) itemMap.get("material");
                int amount = itemMap.containsKey("amount") ? (int) itemMap.get("amount") : 1;
                Material material = parseMaterial(mat, null);
                if (material != null) {
                    items.add(new ItemStack(material, amount));
                }
            }

            kits.put(id, new DuelKit(id, name, desc, icon, helmet, chestplate, leggings, boots, weapon, items));
        }
        plugin.getLogger().info("Loaded " + kits.size() + " kit(s).");
    }

    private Material parseMaterial(String name, Material fallback) {
        if (name == null) return fallback;
        try {
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    private ItemStack parseArmor(ConfigurationSection section, String path) {
        String name = section.getString(path);
        Material mat = parseMaterial(name, null);
        return mat != null ? new ItemStack(mat) : null;
    }

    public DuelKit getKit(String id) {
        return kits.get(id);
    }

    public Collection<DuelKit> getKits() {
        return kits.values();
    }

    public List<DuelKit> getKitList() {
        return new ArrayList<>(kits.values());
    }
}

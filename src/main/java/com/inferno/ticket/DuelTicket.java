package com.inferno.ticket;

import com.inferno.Inferno;
import com.inferno.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class DuelTicket {

    public static final String PDC_KEY = "inferno_ticket";

    private final Inferno plugin;
    private final NamespacedKey key;

    public DuelTicket(Inferno plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin.getDescription().getName().toLowerCase(), PDC_KEY);
    }

    public NamespacedKey getKey() {
        return key;
    }

    /**
     * Create a ticket ItemStack of the given amount.
     */
    public ItemStack createTicket(int amount) {
        String materialName = plugin.getConfigManager().getConfig()
                .getString("ticket.material", "PAPER");
        Material material;
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            material = Material.PAPER;
        }

        String name = plugin.getConfigManager().getConfig()
                .getString("ticket.name", "&#FFD700&lDuel Ticket");
        List<String> lore = plugin.getConfigManager().getConfig()
                .getStringList("ticket.lore");
        if (lore.isEmpty()) {
            lore = List.of(
                    "&7Used to wager in Inferno Duels.",
                    "&7Purchase from the Duel Vendor.",
                    "",
                    "&8Worth: &e1 Coin"
            );
        }

        ItemStack item = new ItemBuilder(material, amount)
                .name(name)
                .lore(lore)
                .glow()
                .build();

        // Add PDC tag
        item.editMeta(meta ->
                meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1));

        return item;
    }

    /**
     * Check if an ItemStack is a Duel Ticket.
     */
    public boolean isTicket(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(key, PersistentDataType.BYTE);
    }
}

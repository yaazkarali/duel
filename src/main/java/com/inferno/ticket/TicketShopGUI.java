package com.inferno.ticket;

import com.inferno.Inferno;
import com.inferno.util.ColorUtil;
import com.inferno.util.ItemBuilder;
import com.inferno.util.TextUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class TicketShopGUI implements Listener {

    private static final String GUI_TITLE = "§6Duel Ticket Shop";

    private final Inferno plugin;

    public TicketShopGUI(Inferno plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ColorUtil.color("&6Duel Ticket Shop"));
        fillBorder(inv);

        Economy economy = plugin.getEconomy();
        double balance = economy != null ? economy.getBalance(player) : 0;
        String balStr = economy != null ? String.format("%.0f", balance) : "N/A";

        // Slot 10: Buy 10
        inv.setItem(10, new ItemBuilder(Material.GOLD_NUGGET, 10)
                .name("&eBuy 10 Tickets")
                .lore("&7Cost: &e10 coins", "&7Your Balance: &e" + balStr, "", "&aClick to purchase!")
                .build());
        // Slot 11: Buy 50
        inv.setItem(11, new ItemBuilder(Material.GOLD_NUGGET, 50)
                .name("&eBuy 50 Tickets")
                .lore("&7Cost: &e50 coins", "&7Your Balance: &e" + balStr, "", "&aClick to purchase!")
                .build());
        // Slot 12: Buy 100
        inv.setItem(12, new ItemBuilder(Material.GOLD_INGOT, 1)
                .name("&eBuy 100 Tickets")
                .lore("&7Cost: &e100 coins", "&7Your Balance: &e" + balStr, "", "&aClick to purchase!")
                .build());
        // Slot 13: Buy 500
        inv.setItem(13, new ItemBuilder(Material.GOLD_INGOT, 5)
                .name("&eBuy 500 Tickets")
                .lore("&7Cost: &e500 coins", "&7Your Balance: &e" + balStr, "", "&aClick to purchase!")
                .build());
        // Slot 14: Buy 1000
        inv.setItem(14, new ItemBuilder(Material.GOLD_BLOCK, 1)
                .name("&eBuy 1000 Tickets")
                .lore("&7Cost: &e1000 coins", "&7Your Balance: &e" + balStr, "", "&aClick to purchase!")
                .build());
        // Slot 15: Custom amount
        inv.setItem(15, new ItemBuilder(Material.ANVIL)
                .name("&eCustom Amount")
                .lore("&7Click to type a custom amount in chat.")
                .build());
        // Slot 16: Sell tickets
        inv.setItem(16, new ItemBuilder(Material.REDSTONE)
                .name("&cSell All Tickets")
                .lore("&71 ticket = 1 coin back", "&7Your tickets: &e" + plugin.getTicketManager().countTickets(player))
                .build());
        // Slot 22: Close
        inv.setItem(22, new ItemBuilder(Material.BARRIER)
                .name("&cClose")
                .build());

        player.openInventory(inv);
    }

    private void fillBorder(Inventory inv) {
        ItemBuilder filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("&r");
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler.build());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().title() == null) return;
        String title = ColorUtil.colorString(GUI_TITLE);
        if (!event.getView().title().equals(ColorUtil.color("&6Duel Ticket Shop"))) return;

        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;

        int slot = event.getSlot();
        handlePurchase(player, slot);
    }

    private void handlePurchase(Player player, int slot) {
        Economy economy = plugin.getEconomy();
        if (economy == null) {
            TextUtil.send(player, "&cVault economy is not available.");
            return;
        }

        int amount = switch (slot) {
            case 10 -> 10;
            case 11 -> 50;
            case 12 -> 100;
            case 13 -> 500;
            case 14 -> 1000;
            case 16 -> -1; // sell
            case 22 -> { player.closeInventory(); yield 0; }
            default -> 0;
        };

        if (amount == 0) return;

        if (amount == -1) {
            // Sell all tickets
            int ticketCount = plugin.getTicketManager().countTickets(player);
            if (ticketCount == 0) {
                TextUtil.send(player, plugin.getConfigManager().getMessage("tickets-sell-none"));
                return;
            }
            if (plugin.getTicketManager().removeTickets(player, ticketCount)) {
                economy.depositPlayer(player, ticketCount);
                String msg = TextUtil.replace(
                        plugin.getConfigManager().getMessage("tickets-sell-success"),
                        "amount", String.valueOf(ticketCount));
                TextUtil.send(player, msg);
                player.closeInventory();
            }
            return;
        }

        if (slot == 15) {
            // Custom amount — close and ask in chat
            player.closeInventory();
            TextUtil.send(player, "&eType the amount of tickets you wish to buy in chat.");
            plugin.getGUIManager().setPendingCustomAmount(player);
            return;
        }

        // Buy tickets
        double cost = amount;
        if (!economy.has(player, cost)) {
            String msg = TextUtil.replace(
                    plugin.getConfigManager().getMessage("tickets-purchase-fail-balance"),
                    "cost", String.valueOf((int) cost));
            TextUtil.send(player, msg);
            return;
        }

        economy.withdrawPlayer(player, cost);
        plugin.getTicketManager().giveTickets(player, amount);
        String msg = TextUtil.replace(TextUtil.replace(
                plugin.getConfigManager().getMessage("tickets-purchase-success"),
                "amount", String.valueOf(amount)),
                "cost", String.valueOf((int) cost));
        TextUtil.send(player, msg);
        player.closeInventory();
    }
}

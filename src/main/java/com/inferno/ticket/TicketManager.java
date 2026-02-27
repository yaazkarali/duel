package com.inferno.ticket;

import com.inferno.Inferno;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TicketManager {

    private final Inferno plugin;

    public TicketManager(Inferno plugin) {
        this.plugin = plugin;
    }

    /**
     * Count the number of Duel Tickets a player has in their inventory.
     */
    public int countTickets(Player player) {
        int count = 0;
        DuelTicket duelTicket = plugin.getDuelTicket();
        for (ItemStack item : player.getInventory().getContents()) {
            if (duelTicket.isTicket(item)) {
                count += item.getAmount();
            }
        }
        return count;
    }

    /**
     * Remove N tickets from a player's inventory. Returns true if successful.
     */
    public boolean removeTickets(Player player, int amount) {
        if (countTickets(player) < amount) return false;

        DuelTicket duelTicket = plugin.getDuelTicket();
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack item = contents[i];
            if (duelTicket.isTicket(item)) {
                if (item.getAmount() <= remaining) {
                    remaining -= item.getAmount();
                    contents[i] = null;
                } else {
                    item.setAmount(item.getAmount() - remaining);
                    remaining = 0;
                }
            }
        }

        player.getInventory().setContents(contents);
        return true;
    }

    /**
     * Give N tickets to a player.
     */
    public void giveTickets(Player player, int amount) {
        DuelTicket duelTicket = plugin.getDuelTicket();
        int remaining = amount;

        while (remaining > 0) {
            int stackSize = Math.min(remaining, 64);
            ItemStack ticket = duelTicket.createTicket(stackSize);
            player.getInventory().addItem(ticket).forEach((slot, leftover) ->
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover));
            remaining -= stackSize;
        }
    }

    /**
     * Check if an item is a Duel Ticket.
     */
    public boolean isTicket(ItemStack item) {
        return plugin.getDuelTicket().isTicket(item);
    }

    /**
     * Create a Duel Ticket ItemStack.
     */
    public ItemStack createTicket(int amount) {
        return plugin.getDuelTicket().createTicket(amount);
    }
}

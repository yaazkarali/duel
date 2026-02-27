package com.inferno.listener;

import com.inferno.Inferno;
import com.inferno.util.TextUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final Inferno plugin;

    public ChatListener(Inferno plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getGUIManager().isPendingCustomAmount(player)) return;

        event.setCancelled(true);
        plugin.getGUIManager().clearPendingCustomAmount(player);

        String input = event.getMessage().trim();
        int amount;
        try {
            amount = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            TextUtil.send(player, plugin.getConfigManager().getMessage("tickets-purchase-fail-balance")
                    .replace("{cost}", input));
            return;
        }

        if (amount <= 0) {
            TextUtil.send(player, "&cAmount must be greater than 0.");
            return;
        }

        Economy economy = plugin.getEconomy();
        if (economy == null) {
            TextUtil.send(player, "&cVault economy is not available.");
            return;
        }

        double cost = amount;
        if (!economy.has(player, cost)) {
            TextUtil.send(player, TextUtil.replace(
                    plugin.getConfigManager().getMessage("tickets-purchase-fail-balance"),
                    "cost", String.valueOf((int) cost)));
            return;
        }

        economy.withdrawPlayer(player, cost);
        plugin.getTicketManager().giveTickets(player, amount);
        TextUtil.send(player, TextUtil.replace(TextUtil.replace(
                plugin.getConfigManager().getMessage("tickets-purchase-success"),
                "amount", String.valueOf(amount)),
                "cost", String.valueOf((int) cost)));
    }
}

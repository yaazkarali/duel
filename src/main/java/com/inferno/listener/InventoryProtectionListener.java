package com.inferno.listener;

import com.inferno.Inferno;
import com.inferno.duel.DuelSession;
import com.inferno.duel.DuelState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class InventoryProtectionListener implements Listener {

    private final Inferno plugin;

    public InventoryProtectionListener(Inferno plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        DuelSession session = plugin.getDuelSessionManager().getSession(player);
        if (session == null) return;

        DuelState state = session.getState();
        if (state == DuelState.ACTIVE || state == DuelState.COUNTDOWN) {
            // Allow inventory interaction but don't allow dropping to different windows
            // Players can still manage their items (hotbar, etc.)
            // Block moving items to external inventories only
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        DuelSession session = plugin.getDuelSessionManager().getSession(player);
        if (session == null) return;

        DuelState state = session.getState();
        if (state == DuelState.COUNTDOWN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        DuelSession session = plugin.getDuelSessionManager().getSession(player);
        if (session == null) return;

        DuelState state = session.getState();
        if (state == DuelState.COUNTDOWN) {
            event.setCancelled(true);
        }
    }
}

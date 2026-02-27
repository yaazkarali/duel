package com.inferno.listener;

import com.inferno.Inferno;
import com.inferno.duel.DuelSession;
import com.inferno.gui.DuelSetupGUI;
import com.inferno.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GUIClickListener implements Listener {

    private final Inferno plugin;

    public GUIClickListener(Inferno plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().title() == null) return;

        // Duel Setup GUI
        DuelSetupGUI setupGUI = plugin.getGUIManager().getDuelSetupGUIForPlayer(player);
        if (setupGUI != null && setupGUI.getInventory() != null
                && event.getInventory().equals(setupGUI.getInventory())) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;

            int slot = event.getRawSlot();
            // Bet slot special handling
            if (slot == 49) {
                int clickType = convertClickType(event.getClick());
                setupGUI.handleBetClick(player, clickType);
            } else {
                setupGUI.handleClick(player, slot);
            }
            return;
        }

        // Ticket shop GUI
        if (event.getView().title().equals(ColorUtil.color("&6Duel Ticket Shop"))) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            plugin.getTicketShopGUI().onInventoryClick(event);
        }

        // Stats GUI - read-only, cancel all
        String titleStr = plugin.getConfigManager().getMessageRaw("stats-header");
        if (event.getView().title().toString().contains("Stats")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        // If player closes the duel setup GUI during SETUP state, cancel the duel
        DuelSession session = plugin.getDuelSessionManager().getSession(player);
        if (session == null) return;

        DuelSetupGUI setupGUI = plugin.getGUIManager().getDuelSetupGUI(session);
        if (setupGUI != null && setupGUI.getInventory() != null
                && event.getInventory().equals(setupGUI.getInventory())) {
            // Only cancel if in SETUP state
            if (session.getState() == com.inferno.duel.DuelState.SETUP) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (session.getState() == com.inferno.duel.DuelState.SETUP) {
                        // Cancel the duel
                        com.inferno.util.TextUtil.send(player,
                                plugin.getConfigManager().getMessage("duel-cancelled"));
                        Player other = session.getOther(player);
                        if (other != null && other.isOnline()) {
                            com.inferno.util.TextUtil.send(other,
                                    plugin.getConfigManager().getMessage("duel-cancelled"));
                        }
                        plugin.getDuelManager().endDuel(session, null,
                                com.inferno.api.events.DuelEndEvent.EndReason.CANCELLED);
                    }
                });
            }
        }
    }

    private int convertClickType(ClickType type) {
        return switch (type) {
            case LEFT -> 0;
            case RIGHT -> 1;
            case SHIFT_LEFT -> 2;
            case SHIFT_RIGHT -> 3;
            default -> 0;
        };
    }
}

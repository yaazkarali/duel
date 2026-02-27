package com.inferno.listener;

import com.inferno.Inferno;
import com.inferno.duel.DuelSession;
import com.inferno.duel.DuelState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    private final Inferno plugin;

    public PlayerJoinQuitListener(Inferno plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Load stats into cache
        plugin.getStatsManager().getStats(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Handle disconnect during duel
        DuelSession session = plugin.getDuelSessionManager().getSession(player);
        if (session != null && session.getState() != DuelState.ENDED) {
            plugin.getDuelManager().handleDisconnect(player, session);
        }

        // Save and unload stats
        plugin.getStatsManager().unloadStats(player.getUniqueId());

        // Clean up pending requests
        plugin.getDuelRequestManager().removeAllRequests(player.getUniqueId());
    }
}

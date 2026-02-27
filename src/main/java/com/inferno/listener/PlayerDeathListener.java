package com.inferno.listener;

import com.inferno.Inferno;
import com.inferno.api.events.DuelEndEvent;
import com.inferno.duel.DuelSession;
import com.inferno.duel.DuelState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private final Inferno plugin;

    public PlayerDeathListener(Inferno plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        DuelSession session = plugin.getDuelSessionManager().getSession(player);
        if (session == null) return;
        if (session.getState() != DuelState.ACTIVE) return;

        // Clear drops to prevent item loss
        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setKeepInventory(true);

        Player winner = session.getOther(player);
        plugin.getDuelManager().endDuel(session, winner, DuelEndEvent.EndReason.DEATH);
    }
}

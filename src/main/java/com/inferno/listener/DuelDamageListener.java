package com.inferno.listener;

import com.inferno.Inferno;
import com.inferno.duel.DuelSession;
import com.inferno.duel.DuelState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DuelDamageListener implements Listener {

    private final Inferno plugin;

    public DuelDamageListener(Inferno plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        DuelSession session = plugin.getDuelSessionManager().getSession(player);
        if (session == null) return;

        // Block ALL damage during countdown
        if (session.getState() == DuelState.COUNTDOWN) {
            event.setCancelled(true);
            return;
        }

        // Block damage from non-duel participants during ACTIVE
        if (session.getState() == DuelState.ACTIVE && event instanceof EntityDamageByEntityEvent ede) {
            if (ede.getDamager() instanceof Player damager) {
                if (!session.isParticipant(damager)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}

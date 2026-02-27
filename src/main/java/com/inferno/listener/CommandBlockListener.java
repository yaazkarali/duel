package com.inferno.listener;

import com.inferno.Inferno;
import com.inferno.duel.DuelSession;
import com.inferno.duel.DuelState;
import com.inferno.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandBlockListener implements Listener {

    private final Inferno plugin;

    // Commands allowed during a duel
    private static final java.util.Set<String> ALLOWED_COMMANDS = java.util.Set.of(
            "/duelstats", "/duel stats"
    );

    public CommandBlockListener(Inferno plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        DuelSession session = plugin.getDuelSessionManager().getSession(player);
        if (session == null) return;

        // Only block during active/countdown states
        DuelState state = session.getState();
        if (state != DuelState.ACTIVE && state != DuelState.COUNTDOWN) return;

        String command = event.getMessage().toLowerCase();
        for (String allowed : ALLOWED_COMMANDS) {
            if (command.startsWith(allowed)) return;
        }

        event.setCancelled(true);
        TextUtil.send(player, plugin.getConfigManager().getMessage("duel-command-blocked"));
    }
}

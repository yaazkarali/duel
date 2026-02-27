package com.inferno.gui;

import com.inferno.Inferno;
import com.inferno.duel.DuelSession;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIManager {

    private final Inferno plugin;
    // Track open duel setup GUIs per session
    private final Map<UUID, DuelSetupGUI> setupGUIs = new HashMap<>();
    // Players waiting to type custom ticket amount in chat
    private final java.util.Set<UUID> pendingCustomAmount = new java.util.HashSet<>();

    public GUIManager(Inferno plugin) {
        this.plugin = plugin;
    }

    public void openDuelSetupGUI(DuelSession session) {
        DuelSetupGUI gui = new DuelSetupGUI(plugin, session);
        setupGUIs.put(session.getSessionId(), gui);
        gui.open();
    }

    public DuelSetupGUI getDuelSetupGUI(DuelSession session) {
        return setupGUIs.get(session.getSessionId());
    }

    public DuelSetupGUI getDuelSetupGUIForPlayer(Player player) {
        DuelSession session = plugin.getDuelSessionManager().getSession(player);
        if (session == null) return null;
        return setupGUIs.get(session.getSessionId());
    }

    public void closeDuelSetupGUI(DuelSession session) {
        DuelSetupGUI gui = setupGUIs.remove(session.getSessionId());
        if (gui != null) {
            if (session.getChallenger().isOnline()) session.getChallenger().closeInventory();
            if (session.getOpponent().isOnline()) session.getOpponent().closeInventory();
        }
    }

    public void setPendingCustomAmount(Player player) {
        pendingCustomAmount.add(player.getUniqueId());
    }

    public boolean isPendingCustomAmount(Player player) {
        return pendingCustomAmount.contains(player.getUniqueId());
    }

    public void clearPendingCustomAmount(Player player) {
        pendingCustomAmount.remove(player.getUniqueId());
    }
}

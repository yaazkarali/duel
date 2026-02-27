package com.inferno.duel;

import com.inferno.Inferno;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DuelSessionManager {

    private final Inferno plugin;
    // playerUUID -> session
    private final Map<UUID, DuelSession> sessions = new HashMap<>();

    public DuelSessionManager(Inferno plugin) {
        this.plugin = plugin;
    }

    public DuelSession createSession(Player challenger, Player opponent) {
        DuelSession session = new DuelSession(challenger, opponent);
        sessions.put(challenger.getUniqueId(), session);
        sessions.put(opponent.getUniqueId(), session);
        return session;
    }

    public DuelSession getSession(Player player) {
        return sessions.get(player.getUniqueId());
    }

    public DuelSession getSession(UUID uuid) {
        return sessions.get(uuid);
    }

    public boolean isInDuel(Player player) {
        return sessions.containsKey(player.getUniqueId());
    }

    public boolean isInDuel(UUID uuid) {
        return sessions.containsKey(uuid);
    }

    public void removeSession(DuelSession session) {
        sessions.remove(session.getChallenger().getUniqueId());
        sessions.remove(session.getOpponent().getUniqueId());
    }

    public Collection<DuelSession> getSessions() {
        // Avoid duplicates (each session stored twice)
        return sessions.values().stream().distinct().toList();
    }
}

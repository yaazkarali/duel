package com.inferno.stats;

import com.inferno.Inferno;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsManager {

    private final Inferno plugin;
    private final Map<UUID, PlayerStats> cache = new HashMap<>();

    public StatsManager(Inferno plugin) {
        this.plugin = plugin;
    }

    public PlayerStats getStats(UUID uuid) {
        return cache.computeIfAbsent(uuid, u ->
                plugin.getDatabaseManager().getDatabase().loadStats(u));
    }

    public void saveStats(UUID uuid) {
        PlayerStats stats = cache.get(uuid);
        if (stats != null) {
            plugin.getDatabaseManager().getDatabase().saveStats(stats);
        }
    }

    public void saveAllStats() {
        cache.forEach((uuid, stats) ->
                plugin.getDatabaseManager().getDatabase().saveStats(stats));
    }

    public void unloadStats(UUID uuid) {
        PlayerStats stats = cache.remove(uuid);
        if (stats != null) {
            plugin.getDatabaseManager().getDatabase().saveStats(stats);
        }
    }

    public void recordWin(UUID uuid, int betAmount) {
        getStats(uuid).addWin(betAmount);
        saveStats(uuid);
    }

    public void recordLoss(UUID uuid, int betAmount) {
        getStats(uuid).addLoss(betAmount);
        saveStats(uuid);
    }
}

package com.inferno.stats;

import com.inferno.Inferno;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class StatsPlaceholders extends PlaceholderExpansion {

    private final Inferno plugin;

    public StatsPlaceholders(Inferno plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "inferno";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Inferno";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";
        PlayerStats stats = plugin.getStatsManager().getStats(player.getUniqueId());
        return switch (params.toLowerCase()) {
            case "wins" -> String.valueOf(stats.getWins());
            case "losses" -> String.valueOf(stats.getLosses());
            case "win_streak" -> String.valueOf(stats.getWinStreak());
            case "best_streak" -> String.valueOf(stats.getBestStreak());
            case "total_bet_won" -> String.valueOf(stats.getTotalBetWon());
            case "total_bet_lost" -> String.valueOf(stats.getTotalBetLost());
            case "win_rate" -> String.format("%.1f%%", stats.getWinRate());
            default -> null;
        };
    }
}

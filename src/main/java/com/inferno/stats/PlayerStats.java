package com.inferno.stats;

import java.util.UUID;

public class PlayerStats {

    private final UUID uuid;
    private int wins;
    private int losses;
    private int totalBetWon;
    private int totalBetLost;
    private int winStreak;
    private int bestStreak;

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() { return uuid; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getTotalBetWon() { return totalBetWon; }
    public int getTotalBetLost() { return totalBetLost; }
    public int getWinStreak() { return winStreak; }
    public int getBestStreak() { return bestStreak; }

    public void setWins(int wins) { this.wins = wins; }
    public void setLosses(int losses) { this.losses = losses; }
    public void setTotalBetWon(int totalBetWon) { this.totalBetWon = totalBetWon; }
    public void setTotalBetLost(int totalBetLost) { this.totalBetLost = totalBetLost; }
    public void setWinStreak(int winStreak) { this.winStreak = winStreak; }
    public void setBestStreak(int bestStreak) { this.bestStreak = bestStreak; }

    public void addWin(int betAmount) {
        wins++;
        winStreak++;
        if (winStreak > bestStreak) bestStreak = winStreak;
        totalBetWon += betAmount;
    }

    public void addLoss(int betAmount) {
        losses++;
        winStreak = 0;
        totalBetLost += betAmount;
    }

    public double getWinRate() {
        int total = wins + losses;
        if (total == 0) return 0.0;
        return (double) wins / total * 100.0;
    }
}

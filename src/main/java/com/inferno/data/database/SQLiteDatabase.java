package com.inferno.data.database;

import com.inferno.Inferno;
import com.inferno.stats.PlayerStats;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class SQLiteDatabase extends Database {

    private final Inferno plugin;
    private Connection connection;

    public SQLiteDatabase(Inferno plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() throws Exception {
        File dbFile = new File(plugin.getDataFolder(), "inferno.db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        connection = DriverManager.getConnection(url);
        createTables();
    }

    @Override
    public void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error closing SQLite connection: " + e.getMessage());
        }
    }

    @Override
    public void createTables() throws Exception {
        String sql = """
                CREATE TABLE IF NOT EXISTS player_stats (
                    uuid TEXT PRIMARY KEY,
                    wins INTEGER DEFAULT 0,
                    losses INTEGER DEFAULT 0,
                    total_bet_won INTEGER DEFAULT 0,
                    total_bet_lost INTEGER DEFAULT 0,
                    win_streak INTEGER DEFAULT 0,
                    best_streak INTEGER DEFAULT 0
                )
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public PlayerStats loadStats(UUID uuid) {
        String sql = "SELECT * FROM player_stats WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                PlayerStats stats = new PlayerStats(uuid);
                stats.setWins(rs.getInt("wins"));
                stats.setLosses(rs.getInt("losses"));
                stats.setTotalBetWon(rs.getInt("total_bet_won"));
                stats.setTotalBetLost(rs.getInt("total_bet_lost"));
                stats.setWinStreak(rs.getInt("win_streak"));
                stats.setBestStreak(rs.getInt("best_streak"));
                return stats;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error loading stats for " + uuid + ": " + e.getMessage());
        }
        return new PlayerStats(uuid);
    }

    @Override
    public void saveStats(PlayerStats stats) {
        String sql = """
                INSERT INTO player_stats (uuid, wins, losses, total_bet_won, total_bet_lost, win_streak, best_streak)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(uuid) DO UPDATE SET
                    wins = excluded.wins,
                    losses = excluded.losses,
                    total_bet_won = excluded.total_bet_won,
                    total_bet_lost = excluded.total_bet_lost,
                    win_streak = excluded.win_streak,
                    best_streak = excluded.best_streak
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, stats.getUuid().toString());
            ps.setInt(2, stats.getWins());
            ps.setInt(3, stats.getLosses());
            ps.setInt(4, stats.getTotalBetWon());
            ps.setInt(5, stats.getTotalBetLost());
            ps.setInt(6, stats.getWinStreak());
            ps.setInt(7, stats.getBestStreak());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error saving stats for " + stats.getUuid() + ": " + e.getMessage());
        }
    }
}

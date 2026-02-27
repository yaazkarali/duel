package com.inferno.data.database;

import com.inferno.Inferno;
import com.inferno.stats.PlayerStats;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.UUID;

public class MySQLDatabase extends Database {

    private final Inferno plugin;
    private HikariDataSource dataSource;

    public MySQLDatabase(Inferno plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() throws Exception {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://"
                + config.getString("database.mysql.host", "localhost")
                + ":" + config.getInt("database.mysql.port", 3306)
                + "/" + config.getString("database.mysql.database", "inferno"));
        hikariConfig.setUsername(config.getString("database.mysql.username", "root"));
        hikariConfig.setPassword(config.getString("database.mysql.password", ""));
        hikariConfig.setMaximumPoolSize(config.getInt("database.mysql.pool-size", 10));
        hikariConfig.setPoolName("InfernoPool");
        dataSource = new HikariDataSource(hikariConfig);
        createTables();
    }

    @Override
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public void createTables() throws Exception {
        String sql = """
                CREATE TABLE IF NOT EXISTS player_stats (
                    uuid VARCHAR(36) PRIMARY KEY,
                    wins INT DEFAULT 0,
                    losses INT DEFAULT 0,
                    total_bet_won INT DEFAULT 0,
                    total_bet_lost INT DEFAULT 0,
                    win_streak INT DEFAULT 0,
                    best_streak INT DEFAULT 0
                )
                """;
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    @Override
    public PlayerStats loadStats(UUID uuid) {
        String sql = "SELECT * FROM player_stats WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
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
            plugin.getLogger().severe("Error loading MySQL stats for " + uuid + ": " + e.getMessage());
        }
        return new PlayerStats(uuid);
    }

    @Override
    public void saveStats(PlayerStats stats) {
        String sql = """
                INSERT INTO player_stats (uuid, wins, losses, total_bet_won, total_bet_lost, win_streak, best_streak)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    wins = VALUES(wins),
                    losses = VALUES(losses),
                    total_bet_won = VALUES(total_bet_won),
                    total_bet_lost = VALUES(total_bet_lost),
                    win_streak = VALUES(win_streak),
                    best_streak = VALUES(best_streak)
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stats.getUuid().toString());
            ps.setInt(2, stats.getWins());
            ps.setInt(3, stats.getLosses());
            ps.setInt(4, stats.getTotalBetWon());
            ps.setInt(5, stats.getTotalBetLost());
            ps.setInt(6, stats.getWinStreak());
            ps.setInt(7, stats.getBestStreak());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error saving MySQL stats for " + stats.getUuid() + ": " + e.getMessage());
        }
    }
}

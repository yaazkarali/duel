package com.inferno.data.database;

import com.inferno.Inferno;

public class DatabaseManager {

    private final Inferno plugin;
    private Database database;

    public DatabaseManager(Inferno plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        String type = plugin.getConfigManager().getConfig().getString("database.type", "sqlite");
        if (type.equalsIgnoreCase("mysql")) {
            database = new MySQLDatabase(plugin);
        } else {
            database = new SQLiteDatabase(plugin);
        }

        try {
            database.initialize();
            plugin.getLogger().info("Database initialized (" + type.toUpperCase() + ").");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
            plugin.getLogger().warning("Falling back to SQLite...");
            database = new SQLiteDatabase(plugin);
            try {
                database.initialize();
            } catch (Exception ex) {
                plugin.getLogger().severe("SQLite fallback also failed: " + ex.getMessage());
            }
        }
    }

    public void shutdown() {
        if (database != null) {
            database.shutdown();
        }
    }

    public Database getDatabase() {
        return database;
    }
}

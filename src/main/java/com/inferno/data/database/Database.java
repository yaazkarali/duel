package com.inferno.data.database;

import com.inferno.stats.PlayerStats;

import java.util.UUID;

public abstract class Database {

    public abstract void initialize() throws Exception;

    public abstract void shutdown();

    public abstract PlayerStats loadStats(UUID uuid);

    public abstract void saveStats(PlayerStats stats);

    public abstract void createTables() throws Exception;
}

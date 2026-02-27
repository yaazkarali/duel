package com.inferno.arena;

import com.inferno.Inferno;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ArenaManager {

    private final Inferno plugin;
    private final Map<String, Arena> arenas = new LinkedHashMap<>();

    public ArenaManager(Inferno plugin) {
        this.plugin = plugin;
    }

    public void load() {
        arenas.clear();
        FileConfiguration cfg = plugin.getConfigManager().getArenas();
        ConfigurationSection section = cfg.getConfigurationSection("arenas");
        if (section == null) return;

        for (String id : section.getKeys(false)) {
            String name = section.getString(id + ".name", id);
            Location spawn1 = loadLocation(section, id + ".spawn1");
            Location spawn2 = loadLocation(section, id + ".spawn2");
            Arena arena = new Arena(id, name, spawn1, spawn2);
            arenas.put(id, arena);
        }
        plugin.getLogger().info("Loaded " + arenas.size() + " arena(s).");
    }

    public void save() {
        FileConfiguration cfg = plugin.getConfigManager().getArenas();
        cfg.set("arenas", null);

        for (Arena arena : arenas.values()) {
            String path = "arenas." + arena.getId();
            cfg.set(path + ".name", arena.getName());
            if (arena.getSpawn1() != null) saveLocation(cfg, path + ".spawn1", arena.getSpawn1());
            if (arena.getSpawn2() != null) saveLocation(cfg, path + ".spawn2", arena.getSpawn2());
        }

        plugin.getConfigManager().saveArenas();
    }

    private Location loadLocation(ConfigurationSection section, String path) {
        String worldName = section.getString(path + ".world");
        if (worldName == null) return null;
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        return new Location(
                world,
                section.getDouble(path + ".x"),
                section.getDouble(path + ".y"),
                section.getDouble(path + ".z"),
                (float) section.getDouble(path + ".yaw", 0),
                (float) section.getDouble(path + ".pitch", 0)
        );
    }

    private void saveLocation(FileConfiguration cfg, String path, Location loc) {
        cfg.set(path + ".world", loc.getWorld().getName());
        cfg.set(path + ".x", loc.getX());
        cfg.set(path + ".y", loc.getY());
        cfg.set(path + ".z", loc.getZ());
        cfg.set(path + ".yaw", loc.getYaw());
        cfg.set(path + ".pitch", loc.getPitch());
    }

    public Arena createArena(String id) {
        Arena arena = new Arena(id, id, null, null);
        arenas.put(id, arena);
        save();
        return arena;
    }

    public void deleteArena(String id) {
        arenas.remove(id);
        save();
    }

    public Arena getArena(String id) {
        return arenas.get(id);
    }

    public Collection<Arena> getArenas() {
        return arenas.values();
    }

    /**
     * Find an available (non-occupied, fully set-up) arena.
     */
    public Optional<Arena> findAvailable() {
        List<Arena> available = arenas.values().stream()
                .filter(a -> !a.isOccupied() && a.isReady())
                .toList();
        if (available.isEmpty()) return Optional.empty();
        return Optional.of(available.get(new Random().nextInt(available.size())));
    }

    public void markOccupied(String id) {
        Arena arena = arenas.get(id);
        if (arena != null) arena.setOccupied(true);
    }

    public void markFree(String id) {
        Arena arena = arenas.get(id);
        if (arena != null) arena.setOccupied(false);
    }
}

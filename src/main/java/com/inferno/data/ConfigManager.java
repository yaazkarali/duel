package com.inferno.data;

import com.inferno.Inferno;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    private final Inferno plugin;
    private FileConfiguration config;
    private FileConfiguration messages;
    private FileConfiguration kits;
    private FileConfiguration arenas;

    private File messagesFile;
    private File kitsFile;
    private File arenasFile;

    public ConfigManager(Inferno plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        config = plugin.getConfig();

        messagesFile = saveResource("messages.yml");
        kitsFile = saveResource("kits.yml");
        arenasFile = saveResource("arenas.yml");

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        kits = YamlConfiguration.loadConfiguration(kitsFile);
        arenas = YamlConfiguration.loadConfiguration(arenasFile);
    }

    private File saveResource(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
        return file;
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        kits = YamlConfiguration.loadConfiguration(kitsFile);
        arenas = YamlConfiguration.loadConfiguration(arenasFile);
    }

    public void saveArenas() {
        try {
            arenas.save(arenasFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to save arenas.yml: " + e.getMessage());
        }
    }

    public String getMessage(String key) {
        String prefix = messages.getString("prefix", "&8[&c&lInferno&8] &r");
        String msg = messages.getString(key, "&cMessage not found: " + key);
        return prefix + msg;
    }

    public String getMessageRaw(String key) {
        return messages.getString(key, "&cMessage not found: " + key);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public FileConfiguration getKits() {
        return kits;
    }

    public FileConfiguration getArenas() {
        return arenas;
    }

    public File getArenasFile() {
        return arenasFile;
    }
}

package com.inferno;

import com.inferno.arena.ArenaManager;
import com.inferno.command.*;
import com.inferno.data.ConfigManager;
import com.inferno.data.database.DatabaseManager;
import com.inferno.duel.DuelManager;
import com.inferno.duel.DuelRequestManager;
import com.inferno.duel.DuelSessionManager;
import com.inferno.gui.GUIManager;
import com.inferno.gui.StatsGUI;
import com.inferno.kit.KitManager;
import com.inferno.listener.*;
import com.inferno.stats.StatsManager;
import com.inferno.stats.StatsPlaceholders;
import com.inferno.ticket.DuelTicket;
import com.inferno.ticket.TicketManager;
import com.inferno.ticket.TicketShopGUI;
import com.inferno.ticket.TicketVendorListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Inferno extends JavaPlugin {

    private static Inferno instance;

    // Managers
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private StatsManager statsManager;
    private ArenaManager arenaManager;
    private KitManager kitManager;
    private DuelRequestManager duelRequestManager;
    private DuelSessionManager duelSessionManager;
    private DuelManager duelManager;
    private GUIManager guiManager;

    // Items
    private DuelTicket duelTicket;
    private TicketManager ticketManager;

    // GUIs
    private TicketShopGUI ticketShopGUI;
    private StatsGUI statsGUI;

    // Vault economy
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;

        // Load config
        configManager = new ConfigManager(this);
        configManager.load();

        // Setup database
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();

        // Setup managers
        statsManager = new StatsManager(this);
        arenaManager = new ArenaManager(this);
        arenaManager.load();
        kitManager = new KitManager(this);
        kitManager.load();

        // Ticket system
        duelTicket = new DuelTicket(this);
        ticketManager = new TicketManager(this);
        ticketShopGUI = new TicketShopGUI(this);
        statsGUI = new StatsGUI(this);

        // Duel system
        duelRequestManager = new DuelRequestManager(this);
        duelSessionManager = new DuelSessionManager(this);
        duelManager = new DuelManager(this);
        guiManager = new GUIManager(this);

        // Hook into Vault
        setupVault();

        // Register listeners
        registerListeners();

        // Register commands
        registerCommands();

        // PlaceholderAPI
        setupPlaceholderAPI();

        getLogger().info("Inferno Duel System enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Save all stats
        if (statsManager != null) statsManager.saveAllStats();

        // Shutdown database
        if (databaseManager != null) databaseManager.shutdown();

        getLogger().info("Inferno Duel System disabled.");
    }

    private void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault not found. Economy features disabled.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().warning("No economy provider found. Economy features disabled.");
            return;
        }
        economy = rsp.getProvider();
        getLogger().info("Vault economy hooked successfully.");
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinQuitListener(this), this);
        pm.registerEvents(new CommandBlockListener(this), this);
        pm.registerEvents(new InventoryProtectionListener(this), this);
        pm.registerEvents(new PlayerDeathListener(this), this);
        pm.registerEvents(new PlayerMoveListener(this), this);
        pm.registerEvents(new LobbyEntranceListener(this), this);
        pm.registerEvents(new DuelDamageListener(this), this);
        pm.registerEvents(new PlayerRespawnListener(this), this);
        pm.registerEvents(new GUIClickListener(this), this);
        pm.registerEvents(new ChatListener(this), this);
        pm.registerEvents(ticketShopGUI, this);

        // Citizens integration (soft dependency)
        if (getServer().getPluginManager().getPlugin("Citizens") != null) {
            try {
                pm.registerEvents(new TicketVendorListener(this), this);
                getLogger().info("Citizens NPC vendor support enabled.");
            } catch (Exception e) {
                getLogger().warning("Failed to hook Citizens: " + e.getMessage());
            }
        }
    }

    private void registerCommands() {
        TabCompleters tabCompleters = new TabCompleters(this);

        registerCommand("duel", new DuelCommand(this), tabCompleters);
        registerCommand("duelaccept", new DuelAcceptCommand(this), tabCompleters);
        registerCommand("dueldeny", new DuelDenyCommand(this), tabCompleters);
        registerCommand("duelstats", new DuelStatsCommand(this), tabCompleters);
        registerCommand("dueladmin", new DuelAdminCommand(this), tabCompleters);
    }

    private void registerCommand(String name, org.bukkit.command.CommandExecutor executor,
                                  org.bukkit.command.TabCompleter completer) {
        var cmd = getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(executor);
            cmd.setTabCompleter(completer);
        } else {
            getLogger().warning("Command not found in plugin.yml: " + name);
        }
    }

    private void setupPlaceholderAPI() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                new StatsPlaceholders(this).register();
                getLogger().info("PlaceholderAPI hooked successfully.");
            } catch (Exception e) {
                getLogger().warning("Failed to hook PlaceholderAPI: " + e.getMessage());
            }
        }
    }

    public static Inferno getInstance() {
        return instance;
    }

    // Getters
    public ConfigManager getConfigManager() { return configManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public StatsManager getStatsManager() { return statsManager; }
    public ArenaManager getArenaManager() { return arenaManager; }
    public KitManager getKitManager() { return kitManager; }
    public DuelRequestManager getDuelRequestManager() { return duelRequestManager; }
    public DuelSessionManager getDuelSessionManager() { return duelSessionManager; }
    public DuelManager getDuelManager() { return duelManager; }
    public GUIManager getGUIManager() { return guiManager; }
    public DuelTicket getDuelTicket() { return duelTicket; }
    public TicketManager getTicketManager() { return ticketManager; }
    public TicketShopGUI getTicketShopGUI() { return ticketShopGUI; }
    public StatsGUI getStatsGUI() { return statsGUI; }
    public Economy getEconomy() { return economy; }
}

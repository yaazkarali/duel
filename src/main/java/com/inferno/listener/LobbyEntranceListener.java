package com.inferno.listener;

import com.inferno.Inferno;
import com.inferno.util.TextUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class LobbyEntranceListener implements Listener {

    private final Inferno plugin;

    public LobbyEntranceListener(Inferno plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to == null) return;

        FileConfiguration config = plugin.getConfigManager().getConfig();
        String worldName = config.getString("lobby.world", "world");
        if (to.getWorld() == null || !to.getWorld().getName().equals(worldName)) return;

        double minX = config.getDouble("lobby.region.min.x", -50);
        double minY = config.getDouble("lobby.region.min.y", 60);
        double minZ = config.getDouble("lobby.region.min.z", -50);
        double maxX = config.getDouble("lobby.region.max.x", 50);
        double maxY = config.getDouble("lobby.region.max.y", 80);
        double maxZ = config.getDouble("lobby.region.max.z", 50);

        if (!isInRegion(to, minX, minY, minZ, maxX, maxY, maxZ)) return;

        // Already in duel - skip
        if (plugin.getDuelSessionManager().isInDuel(player)) return;

        // Check inventory: must be empty except for duel tickets
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (!plugin.getTicketManager().isTicket(item)) {
                TextUtil.send(player, plugin.getConfigManager().getMessage("inventory-not-empty"));
                // Teleport back
                Location from = event.getFrom();
                player.teleport(from);
                return;
            }
        }
    }

    private boolean isInRegion(Location loc, double minX, double minY, double minZ,
                                double maxX, double maxY, double maxZ) {
        return loc.getX() >= minX && loc.getX() <= maxX
                && loc.getY() >= minY && loc.getY() <= maxY
                && loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
}

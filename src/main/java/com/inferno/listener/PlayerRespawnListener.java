package com.inferno.listener;

import com.inferno.Inferno;
import com.inferno.duel.DuelSession;
import com.inferno.duel.DuelState;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    private final Inferno plugin;

    public PlayerRespawnListener(Inferno plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        // If player was in a duel, the endDuel method already handles teleporting back
        // This listener ensures the respawn location is set to lobby if needed
        DuelSession session = plugin.getDuelSessionManager().getSession(player);
        if (session != null && session.getState() == DuelState.ENDED) return;

        if (session != null) {
            Location lobby = getLobbySpawn();
            if (lobby != null) {
                event.setRespawnLocation(lobby);
            }
        }
    }

    private Location getLobbySpawn() {
        var cfg = plugin.getConfigManager().getConfig();
        String worldName = cfg.getString("lobby.world", "world");
        var world = plugin.getServer().getWorld(worldName);
        if (world == null) return null;
        return new Location(world,
                cfg.getDouble("lobby.spawn.x", 0.5),
                cfg.getDouble("lobby.spawn.y", 64.0),
                cfg.getDouble("lobby.spawn.z", 0.5),
                (float) cfg.getDouble("lobby.spawn.yaw", 0),
                (float) cfg.getDouble("lobby.spawn.pitch", 0));
    }
}

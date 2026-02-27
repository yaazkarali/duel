package com.inferno.duel;

import com.inferno.Inferno;
import com.inferno.api.events.DuelEndEvent;
import com.inferno.arena.Arena;
import com.inferno.kit.KitApplier;
import com.inferno.util.TextUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Core duel lifecycle manager: start, end, teleport, etc.
 */
public class DuelManager {

    private final Inferno plugin;

    public DuelManager(Inferno plugin) {
        this.plugin = plugin;
    }

    /**
     * Start the duel: remove tickets, teleport, apply kits, begin countdown.
     */
    public void startDuel(DuelSession session) {
        Player challenger = session.getChallenger();
        Player opponent = session.getOpponent();

        int bet = session.getBetAmount();

        // Remove tickets from both players
        if (bet > 0) {
            plugin.getTicketManager().removeTickets(challenger, bet);
            plugin.getTicketManager().removeTickets(opponent, bet);
        }

        // Find available arena
        Optional<Arena> arenaOpt = plugin.getArenaManager().findAvailable();
        if (arenaOpt.isEmpty()) {
            TextUtil.send(challenger, "&cNo arenas are available right now. Please try again.");
            TextUtil.send(opponent, "&cNo arenas are available right now. Please try again.");
            endDuel(session, null, DuelEndEvent.EndReason.CANCELLED);
            return;
        }

        Arena arena = arenaOpt.get();
        session.setArena(arena);
        plugin.getArenaManager().markOccupied(arena.getId());

        // Set state to countdown before teleporting
        session.setState(DuelState.COUNTDOWN);

        // Teleport players
        challenger.teleport(arena.getSpawn1());
        opponent.teleport(arena.getSpawn2());

        // Apply kits
        KitApplier.apply(challenger, session.getSelectedKit());
        KitApplier.apply(opponent, session.getSelectedKit());

        // Start countdown
        DuelCountdown countdown = new DuelCountdown(plugin, session);
        countdown.runTaskTimer(plugin, 0L, 20L);
    }

    /**
     * End a duel. Winner/loser may be null if cancelled.
     */
    public void endDuel(DuelSession session, Player winner, DuelEndEvent.EndReason reason) {
        if (session.getState() == DuelState.ENDED) return;
        session.setState(DuelState.ENDED);

        Player challenger = session.getChallenger();
        Player opponent = session.getOpponent();
        Player loser = winner != null ? session.getOther(winner) : null;

        // Distribute tickets
        if (winner != null && session.getBetAmount() > 0) {
            int totalWin = session.getBetAmount() * 2;
            plugin.getTicketManager().giveTickets(winner, totalWin);
            String winMsg = TextUtil.replace(
                    plugin.getConfigManager().getMessage("duel-tickets-won"),
                    "amount", String.valueOf(totalWin));
            TextUtil.send(winner, winMsg);
        }

        // Update stats
        if (winner != null && loser != null) {
            int bet = session.getBetAmount();
            plugin.getStatsManager().recordWin(winner.getUniqueId(), bet);
            plugin.getStatsManager().recordLoss(loser.getUniqueId(), bet);

            // Send messages
            TextUtil.send(winner, TextUtil.replace(
                    plugin.getConfigManager().getMessage("duel-winner"),
                    "player", loser.getName()));
            TextUtil.send(loser, TextUtil.replace(
                    plugin.getConfigManager().getMessage("duel-loser"),
                    "player", winner.getName()));
        }

        // Teleport back to lobby
        Location lobby = getLobbySpawn();
        if (lobby != null) {
            if (challenger.isOnline()) challenger.teleport(lobby);
            if (opponent.isOnline()) opponent.teleport(lobby);
        }

        // Clear inventories (kit items)
        if (challenger.isOnline()) challenger.getInventory().clear();
        if (opponent.isOnline()) opponent.getInventory().clear();

        // Free arena
        if (session.getArena() != null) {
            plugin.getArenaManager().markFree(session.getArena().getId());
        }

        // Fire DuelEndEvent
        DuelEndEvent event = new DuelEndEvent(session, winner, loser, reason);
        plugin.getServer().getPluginManager().callEvent(event);

        // Remove session
        plugin.getDuelSessionManager().removeSession(session);

        // Close GUIs
        plugin.getGUIManager().closeDuelSetupGUI(session);
    }

    /**
     * Handle a player disconnecting mid-duel.
     */
    public void handleDisconnect(Player disconnected, DuelSession session) {
        if (session.getState() == DuelState.ENDED) return;

        Player other = session.getOther(disconnected);
        if (other != null && other.isOnline()) {
            TextUtil.send(other, TextUtil.replace(
                    plugin.getConfigManager().getMessage("duel-disconnect-lose"),
                    "player", disconnected.getName()));
            endDuel(session, other, DuelEndEvent.EndReason.DISCONNECT);
        } else {
            endDuel(session, null, DuelEndEvent.EndReason.CANCELLED);
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

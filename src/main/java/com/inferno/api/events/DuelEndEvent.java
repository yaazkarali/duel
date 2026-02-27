package com.inferno.api.events;

import com.inferno.duel.DuelSession;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a duel concludes.
 */
public class DuelEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DuelSession session;
    private final Player winner;
    private final Player loser;
    private final EndReason reason;

    public DuelEndEvent(DuelSession session, @Nullable Player winner, @Nullable Player loser, EndReason reason) {
        this.session = session;
        this.winner = winner;
        this.loser = loser;
        this.reason = reason;
    }

    public DuelSession getSession() {
        return session;
    }

    @Nullable
    public Player getWinner() {
        return winner;
    }

    @Nullable
    public Player getLoser() {
        return loser;
    }

    public EndReason getReason() {
        return reason;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public enum EndReason {
        DEATH,
        DISCONNECT,
        CANCELLED
    }
}

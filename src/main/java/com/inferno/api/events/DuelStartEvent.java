package com.inferno.api.events;

import com.inferno.duel.DuelSession;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a duel begins (after countdown, before FIGHT).
 */
public class DuelStartEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DuelSession session;
    private boolean cancelled;

    public DuelStartEvent(DuelSession session) {
        this.session = session;
        this.cancelled = false;
    }

    public DuelSession getSession() {
        return session;
    }

    public Player getChallenger() {
        return session.getChallenger();
    }

    public Player getOpponent() {
        return session.getOpponent();
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

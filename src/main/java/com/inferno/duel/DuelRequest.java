package com.inferno.duel;

import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.UUID;

public class DuelRequest {

    private final UUID senderUUID;
    private final UUID receiverUUID;
    private final Player sender;
    private final Player receiver;
    private final Instant createdAt;
    private final int expirySeconds;

    public DuelRequest(Player sender, Player receiver, int expirySeconds) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderUUID = sender.getUniqueId();
        this.receiverUUID = receiver.getUniqueId();
        this.createdAt = Instant.now();
        this.expirySeconds = expirySeconds;
    }

    public Player getSender() { return sender; }
    public Player getReceiver() { return receiver; }
    public UUID getSenderUUID() { return senderUUID; }
    public UUID getReceiverUUID() { return receiverUUID; }
    public Instant getCreatedAt() { return createdAt; }

    public boolean isExpired() {
        return Instant.now().isAfter(createdAt.plusSeconds(expirySeconds));
    }
}

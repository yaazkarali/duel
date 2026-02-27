package com.inferno.duel;

import com.inferno.Inferno;
import com.inferno.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DuelRequestManager {

    private final Inferno plugin;
    // Key: sender UUID -> Map<receiverUUID, DuelRequest>
    private final Map<UUID, Map<UUID, DuelRequest>> pendingRequests = new HashMap<>();
    // Cooldowns: senderUUID -> time when cooldown expires
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public DuelRequestManager(Inferno plugin) {
        this.plugin = plugin;
        startExpiryTask();
    }

    private void startExpiryTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkExpired();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void checkExpired() {
        int expiry = plugin.getConfigManager().getConfig().getInt("duel-request.expiry-seconds", 30);
        Iterator<Map.Entry<UUID, Map<UUID, DuelRequest>>> it = pendingRequests.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Map<UUID, DuelRequest>> entry = it.next();
            entry.getValue().entrySet().removeIf(e -> {
                DuelRequest req = e.getValue();
                if (req.isExpired()) {
                    // Notify players
                    Player sender = req.getSender();
                    Player receiver = req.getReceiver();
                    if (sender != null && sender.isOnline()) {
                        TextUtil.send(sender, TextUtil.replace(
                                plugin.getConfigManager().getMessage("duel-request-expired-sender"),
                                "player", receiver.getName()));
                    }
                    if (receiver != null && receiver.isOnline()) {
                        TextUtil.send(receiver, TextUtil.replace(
                                plugin.getConfigManager().getMessage("duel-request-expired-receiver"),
                                "player", sender.getName()));
                    }
                    return true;
                }
                return false;
            });
            if (entry.getValue().isEmpty()) it.remove();
        }
    }

    /**
     * Send a duel request from sender to receiver.
     * Returns false if request already exists or on cooldown.
     */
    public boolean sendRequest(Player sender, Player receiver) {
        UUID senderUUID = sender.getUniqueId();
        UUID receiverUUID = receiver.getUniqueId();

        // Check cooldown
        long now = System.currentTimeMillis();
        Long cooldownEnd = cooldowns.get(senderUUID);
        if (cooldownEnd != null && now < cooldownEnd) return false;

        // Check existing request
        if (hasPendingRequest(senderUUID, receiverUUID)) return false;

        int expiry = plugin.getConfigManager().getConfig().getInt("duel-request.expiry-seconds", 30);
        DuelRequest request = new DuelRequest(sender, receiver, expiry);
        pendingRequests.computeIfAbsent(senderUUID, k -> new HashMap<>()).put(receiverUUID, request);

        // Set cooldown
        int cooldownSec = plugin.getConfigManager().getConfig().getInt("duel-request.cooldown-seconds", 5);
        cooldowns.put(senderUUID, now + cooldownSec * 1000L);

        return true;
    }

    public boolean hasPendingRequest(UUID senderUUID, UUID receiverUUID) {
        Map<UUID, DuelRequest> map = pendingRequests.get(senderUUID);
        if (map == null) return false;
        DuelRequest req = map.get(receiverUUID);
        return req != null && !req.isExpired();
    }

    /**
     * Get a pending request from sender to receiver (where sender sent TO this receiver).
     */
    public DuelRequest getRequest(UUID senderUUID, UUID receiverUUID) {
        Map<UUID, DuelRequest> map = pendingRequests.get(senderUUID);
        if (map == null) return null;
        DuelRequest req = map.get(receiverUUID);
        if (req != null && req.isExpired()) {
            map.remove(receiverUUID);
            return null;
        }
        return req;
    }

    /**
     * Find a pending request where 'senderUUID' sent to 'receiverUUID'.
     * Used from receiver perspective: receiverUUID is the one accepting,
     * senderUUID is the challenger.
     */
    public DuelRequest findRequestTo(UUID receiverUUID, UUID senderUUID) {
        return getRequest(senderUUID, receiverUUID);
    }

    public void removeRequest(UUID senderUUID, UUID receiverUUID) {
        Map<UUID, DuelRequest> map = pendingRequests.get(senderUUID);
        if (map != null) {
            map.remove(receiverUUID);
            if (map.isEmpty()) pendingRequests.remove(senderUUID);
        }
    }

    public void removeAllRequests(UUID playerUUID) {
        pendingRequests.remove(playerUUID);
        pendingRequests.values().forEach(m -> m.remove(playerUUID));
    }

    public boolean isOnCooldown(UUID senderUUID) {
        Long cooldownEnd = cooldowns.get(senderUUID);
        return cooldownEnd != null && System.currentTimeMillis() < cooldownEnd;
    }
}

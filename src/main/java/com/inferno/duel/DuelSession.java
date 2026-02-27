package com.inferno.duel;

import com.inferno.arena.Arena;
import com.inferno.kit.DuelKit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelSession {

    private final UUID sessionId;
    private final Player challenger;
    private final Player opponent;
    private Arena arena;
    private DuelKit selectedKit;
    private int betAmount;
    private DuelState state;

    // Per-player tracking for GUI setup
    private DuelKit challengerKitChoice;
    private DuelKit opponentKitChoice;
    private int challengerBet;
    private int opponentBet;
    private boolean challengerConfirmed;
    private boolean opponentConfirmed;

    public DuelSession(Player challenger, Player opponent) {
        this.sessionId = UUID.randomUUID();
        this.challenger = challenger;
        this.opponent = opponent;
        this.state = DuelState.SETUP;
        this.challengerBet = 0;
        this.opponentBet = 0;
        this.challengerConfirmed = false;
        this.opponentConfirmed = false;
    }

    public UUID getSessionId() { return sessionId; }
    public Player getChallenger() { return challenger; }
    public Player getOpponent() { return opponent; }
    public Arena getArena() { return arena; }
    public DuelKit getSelectedKit() { return selectedKit; }
    public int getBetAmount() { return betAmount; }
    public DuelState getState() { return state; }

    public DuelKit getChallengerKitChoice() { return challengerKitChoice; }
    public DuelKit getOpponentKitChoice() { return opponentKitChoice; }
    public int getChallengerBet() { return challengerBet; }
    public int getOpponentBet() { return opponentBet; }
    public boolean isChallengerConfirmed() { return challengerConfirmed; }
    public boolean isOpponentConfirmed() { return opponentConfirmed; }

    public void setArena(Arena arena) { this.arena = arena; }
    public void setSelectedKit(DuelKit kit) { this.selectedKit = kit; }
    public void setBetAmount(int amount) { this.betAmount = amount; }
    public void setState(DuelState state) { this.state = state; }

    public void setChallengerKitChoice(DuelKit kit) { this.challengerKitChoice = kit; }
    public void setOpponentKitChoice(DuelKit kit) { this.opponentKitChoice = kit; }
    public void setChallengerBet(int amount) { this.challengerBet = amount; }
    public void setOpponentBet(int amount) { this.opponentBet = amount; }
    public void setChallengerConfirmed(boolean confirmed) { this.challengerConfirmed = confirmed; }
    public void setOpponentConfirmed(boolean confirmed) { this.opponentConfirmed = confirmed; }

    public boolean isChallenger(Player player) {
        return player.getUniqueId().equals(challenger.getUniqueId());
    }

    public boolean isParticipant(Player player) {
        return isChallenger(player) || player.getUniqueId().equals(opponent.getUniqueId());
    }

    public Player getOther(Player player) {
        return isChallenger(player) ? opponent : challenger;
    }

    /**
     * Returns true if both players have confirmed and kits match.
     */
    public boolean isReadyToStart() {
        return challengerConfirmed && opponentConfirmed
                && challengerKitChoice != null && opponentKitChoice != null
                && challengerKitChoice.getId().equals(opponentKitChoice.getId())
                && challengerBet == opponentBet;
    }

    /**
     * Finalize kit and bet before starting.
     */
    public void finalize(DuelKit kit, int bet) {
        this.selectedKit = kit;
        this.betAmount = bet;
    }
}

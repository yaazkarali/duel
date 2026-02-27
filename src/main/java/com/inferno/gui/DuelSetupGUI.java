package com.inferno.gui;

import com.inferno.Inferno;
import com.inferno.duel.DuelSession;
import com.inferno.kit.DuelKit;
import com.inferno.util.ColorUtil;
import com.inferno.util.ItemBuilder;
import com.inferno.util.SkullUtil;
import com.inferno.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The shared duel setup GUI shown to both players simultaneously.
 * Layout (6 rows, 54 slots):
 * Row 0: challenger head (slot 4), title (slot 22 center), opponent head (slot 49)
 * Row 1-2: kit selections (center area)
 * Row 5: bet button (slot 46), challenger confirm (slot 45), opponent confirm (slot 53)
 */
public class DuelSetupGUI {

    private static final String TITLE = "&8⚔ &6Duel Setup &8⚔";

    private final Inferno plugin;
    private final DuelSession session;
    private Inventory inventory;

    // Kit slots: 6 kits in slots 20,21,22,23,24,25 (row 2-3)
    private static final int[] KIT_SLOTS = {20, 21, 22, 23, 24, 25};
    private static final int CHALLENGER_HEAD_SLOT = 3;
    private static final int OPPONENT_HEAD_SLOT = 5;
    private static final int CHALLENGER_KIT_STATUS_SLOT = 29;
    private static final int OPPONENT_KIT_STATUS_SLOT = 33;
    private static final int BET_SLOT = 49;
    private static final int CHALLENGER_CONFIRM_SLOT = 45;
    private static final int OPPONENT_CONFIRM_SLOT = 53;

    public DuelSetupGUI(Inferno plugin, DuelSession session) {
        this.plugin = plugin;
        this.session = session;
    }

    public void open() {
        this.inventory = Bukkit.createInventory(null, 54, ColorUtil.color(TITLE));
        refresh();
        session.getChallenger().openInventory(inventory);
        session.getOpponent().openInventory(inventory);
    }

    public void refresh() {
        if (inventory == null) return;

        // Fill background
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("&r").build();
        for (int i = 0; i < 54; i++) inventory.setItem(i, filler);

        // Challenger head
        ItemStack chalHead = buildPlayerHead(session.getChallenger(), true);
        inventory.setItem(CHALLENGER_HEAD_SLOT, chalHead);

        // Opponent head
        ItemStack oppHead = buildPlayerHead(session.getOpponent(), false);
        inventory.setItem(OPPONENT_HEAD_SLOT, oppHead);

        // Title center
        inventory.setItem(4, new ItemBuilder(Material.BEACON)
                .name("&6&l⚔ Duel Setup ⚔")
                .lore("&7Select a kit and set your bet.", "&7Both players must confirm to start!")
                .build());

        // Kit buttons
        List<DuelKit> kits = plugin.getKitManager().getKitList();
        for (int i = 0; i < KIT_SLOTS.length; i++) {
            if (i < kits.size()) {
                DuelKit kit = kits.get(i);
                boolean chalChose = session.getChallengerKitChoice() != null
                        && session.getChallengerKitChoice().getId().equals(kit.getId());
                boolean oppChose = session.getOpponentKitChoice() != null
                        && session.getOpponentKitChoice().getId().equals(kit.getId());

                List<String> lore = new ArrayList<>();
                lore.add("&7" + kit.getDescription());
                lore.add("");
                if (chalChose) lore.add("&a✔ " + session.getChallenger().getName() + " selected");
                if (oppChose) lore.add("&a✔ " + session.getOpponent().getName() + " selected");
                if (!chalChose && !oppChose) lore.add("&eClick to select this kit!");

                inventory.setItem(KIT_SLOTS[i], new ItemBuilder(kit.getIcon())
                        .name(kit.getDisplayName())
                        .lore(lore)
                        .build());
            } else {
                inventory.setItem(KIT_SLOTS[i], filler);
            }
        }

        // Challenger kit status
        String chalKitName = session.getChallengerKitChoice() != null
                ? session.getChallengerKitChoice().getDisplayName() : "&cNone";
        inventory.setItem(CHALLENGER_KIT_STATUS_SLOT, new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
                .name("&e" + session.getChallenger().getName() + "'s Kit")
                .lore("&7Selected: " + chalKitName)
                .build());

        // Opponent kit status
        String oppKitName = session.getOpponentKitChoice() != null
                ? session.getOpponentKitChoice().getDisplayName() : "&cNone";
        inventory.setItem(OPPONENT_KIT_STATUS_SLOT, new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE)
                .name("&e" + session.getOpponent().getName() + "'s Kit")
                .lore("&7Selected: " + oppKitName)
                .build());

        // Bet button (center bottom)
        int chalTickets = plugin.getTicketManager().countTickets(session.getChallenger());
        int oppTickets = plugin.getTicketManager().countTickets(session.getOpponent());
        inventory.setItem(BET_SLOT, new ItemBuilder(Material.GOLD_NUGGET)
                .name("&6Bet Amount")
                .lore(
                        "&7Current bet: &e" + session.getChallengerBet() + " tickets",
                        "&7" + session.getChallenger().getName() + " tickets: &e" + chalTickets,
                        "&7" + session.getOpponent().getName() + " tickets: &e" + oppTickets,
                        "",
                        "&eLeft-click to increase bet (+1)",
                        "&eRight-click to decrease bet (-1)",
                        "&eShift+left to +10, Shift+right to -10"
                )
                .build());

        // Challenger confirm button
        Material chalConfirmMat = session.isChallengerConfirmed()
                ? Material.LIME_WOOL : Material.RED_WOOL;
        String chalConfirmStatus = session.isChallengerConfirmed() ? "&a✔ Confirmed" : "&cNot Confirmed";
        inventory.setItem(CHALLENGER_CONFIRM_SLOT, new ItemBuilder(chalConfirmMat)
                .name("&e" + session.getChallenger().getName() + " - Confirm")
                .lore("&7Status: " + chalConfirmStatus,
                        "",
                        "&eClick to confirm/unconfirm")
                .build());

        // Opponent confirm button
        Material oppConfirmMat = session.isOpponentConfirmed()
                ? Material.LIME_WOOL : Material.RED_WOOL;
        String oppConfirmStatus = session.isOpponentConfirmed() ? "&a✔ Confirmed" : "&cNot Confirmed";
        inventory.setItem(OPPONENT_CONFIRM_SLOT, new ItemBuilder(oppConfirmMat)
                .name("&e" + session.getOpponent().getName() + " - Confirm")
                .lore("&7Status: " + oppConfirmStatus,
                        "",
                        "&eClick to confirm/unconfirm")
                .build());
    }

    public void handleClick(Player player, int slot) {
        boolean isChallenger = session.isChallenger(player);

        // Kit selection
        List<DuelKit> kits = plugin.getKitManager().getKitList();
        for (int i = 0; i < KIT_SLOTS.length; i++) {
            if (slot == KIT_SLOTS[i]) {
                if (i < kits.size()) {
                    DuelKit selected = kits.get(i);
                    if (isChallenger) {
                        session.setChallengerKitChoice(selected);
                        session.setChallengerConfirmed(false);
                    } else {
                        session.setOpponentKitChoice(selected);
                        session.setOpponentConfirmed(false);
                    }
                    refresh();

                    // Notify other player
                    Player other = session.getOther(player);
                    String kitMsg = TextUtil.replace(TextUtil.replace(
                            plugin.getConfigManager().getMessage("kit-other-selected"),
                            "player", player.getName()),
                            "kit", selected.getDisplayName());
                    TextUtil.send(other, kitMsg);
                }
                return;
            }
        }

        // Bet button
        if (slot == BET_SLOT) {
            return; // Handled in click event with click type
        }

        // Confirm buttons
        if (slot == CHALLENGER_CONFIRM_SLOT && isChallenger) {
            toggleConfirm(player, true);
        } else if (slot == OPPONENT_CONFIRM_SLOT && !isChallenger) {
            toggleConfirm(player, false);
        }
    }

    public void handleBetClick(Player player, int clickType) {
        // clickType: 0=left, 1=right, 2=shift+left, 3=shift+right
        int delta = switch (clickType) {
            case 0 -> 1;
            case 1 -> -1;
            case 2 -> 10;
            case 3 -> -10;
            default -> 0;
        };
        if (delta == 0) return;

        boolean isChallenger = session.isChallenger(player);
        int currentBet = isChallenger ? session.getChallengerBet() : session.getOpponentBet();
        int maxTickets = plugin.getTicketManager().countTickets(player);
        int newBet = Math.max(0, Math.min(maxTickets, currentBet + delta));

        if (isChallenger) {
            session.setChallengerBet(newBet);
            session.setChallengerConfirmed(false);
        } else {
            session.setOpponentBet(newBet);
            session.setOpponentConfirmed(false);
        }

        TextUtil.send(player, TextUtil.replace(
                plugin.getConfigManager().getMessage("bet-updated"),
                "amount", String.valueOf(newBet)));
        refresh();
    }

    private void toggleConfirm(Player player, boolean isChallenger) {
        // Validate kit selection
        DuelKit kitChoice = isChallenger ? session.getChallengerKitChoice() : session.getOpponentKitChoice();
        if (kitChoice == null) {
            TextUtil.send(player, "&cYou must select a kit first.");
            return;
        }

        // Validate kit match
        DuelKit otherKitChoice = isChallenger ? session.getOpponentKitChoice() : session.getChallengerKitChoice();
        if (otherKitChoice != null && !kitChoice.getId().equals(otherKitChoice.getId())) {
            TextUtil.send(player, plugin.getConfigManager().getMessage("kit-not-agreed"));
            return;
        }

        // Validate bet
        int myBet = isChallenger ? session.getChallengerBet() : session.getOpponentBet();
        int theirBet = isChallenger ? session.getOpponentBet() : session.getChallengerBet();
        if (myBet != theirBet && theirBet > 0) {
            TextUtil.send(player, plugin.getConfigManager().getMessage("bet-mismatch"));
            return;
        }

        // Check tickets
        if (myBet > plugin.getTicketManager().countTickets(player)) {
            TextUtil.send(player, plugin.getConfigManager().getMessage("bet-not-enough-tickets")
                    .replace("{count}", String.valueOf(plugin.getTicketManager().countTickets(player))));
            return;
        }

        if (isChallenger) {
            session.setChallengerConfirmed(!session.isChallengerConfirmed());
        } else {
            session.setOpponentConfirmed(!session.isOpponentConfirmed());
        }

        refresh();

        Player other = session.getOther(player);

        if (isChallenger ? session.isChallengerConfirmed() : session.isOpponentConfirmed()) {
            TextUtil.send(player, TextUtil.replace(
                    plugin.getConfigManager().getMessage("confirm-waiting"),
                    "player", other.getName()));
        }

        // Check if both confirmed
        if (session.isReadyToStart()) {
            TextUtil.send(session.getChallenger(), plugin.getConfigManager().getMessage("confirm-both"));
            TextUtil.send(session.getOpponent(), plugin.getConfigManager().getMessage("confirm-both"));

            session.finalize(kitChoice, myBet);
            plugin.getDuelManager().startDuel(session);
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    private ItemStack buildPlayerHead(Player player, boolean isChallenger) {
        var stats = plugin.getStatsManager().getStats(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add("&7Wins: &a" + stats.getWins());
        lore.add("&7Losses: &c" + stats.getLosses());
        lore.add("&7Win Streak: &e" + stats.getWinStreak());
        lore.add("");
        lore.add("&7Role: " + (isChallenger ? "&eChallenger" : "&bOpponent"));

        return SkullUtil.getSkull(player.getUniqueId(),
                ColorUtil.color("&e" + player.getName() + (isChallenger ? " &7(Challenger)" : " &7(Opponent)")));
    }
}

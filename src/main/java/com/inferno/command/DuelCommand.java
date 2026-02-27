package com.inferno.command;

import com.inferno.Inferno;
import com.inferno.api.events.DuelRequestEvent;
import com.inferno.duel.DuelSession;
import com.inferno.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DuelCommand implements CommandExecutor {

    private final Inferno plugin;

    public DuelCommand(Inferno plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            return true;
        }

        if (!player.hasPermission("inferno.duel")) {
            TextUtil.send(player, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 1) {
            TextUtil.send(player, "&cUsage: /duel <player>");
            return true;
        }

        Player target = plugin.getServer().getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            TextUtil.send(player, TextUtil.replace(
                    plugin.getConfigManager().getMessage("player-not-found"), "player", args[0]));
            return true;
        }

        if (target.equals(player)) {
            TextUtil.send(player, plugin.getConfigManager().getMessage("duel-request-self"));
            return true;
        }

        if (plugin.getDuelSessionManager().isInDuel(player)) {
            TextUtil.send(player, plugin.getConfigManager().getMessage("duel-request-you-in-duel"));
            return true;
        }

        if (plugin.getDuelSessionManager().isInDuel(target)) {
            TextUtil.send(player, TextUtil.replace(
                    plugin.getConfigManager().getMessage("duel-request-in-duel"),
                    "player", target.getName()));
            return true;
        }

        if (plugin.getDuelRequestManager().isOnCooldown(player.getUniqueId())) {
            TextUtil.send(player, plugin.getConfigManager().getMessage("duel-request-cooldown"));
            return true;
        }

        if (plugin.getDuelRequestManager().hasPendingRequest(player.getUniqueId(), target.getUniqueId())) {
            TextUtil.send(player, TextUtil.replace(
                    plugin.getConfigManager().getMessage("duel-request-already-sent"),
                    "player", target.getName()));
            return true;
        }

        // Fire cancellable event
        DuelRequestEvent event = new DuelRequestEvent(player, target);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return true;

        // Send request
        if (!plugin.getDuelRequestManager().sendRequest(player, target)) {
            TextUtil.send(player, "&cCould not send duel request.");
            return true;
        }

        // Notify sender
        TextUtil.send(player, TextUtil.replace(
                plugin.getConfigManager().getMessage("duel-request-sent"),
                "player", target.getName()));

        // Notify receiver with clickable buttons
        Component acceptBtn = Component.text("[ACCEPT]")
                .color(NamedTextColor.GREEN)
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/duelaccept " + player.getName()))
                .hoverEvent(HoverEvent.showText(Component.text("Click to accept the duel!")));

        Component rejectBtn = Component.text("[REJECT]")
                .color(NamedTextColor.RED)
                .decorate(TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/dueldeny " + player.getName()))
                .hoverEvent(HoverEvent.showText(Component.text("Click to reject the duel.")));

        Component requestMsg = TextUtil.component(TextUtil.replace(
                plugin.getConfigManager().getMessage("duel-request-received"),
                "player", player.getName()))
                .append(Component.newline())
                .append(acceptBtn)
                .append(Component.text(" "))
                .append(rejectBtn);

        target.sendMessage(requestMsg);
        return true;
    }
}

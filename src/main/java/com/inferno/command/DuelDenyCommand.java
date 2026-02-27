package com.inferno.command;

import com.inferno.Inferno;
import com.inferno.duel.DuelRequest;
import com.inferno.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DuelDenyCommand implements CommandExecutor {

    private final Inferno plugin;

    public DuelDenyCommand(Inferno plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
            return true;
        }

        if (args.length < 1) {
            TextUtil.send(player, "&cUsage: /dueldeny <player>");
            return true;
        }

        Player challenger = plugin.getServer().getPlayerExact(args[0]);
        if (challenger == null || !challenger.isOnline()) {
            TextUtil.send(player, TextUtil.replace(
                    plugin.getConfigManager().getMessage("player-not-found"), "player", args[0]));
            return true;
        }

        DuelRequest request = plugin.getDuelRequestManager()
                .findRequestTo(player.getUniqueId(), challenger.getUniqueId());
        if (request == null) {
            TextUtil.send(player, TextUtil.replace(
                    plugin.getConfigManager().getMessage("duel-request-none"),
                    "player", challenger.getName()));
            return true;
        }

        plugin.getDuelRequestManager().removeRequest(challenger.getUniqueId(), player.getUniqueId());

        TextUtil.send(player, TextUtil.replace(
                plugin.getConfigManager().getMessage("duel-denied"),
                "player", challenger.getName()));

        TextUtil.send(challenger, TextUtil.replace(
                plugin.getConfigManager().getMessage("duel-denied-sender"),
                "player", player.getName()));

        return true;
    }
}

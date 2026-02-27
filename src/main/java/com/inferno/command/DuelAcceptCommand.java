package com.inferno.command;

import com.inferno.Inferno;
import com.inferno.duel.DuelRequest;
import com.inferno.duel.DuelSession;
import com.inferno.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DuelAcceptCommand implements CommandExecutor {

    private final Inferno plugin;

    public DuelAcceptCommand(Inferno plugin) {
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
            TextUtil.send(player, "&cUsage: /duelaccept <player>");
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

        // Remove the request
        plugin.getDuelRequestManager().removeRequest(challenger.getUniqueId(), player.getUniqueId());

        // Notify both
        TextUtil.send(player, TextUtil.replace(
                plugin.getConfigManager().getMessage("duel-accepted"),
                "player", challenger.getName()));

        // Create session and open GUI for both
        DuelSession session = plugin.getDuelSessionManager().createSession(challenger, player);
        plugin.getGUIManager().openDuelSetupGUI(session);

        return true;
    }
}

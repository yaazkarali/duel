package com.inferno.command;

import com.inferno.Inferno;
import com.inferno.stats.PlayerStats;
import com.inferno.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DuelStatsCommand implements CommandExecutor {

    private final Inferno plugin;

    public DuelStatsCommand(Inferno plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("inferno.stats")) {
            if (sender instanceof Player p) TextUtil.send(p, plugin.getConfigManager().getMessage("no-permission"));
            else sender.sendMessage("No permission.");
            return true;
        }

        Player target = null;
        UUID targetUUID = null;
        String targetName = null;

        if (args.length >= 1) {
            target = plugin.getServer().getPlayerExact(args[0]);
            if (target != null) {
                targetUUID = target.getUniqueId();
                targetName = target.getName();
            } else {
                // Try offline player
                @SuppressWarnings("deprecation")
                OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
                if (offline.hasPlayedBefore()) {
                    targetUUID = offline.getUniqueId();
                    targetName = offline.getName() != null ? offline.getName() : args[0];
                } else {
                    if (sender instanceof Player p) {
                        TextUtil.send(p, TextUtil.replace(
                                plugin.getConfigManager().getMessage("player-not-found"),
                                "player", args[0]));
                    }
                    return true;
                }
            }
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Specify a player name.");
                return true;
            }
            target = player;
            targetUUID = player.getUniqueId();
            targetName = player.getName();
        }

        PlayerStats stats = plugin.getStatsManager().getStats(targetUUID);

        // Show GUI if sender is a player and target is online
        if (sender instanceof Player viewer && target != null && target.isOnline()) {
            plugin.getStatsGUI().open(viewer, target);
            return true;
        }

        // Otherwise, text output
        String header = TextUtil.replace(
                plugin.getConfigManager().getMessage("stats-header"), "player", targetName);
        sender.sendMessage(com.inferno.util.ColorUtil.colorString(header));
        sendStatLine(sender, "stats-wins", "wins", String.valueOf(stats.getWins()));
        sendStatLine(sender, "stats-losses", "losses", String.valueOf(stats.getLosses()));
        sendStatLine(sender, "stats-win-streak", "streak", String.valueOf(stats.getWinStreak()));
        sendStatLine(sender, "stats-best-streak", "best", String.valueOf(stats.getBestStreak()));
        sendStatLine(sender, "stats-total-bet-won", "won", String.valueOf(stats.getTotalBetWon()));
        sendStatLine(sender, "stats-total-bet-lost", "lost", String.valueOf(stats.getTotalBetLost()));
        sender.sendMessage(com.inferno.util.ColorUtil.colorString(plugin.getConfigManager().getMessage("stats-footer")));
        return true;
    }

    private void sendStatLine(CommandSender sender, String key, String placeholder, String value) {
        String msg = TextUtil.replace(plugin.getConfigManager().getMessage(key), placeholder, value);
        sender.sendMessage(com.inferno.util.ColorUtil.colorString(msg));
    }
}

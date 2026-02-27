package com.inferno.command;

import com.inferno.Inferno;
import com.inferno.arena.ArenaSetupCommand;
import com.inferno.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DuelAdminCommand implements CommandExecutor {

    private final Inferno plugin;
    private final ArenaSetupCommand arenaSetupCommand;

    public DuelAdminCommand(Inferno plugin) {
        this.plugin = plugin;
        this.arenaSetupCommand = new ArenaSetupCommand(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("inferno.admin")) {
            if (sender instanceof Player player) {
                TextUtil.send(player, plugin.getConfigManager().getMessage("no-permission"));
            } else {
                sender.sendMessage("No permission.");
            }
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("§cUsage: /dueladmin <reload|arena>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.getConfigManager().reload();
                plugin.getKitManager().load();
                plugin.getArenaManager().load();
                sender.sendMessage("§aInferno reloaded successfully.");
            }
            case "arena" -> {
                // Delegate to arena command handler
                String[] newArgs = new String[args.length - 1 + 1];
                newArgs[0] = "arena";
                System.arraycopy(args, 1, newArgs, 1, args.length - 1);
                arenaSetupCommand.onCommand(sender, command, label, newArgs);
            }
            default -> sender.sendMessage("§cUnknown subcommand. Use: /dueladmin <reload|arena>");
        }

        return true;
    }
}

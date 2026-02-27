package com.inferno.arena;

import com.inferno.Inferno;
import com.inferno.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ArenaSetupCommand implements CommandExecutor {

    private final Inferno plugin;

    public ArenaSetupCommand(Inferno plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (!player.hasPermission("inferno.admin")) {
            TextUtil.send(player, plugin.getConfigManager().getMessage("no-permission"));
            return true;
        }

        if (args.length < 2 || !args[0].equalsIgnoreCase("arena")) {
            TextUtil.send(player, plugin.getConfigManager().getMessage("admin-usage"));
            return true;
        }

        String subCmd = args[1].toLowerCase();
        String arenaId = args.length >= 3 ? args[2] : null;
        ArenaManager arenaManager = plugin.getArenaManager();

        switch (subCmd) {
            case "create" -> {
                if (arenaId == null) { TextUtil.send(player, "&cProvide an arena name."); return true; }
                if (arenaManager.getArena(arenaId) != null) {
                    TextUtil.send(player, "&cArena &e" + arenaId + " &calready exists.");
                    return true;
                }
                arenaManager.createArena(arenaId);
                String msg = TextUtil.replace(plugin.getConfigManager().getMessage("admin-arena-created"), "arena", arenaId);
                TextUtil.send(player, msg);
            }
            case "setspawn1" -> {
                if (arenaId == null) { TextUtil.send(player, "&cProvide an arena name."); return true; }
                Arena arena = arenaManager.getArena(arenaId);
                if (arena == null) {
                    TextUtil.send(player, TextUtil.replace(plugin.getConfigManager().getMessage("admin-arena-not-found"), "arena", arenaId));
                    return true;
                }
                arena.setSpawn1(player.getLocation());
                arenaManager.save();
                TextUtil.send(player, TextUtil.replace(TextUtil.replace(
                        plugin.getConfigManager().getMessage("admin-arena-spawn-set"), "num", "1"), "arena", arenaId));
            }
            case "setspawn2" -> {
                if (arenaId == null) { TextUtil.send(player, "&cProvide an arena name."); return true; }
                Arena arena = arenaManager.getArena(arenaId);
                if (arena == null) {
                    TextUtil.send(player, TextUtil.replace(plugin.getConfigManager().getMessage("admin-arena-not-found"), "arena", arenaId));
                    return true;
                }
                arena.setSpawn2(player.getLocation());
                arenaManager.save();
                TextUtil.send(player, TextUtil.replace(TextUtil.replace(
                        plugin.getConfigManager().getMessage("admin-arena-spawn-set"), "num", "2"), "arena", arenaId));
            }
            case "delete" -> {
                if (arenaId == null) { TextUtil.send(player, "&cProvide an arena name."); return true; }
                if (arenaManager.getArena(arenaId) == null) {
                    TextUtil.send(player, TextUtil.replace(plugin.getConfigManager().getMessage("admin-arena-not-found"), "arena", arenaId));
                    return true;
                }
                arenaManager.deleteArena(arenaId);
                TextUtil.send(player, TextUtil.replace(plugin.getConfigManager().getMessage("admin-arena-deleted"), "arena", arenaId));
            }
            case "list" -> {
                TextUtil.send(player, plugin.getConfigManager().getMessage("admin-arena-list-header"));
                for (Arena arena : arenaManager.getArenas()) {
                    String status = arena.isOccupied() ? "&cOccupied" : (arena.isReady() ? "&aReady" : "&eIncomplete");
                    String msg = TextUtil.replace(TextUtil.replace(
                            plugin.getConfigManager().getMessage("admin-arena-list-entry"),
                            "arena", arena.getId()), "status", status);
                    TextUtil.send(player, msg);
                }
            }
            default -> TextUtil.send(player, plugin.getConfigManager().getMessage("admin-usage"));
        }

        return true;
    }
}

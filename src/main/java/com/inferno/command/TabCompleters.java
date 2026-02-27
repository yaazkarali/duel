package com.inferno.command;

import com.inferno.Inferno;
import com.inferno.arena.Arena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleters implements TabCompleter {

    private final Inferno plugin;

    public TabCompleters(Inferno plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        return switch (command.getName().toLowerCase()) {
            case "duel", "duelaccept", "dueldeny" -> tabCompletePlayers(args);
            case "duelstats" -> args.length == 1 ? tabCompletePlayers(args) : List.of();
            case "dueladmin" -> tabCompleteAdmin(args);
            default -> List.of();
        };
    }

    private List<String> tabCompletePlayers(String[] args) {
        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(prefix))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private List<String> tabCompleteAdmin(String[] args) {
        if (args.length == 1) {
            return filterPrefix(args[0], "reload", "arena");
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("arena")) {
            if (args.length == 2) {
                return filterPrefix(args[1], "create", "setspawn1", "setspawn2", "delete", "list");
            }
            if (args.length == 3) {
                // Arena names
                String prefix = args[2].toLowerCase();
                return plugin.getArenaManager().getArenas().stream()
                        .map(Arena::getId)
                        .filter(id -> id.toLowerCase().startsWith(prefix))
                        .collect(Collectors.toList());
            }
        }
        return List.of();
    }

    private List<String> filterPrefix(String input, String... options) {
        String lower = input.toLowerCase();
        return Arrays.stream(options)
                .filter(o -> o.toLowerCase().startsWith(lower))
                .collect(Collectors.toList());
    }
}
